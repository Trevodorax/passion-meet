import { Body, Controller, Post, Get, Param, NotFoundException } from '@nestjs/common';
import { Public } from '../user/decorators/public.decorator';
import { User } from '../user/user.entity';
import { GroupService } from './group.service';
import { CreateGroupDto } from './dto/createGroup.dto';
import { Passion } from '../passion/passion.entity';

interface GroupResponse {
  id: string;
  name: string;
  description: string;
  passion: Passion
  imageUrl: string;
  createdBy: User;
  participants: User[];
}

@Controller('groups')
export class GroupController {
  constructor(private readonly groupService: GroupService) {}

  @Public()
  @Post()
  async createGroup(@Body() body: CreateGroupDto): Promise<GroupResponse> {
    const group = await this.groupService.createGroup(body)

    return group
  }

  @Public()
  @Get('/:groupId')
  async getOneGroup(@Param('groupId') groupId: string): Promise<GroupResponse> {
    const group = await this.groupService.findOneById(groupId)
    if (group === null) {
      throw new NotFoundException('Group_NOT_FOUND')
    }
    return group
  }
}