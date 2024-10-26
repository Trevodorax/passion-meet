import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from './../src/app.module';
import { DataSource } from 'typeorm';
import { User } from '../src/user/user.entity';

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
    await dataSource.createQueryBuilder().delete().from(User).execute();
  });

  const givenUserExists = async (userData: {email: string, password: string, username: string}) => {
    await request(app.getHttpServer())
      .post('/user')
      .send(userData)
      .expect(201);
    return userData;
  };

  describe('/user (POST)', () => {
    it('should create a user with valid data', () => {
      return request(app.getHttpServer())
        .post('/user')
        .send({
          email: 'test@example.com',
          password: 'password123',
          username: 'testuser',
        })
        .expect(201)
        .expect((res) => {
          expect(typeof res.body.id).toBe('string')
          expect(res.body.email).toBe('test@example.com')
          expect(res.body.username).toBe('testuser')
        });
    });

    it('should return 400 if email is missing', () => {
      return request(app.getHttpServer())
        .post('/user')
        .send({
          password: 'password123',
          username: 'testuser',
        })
        .expect(400)
        .expect((res) => {
          expect(res.body.message).toContain('email should not be empty');
        });
    });

    it('should return 400 if password is too short', () => {
      return request(app.getHttpServer())
        .post('/user')
        .send({
          email: 'test@example.com',
          password: 'short',
          username: 'testuser',
        })
        .expect(400)
        .expect((res) => {
          expect(res.body.message).toContain('password must be longer than or equal to 6 characters');
        });
    });

    it('should return 400 if username is missing', () => {
      return request(app.getHttpServer())
        .post('/user')
        .send({
          email: 'test@example.com',
          password: 'password123',
        })
        .expect(400)
        .expect((res) => {
          expect(res.body.message).toContain('username should not be empty');
        });
    });

    it('should return 400 if the email is already taken', async () => {
      await givenUserExists({
        email: 'test@example.com',
        password: 'password',
        username: 'user',
      });
  
      return request(app.getHttpServer())
        .post('/user')
        .send({
          email: 'test@example.com',
          password: 'password',
          username: 'user',
        })
        .expect(400)
        .expect((res) => {
          expect(res.body.message).toBe('EMAIL_TAKEN')
        });
    });
  });
});
