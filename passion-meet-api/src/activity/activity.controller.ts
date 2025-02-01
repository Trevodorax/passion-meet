import { Body, Controller, Post, Get, Param, NotFoundException } from '@nestjs/common';
import { Public } from '../user/decorators/public.decorator';
import { User } from '../user/user.entity';
import { ActivityService } from './activity.service';
import { CreateActivityDto } from './dto/createActivity.dto';

interface ActivityResponse {
  id: string;
  name: string;
  description: string;
  type: string;
  startDate: Date;
  endDate: Date;
  location: string;
  maxParticipants: number;
  imageUrl: string;
  createdBy: User;
  participants: User[];
}

@Controller('activities')
export class ActivityController {
  constructor(private readonly activityService: ActivityService) {}

  @Public()
  @Post()
  async createActivity(@Body() body: CreateActivityDto): Promise<ActivityResponse> {
    const activity = await this.activityService.createActivity(body)

    return activity
  }

  @Public()
  @Get('/:activityId')
  async getOneActivity(@Param('activityId') activityId: string): Promise<ActivityResponse> {
    const activity = await this.activityService.findOneById(activityId)
    if (activity === null) {
      throw new NotFoundException('ACTIVITY_NOT_FOUND')
    }
    return activity
  }
}