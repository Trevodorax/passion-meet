import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from './../src/app.module';
import { DataSource } from 'typeorm';
import { Passion } from '../src/passion/passion.entity';
import { PassionType } from '../src/passion/enum/passionType';

describe('AppController (e2e)', () => {
  let app: INestApplication;

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    app.useGlobalPipes(new ValidationPipe())
    await app.init();
  });

  afterEach(async () => {
    const dataSource = app.get(DataSource);
    await dataSource.createQueryBuilder().delete().from(Passion).execute();
  });

  const givenPassionExists = async (passionData: {name: string, description: string, picture: string, type: PassionType}) => {
    await request(app.getHttpServer())
      .post('/passion')
      .send(passionData)
      .expect(201);

    return passionData;
  };

  describe('/passion (POST)', () => {
    it('should create a new passion if data is valid', () => {
        return request(app.getHttpServer())
            .post('/passion')
            .send({
                name: 'pokemon',
                description: 'best passion in the world',
                picture: 'path/to/the/picture',
                type: PassionType.GAME
            })
            .expect(201)
            .expect(({body}) => {
                expect(body).toEqual({
                    id: expect.any(String),
                    name: 'pokemon',
                    description: 'best passion in the world',
                    picture: 'path/to/the/picture',
                    type: PassionType.GAME
                })
            });
        });
    it('should return a 400 if the name is already taken', async () => {
        await givenPassionExists({
            name: 'pokemon',
            description: 'best passion in the world',
            picture: 'path/to/the/picture',
            type: PassionType.GAME
        });

        return request(app.getHttpServer())
            .post('/passion')
            .send({
                name: 'pokemon',
                description: 'best passion in the world',
                picture: 'path/to/the/picture',
                type: PassionType.GAME
            })
            .expect(400)
            .expect(({body}) => {
                expect(body.message).toEqual('Name for passion is already taken')
            });
        });
    });
    it('should return a 400 if the type is not in the enum', async () => { 
        return request(app.getHttpServer())
            .post('/passion')
            .send({
                name: 'pokemon',
                description: 'best passion in the world',
                picture: 'path/to/the/picture',
                type: 'WRONG_TYPE'
            })
            .expect(400)
            .expect(({body}) => {
                expect(body.message).toEqual('type must be a valid enum value')
        });
    });
    it('should return a 400 if the name is missing', async () => {
        return request(app.getHttpServer())
            .post('/passion')
            .send({
                description: 'best passion in the world',
                picture: 'path/to/the/picture',
                type: PassionType.GAME
            })
            .expect(400)
            .expect(({body}) => {
                expect(body.message).toEqual('name should not be empty')
            });
    });
    it('should return a 400 if the description is missing', async () => {
        return request(app.getHttpServer())
            .post('/passion')
            .send({
                name: 'pokemon',
                picture: 'path/to/the/picture',
                type: PassionType.GAME
            })
            .expect(400)
            .expect(({body}) => {
                expect(body.message).toEqual('description should not be empty')
            });
    });
    it('should return a 400 if the picture is missing', async () => {
        return request(app.getHttpServer())
            .post('/passion')
            .send({
                name: 'pokemon',
                description: 'best passion in the world',
                type: PassionType.GAME
            })
            .expect(400)
            .expect(({body}) => {
                expect(body.message).toEqual('picture should not be empty')
            });
    });
});