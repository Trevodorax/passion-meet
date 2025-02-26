import { forwardRef, Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Group } from './group.entity';
import { GroupController } from './group.controller';
import { GroupService } from './group.service';
import { PassionModule } from '../passion/passion.module';
import { ActivityModule } from '../activity/activity.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([Group]),
    forwardRef(() => PassionModule),
    forwardRef(() => ActivityModule)      
  ],
  controllers: [GroupController],
  providers: [
    GroupService,
  ],
  exports: [GroupService],
})
export class GroupModule {}
