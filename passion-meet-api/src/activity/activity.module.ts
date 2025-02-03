import { forwardRef, Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Activity } from './activity.entity';
import { ActivityController } from './activity.controller';
import { ActivityService } from './activity.service';
import { UserModule } from '../user/user.module';
import { GroupModule } from '../group/group.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([Activity]),
    forwardRef(() => UserModule),
    forwardRef(() => GroupModule),    
  ],
  controllers: [ActivityController],
  providers: [
    ActivityService,
  ],
  exports: [ActivityService],
})
export class ActivityModule {}
