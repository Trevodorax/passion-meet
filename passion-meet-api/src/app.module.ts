import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ConfigModule } from '@nestjs/config';

import { TypeOrmConfigService } from './config/database.config';

import { UserModule } from './user/user.module';
import { JwtModule } from '@nestjs/jwt';
import { PassionModule } from './passion/passion.module';
import { ActivityModule } from './activity/activity.module';
import { GroupModule } from './group/group.module';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: ['.env'],
    }),
    TypeOrmModule.forRootAsync({
      imports: [ConfigModule],
      useClass: TypeOrmConfigService,
    }),
    JwtModule.register({ global: true }),
    UserModule,
    PassionModule,
    ActivityModule,
    GroupModule,
  ],
})
export class AppModule {}
