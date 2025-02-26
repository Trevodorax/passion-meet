import { forwardRef, Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Group } from './group.entity';
import { GroupController } from './group.controller';
import { GroupService } from './group.service';
import { PassionModule } from '../passion/passion.module';
import { ActivityModule } from 'src/activity/activity.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([Group]),
    forwardRef(() => ActivityModule),    
    PassionModule
  ],
  controllers: [GroupController],
  providers: [
    GroupService,
  ],
  exports: [GroupService],
})
export class GroupModule {}
