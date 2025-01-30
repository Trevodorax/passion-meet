import { Body, Controller, Post } from '@nestjs/common';
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
    console.log('ActivityController.createActivity', body)  
    const activity = await this.activityService.createActivity(body)

    return {  
      id: activity.id,
      name: activity.name,
      description: activity.description,
      type: activity.type,
      startDate: activity.startDate,
      endDate: activity.endDate,
      location: activity.location,
      maxParticipants: activity.maxParticipants,
      imageUrl: activity.imageUrl,
      createdBy: activity.createdBy,
      participants: activity.participants
    }
  }
}