import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Group } from './group.entity';
import { GroupController } from './group.controller';
import { GroupService } from './group.service';
import { PassionModule } from '../passion/passion.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([Group]),
    PassionModule
  ],
  controllers: [GroupController],
  providers: [
    GroupService,
  ],
  exports: [GroupService],
})
export class GroupModule {}
