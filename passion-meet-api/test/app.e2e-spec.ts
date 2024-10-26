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

  describe('/user/login (POST)', () => {
    it('should send an error if user logs in with wroongly formatted request', () => {
      return request(app.getHttpServer())
        .post('/user/login')
        .send({
          bull: 'shit'
        })
        .expect(400)
    })

    it('should send a 404 if the email is not found', () => {
      return request(app.getHttpServer())
        .post('/user/login')
        .send({
          email: 'notexisting@gmail.com',
          password: 'verywrong'
        })
        .expect(404)
        .expect((res) => {
          expect(res.body.message).toBe('USER_WITH_EMAIL_NOT_FOUND')
        })
    })

    it('should send a 401 if the password does not match', async () => {
      await givenUserExists({
        email: 'email@gmail.com',
        password: 'password',
        username: 'user'
      })

      return request(app.getHttpServer())
        .post('/user/login')
        .send({
          email: 'email@gmail.com',
          password: 'wrongpassword'
        })
        .expect(401)
        .expect((res) => {
          expect(res.body.message).toBe('WRONG_PASSWORD')
        })
    })

    it('should send a token when everything is fine', async () => {
      await givenUserExists({
        email: 'email@gmail.com',
        password: 'password',
        username: 'user'
      })

      return request(app.getHttpServer())
        .post('/user/login')
        .send({
          email: 'email@gmail.com',
          password: 'password'
        })
        .expect(201)
        .expect((res) => {
          expect(res.body.token).toBeTruthy()
        })
    })
  })

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
