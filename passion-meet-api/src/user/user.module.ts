import { forwardRef, Module } from '@nestjs/common';
import { UserService } from './user.service';
import { UserController } from './user.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from './user.entity';
import { APP_GUARD } from '@nestjs/core';
import { AuthGuard } from './auth.guard';
import { PassionModule } from '../passion/passion.module';
import { ActivityModule } from '../activity/activity.module';
import { GroupModule } from '../group/group.module';
import { RelationModule } from '../relation/relation.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([User]),
    PassionModule,
    ActivityModule,
    GroupModule,
    RelationModule,
  ],
  controllers: [UserController],
  providers: [
    UserService,
    {
      provide: APP_GUARD,
      useClass: AuthGuard,
    },
  ],
  exports: [UserService],
})
export class UserModule {}
