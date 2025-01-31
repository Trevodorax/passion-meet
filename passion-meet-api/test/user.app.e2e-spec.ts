import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from './../src/app.module';
import { DataSource } from 'typeorm';
import { User } from '../src/user/user.entity';
import { PassionType } from '../src/passion/enum/passionType';
import { Passion } from '../src/passion/passion.entity';
import { Activity } from '../src/activity/activity.entity';

describe('AppController (e2e)', () => {
  let app: INestApplication;

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    app.useGlobalPipes(new ValidationPipe())
    await app.init();

    const dataSource = app.get(DataSource);
    await dataSource.createQueryBuilder().delete().from(Passion).execute();
    await dataSource.createQueryBuilder().delete().from(Activity).execute();
    await dataSource.createQueryBuilder().delete().from(User).execute();
  });

  afterEach(async () => {
    const dataSource = app.get(DataSource);
    await dataSource.createQueryBuilder().delete().from(Passion).execute();
    await dataSource.createQueryBuilder().delete().from(Activity).execute();
    await dataSource.createQueryBuilder().delete().from(User).execute();
  });

  const givenUserExists = async (userData: {email: string, password: string, username: string}) => {
    await request(app.getHttpServer())
      .post('/users')
      .send(userData)
      .expect(201);

    return userData;
  };

  const givenUserIsLoggedIn = async (userData: {email: string, password: string, username: string}) => {
    await givenUserExists(userData)

    const response = await request(app.getHttpServer())
      .post('/users/login')
      .send({email: userData.email, password: userData.password})
      .expect(201);

    return {
      userData,
      token: response.body.token
    };
  };

  describe('/users/me (GET)', () => {
    it('should send a 401 if the user is not authentified', () => {
      return request(app.getHttpServer())
        .get('/users/me')
        .send()
        .expect(401)
    })

    it('should return the authentified user if there is one', async () => {
      const { token } = await givenUserIsLoggedIn({email: 'email@gmail.com', password: 'password', username: 'user'})
      
      return request(app.getHttpServer())
        .get('/users/me')
        .set('Authorization', `Bearer ${token}`)
        .send()
        .expect(200)
        .expect((res) => {
          expect(res.body.email).toBe('email@gmail.com')
        })
    })
  })

  describe('/users/login (POST)', () => {
    it('should send an error if user logs in with wroongly formatted request', () => {
      return request(app.getHttpServer())
        .post('/users/login')
        .send({
          bull: 'shit'
        })
        .expect(400)
    })

    it('should send a 404 if the email is not found', () => {
      return request(app.getHttpServer())
        .post('/users/login')
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
        .post('/users/login')
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
        .post('/users/login')
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

  describe('/users (POST)', () => {
    it('should create a user with valid data', () => {
      return request(app.getHttpServer())
        .post('/users')
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
        .post('/users')
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
        .post('/users')
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
        .post('/users')
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
        .post('/users')
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
  describe('/users/me/passion (GET)', () => {
    it('should return passion if added to the user', async () => {
      const { token } = await givenUserIsLoggedIn({email: 'email@gmail.com', password: 'password', username: 'user'})
      let passionId: string = "";

      await request(app.getHttpServer())
      .post('/passions')
      .send({
          name: 'pokemon',
          description: 'best passion in the world',
          type: PassionType.GAME,
          picture: 'path/to/picture'
      })
      .expect(201)
      .expect((res) => {
        passionId = res.body.id
      });
      await request(app.getHttpServer())
      .post('/users/me/passions')
      .set('Authorization', `Bearer ${token}`)
      .send({
          "passionId": passionId
      })
      .expect(201)
      await request(app.getHttpServer())
      .get('/users/me/passions')
      .set('Authorization', `Bearer ${token}`)
      .send()
      .expect(200)
      .expect((res) => {
        expect(res.body.passions[0].name).toBe('pokemon')
      })
    });
  });
  describe('/activities (POST)', () => {
    it('should create a new activity if data is valid', async () => {
      let user: User;
      await request(app.getHttpServer())
      .post('/users')
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
        user = res.body
      });
      await request(app.getHttpServer())
      .post('/activities')
      .send({
        name: 'activity',
        description: 'best activity in the world',
        type: 'GAME',
        startDate: new Date('2021-01-01'),
        endDate: new Date('2021-01-02'),
        location: 'somewhere',
        maxParticipants: 10,
        imageUrl: 'path/to/the/picture',
        createdBy: user
      })
      .expect(201)
      .expect(({body}) => {
        expect(body).toEqual({
          id: expect.any(String),
          name: 'activity',
          description: 'best activity in the world',
          type: 'GAME',
          startDate: expect.any(String),
          endDate: expect.any(String),
          location: 'somewhere',
          maxParticipants: 10,
          imageUrl: 'path/to/the/picture',
          createdBy: user,
          participants: []
        })
      });
    });
  });
  describe('users/me/activities (POST)', () => {
    it('user should join and leave a created activity', async () => {
      let user: User;
      let activity: Activity;
      const { token } = await givenUserIsLoggedIn({email: 'email@gmail.com', password: 'password', username: 'user'})
      await request(app.getHttpServer())
      .post('/users')
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
        user = res.body
      });
      await request(app.getHttpServer())
      .post('/activities')
      .send({
        name: 'activity',
        description: 'best activity in the world',
        type: 'GAME',
        startDate: new Date('2030-01-01'),
        endDate: new Date('2030-01-02'),
        location: 'somewhere',
        maxParticipants: 10,
        imageUrl: 'path/to/the/picture',
        createdBy: user
      })
      .expect(201)
      .expect(({body}) => {
        expect(body).toEqual({
          id: expect.any(String),
          name: 'activity',
          description: 'best activity in the world',
          type: 'GAME',
          startDate: expect.any(String),
          endDate: expect.any(String),
          location: 'somewhere',
          maxParticipants: 10,
          imageUrl: 'path/to/the/picture',
          createdBy: user,
          participants: []
        })
        activity = body
      })
      await request(app.getHttpServer())
      .post('/users/me/activities')
      .set('Authorization', `Bearer ${token}`)
      .send({
        activityId: activity.id
      })
      .expect(201)
      await request(app.getHttpServer())
      .get('/activities/' + activity.id + '')
      .send()
      .expect(200)
      .expect(({body}) => {
        console.log(body)
        expect(body.participants).toHaveLength(1)
      });
      await request(app.getHttpServer())
      .delete('/users/me/activities')
      .set('Authorization', `Bearer ${token}`)
      .send({
        activityId: activity.id
      })
      .expect(200)
      await request(app.getHttpServer())
      .get('/activities/${activity.id}')
      .send()
      .expect(200)
      .expect(({body}) => {
        expect(body.participants).toHaveLength(0)
      });
    });
  });
});
