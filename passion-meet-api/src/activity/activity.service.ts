import { BadRequestException, forwardRef, Inject, Injectable, NotFoundException, UnauthorizedException, UnprocessableEntityException } from '@nestjs/common';
import { User } from '../user/user.entity';
import { Activity } from './activity.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { CreateActivityDto } from './dto/createActivity.dto';
import { UserService } from '../user/user.service';
import { GroupService } from '../group/group.service';
import { Group } from '../group/group.entity';

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
    group: Group;
    participants: User[];
}

@Injectable()
export class ActivityService {
    constructor(
        @InjectRepository(Activity)
        private activityRepository: Repository<Activity>,
        @Inject(forwardRef(() => UserService))
        private userService: UserService,
        @Inject(forwardRef(() => GroupService))
        private groupService: GroupService
    ) {}

    async createActivity(dto: CreateActivityDto): Promise<CreatedActivity> {

        await this.checkValidDates(dto.startDate, dto.endDate)
        if (dto.maxParticipants < 1) {
            throw new UnprocessableEntityException('Max participants must be at least 1')
        }

        const user = await this.userService.findOneById(dto.createdBy.id)
        const group = await this.groupService.findOneById(dto.group.id)
        if (user === null) {
            throw new NotFoundException('User not found')
        }
        if (group === null) {
            throw new NotFoundException('Group not found')
        }

        const draftActivity = this.activityRepository.create({
            name: dto.name,
            description: dto.description,
            type: dto.type,
            startDate: dto.startDate,
            endDate: dto.endDate,
            location: dto.location,
            maxParticipants: dto.maxParticipants,
            imageUrl: dto.imageUrl,
            createdBy: dto.createdBy,
            group: dto.group
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
            group: savedActivity.group,
            participants: []
        }
    }

    async findOneById(id: string): Promise<Activity | null> {
        const activity = await this.activityRepository.findOne({where: {id: id}, relations: ['participants', 'createdBy']}) 
        return activity
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
