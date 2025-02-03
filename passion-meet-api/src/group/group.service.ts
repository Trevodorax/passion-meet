import { BadRequestException, Injectable, NotFoundException, UnauthorizedException, UnprocessableEntityException } from '@nestjs/common';
import { User } from '../user/user.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { CreateGroupDto } from './dto/createGroup.dto';
import { Passion } from '../passion/passion.entity';
import { Group } from './group.entity';
import { Activity } from '../activity/activity.entity';

interface CreatedGroup {
    id: string;
    name: string;
    description: string;
    passion: Passion
    imageUrl: string;
    createdBy: User;
    activities: Activity[];
    participants: User[];
}

@Injectable()
export class GroupService {
    constructor(
        @InjectRepository(Group)
        private groupRepository: Repository<Group>,
    ) {}

    async createGroup(dto: CreateGroupDto): Promise<CreatedGroup> {

        const draftGroup = this.groupRepository.create({
            name: dto.name,
            description: dto.description,
            passion: dto.passion,
            imageUrl: dto.imageUrl,
            createdBy: dto.createdBy,
        })

        const savedGroup = await this.groupRepository.save(draftGroup)

        return {
            id: savedGroup.id,
            name: savedGroup.name,
            description: savedGroup.description,
            passion: savedGroup.passion,
            imageUrl: savedGroup.imageUrl,
            createdBy: savedGroup.createdBy,
            activities: [],
            participants: []
        }
    }

    async findOneById(id: string): Promise<Group | null> {
        const group = await this.groupRepository.findOne({where: {id: id}, relations: ['participants']}) 
        return group
    }

    async findOneByName(name: string): Promise<Group | null> {
        return this.groupRepository.findOneBy({name})
    }

    async findAll(): Promise<Group[]> {
        return this.groupRepository.find()
    }

    async findActivitiesByGroupId(id: string): Promise<Group | null> {
        return this.groupRepository.findOne({where: {id: id}, relations: ['activities']})
    }

    async findManyByName(name: string): Promise<Group[] | null> {
        return this.groupRepository.createQueryBuilder('Group')
        .where('Group.name like :name', {name: `%${name}%`})
        .getMany();
    }

    async save(group: Group): Promise<Group> {
        return this.groupRepository.save(group)
    }
}
