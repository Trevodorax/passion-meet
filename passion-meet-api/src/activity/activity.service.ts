import { BadRequestException, Injectable, NotFoundException, UnauthorizedException, UnprocessableEntityException } from '@nestjs/common';
import { User } from '../user/user.entity';
import { Activity } from './activity.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { CreateActivityDto } from './dto/createActivity.dto';

interface CreatedActivity {
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

@Injectable()
export class ActivityService {
    constructor(
        @InjectRepository(Activity)
        private activityRepository: Repository<Activity>,
    ) {}

    async createActivity(dto: CreateActivityDto): Promise<CreatedActivity> {

        await this.checkValidDates(dto.startDate, dto.endDate)

        const draftActivity = this.activityRepository.create({
            name: dto.name,
            description: dto.description,
            type: dto.type,
            startDate: dto.startDate,
            endDate: dto.endDate,
            location: dto.location,
            maxParticipants: dto.maxParticipants,
            imageUrl: dto.imageUrl,
            createdBy: dto.createdBy
        })

        const savedActivity = await this.activityRepository.save(draftActivity)

        return {
            id: savedActivity.id,
            name: savedActivity.name,
            description: savedActivity.description,
            type: savedActivity.type,
            startDate: savedActivity.startDate,
            endDate: savedActivity.endDate,
            location: savedActivity.location,
            maxParticipants: savedActivity.maxParticipants,
            imageUrl: savedActivity.imageUrl,
            createdBy: savedActivity.createdBy,
            participants: []
        }
    }

    async findOneById(id: string): Promise<Activity | null> {
        return this.activityRepository.findOneBy({id})
    }

    async findOneByName(name: string): Promise<Activity | null> {
        return this.activityRepository.findOneBy({name})
    }

    async findManyByName(name: string): Promise<Activity[] | null> {
        return this.activityRepository.createQueryBuilder('activity')
        .where('activity.name like :name', {name: `%${name}%`})
        .getMany();
    }

    async save(activity: Activity): Promise<Activity> {
        return this.activityRepository.save(activity)
    }

    // Private methods

    async checkValidDates(startDate: Date, endDate: Date): Promise<void> {
        if (startDate >= endDate) {
            throw new UnprocessableEntityException('Start date must be before end date')
        }
    }
}
