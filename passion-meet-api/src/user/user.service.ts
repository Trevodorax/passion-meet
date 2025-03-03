import { BadRequestException, Injectable, NotFoundException, UnauthorizedException, UnprocessableEntityException } from '@nestjs/common';
import { CreateUserDto } from './dto/createUser.dto';
import { User } from './user.entity';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import * as bcrypt from 'bcrypt';
import { LoginDto } from './dto/Login.dto';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { Passion } from '../passion/passion.entity';
import { PassionService } from '../passion/passion.service';
import { AddPassionDto } from './dto/addPassion.dto';
import { ActivityService } from '../activity/activity.service';
import { GroupService } from '../group/group.service';
import { Relation } from '../relation/relation.entity';
import { RelationService } from '../relation/relation.service';
import { Activity } from '../activity/activity.entity';
import { Group } from '../group/group.entity';

interface CreatedUser {
    id: string;
    email: string;
    username: string;
}

@Injectable()
export class UserService {
    constructor(
        @InjectRepository(User)
        private userRepository: Repository<User>,
        private jwtService: JwtService,
        private configService: ConfigService,
        private passionService: PassionService,
        private activityService: ActivityService,
        private groupService: GroupService,
        private relationService: RelationService
    ) {}

    async createUser(dto: CreateUserDto): Promise<CreatedUser> {
        const isEmailAvailable = await this.isEmailAvailable(dto.email)
        if(!isEmailAvailable) {
            throw new BadRequestException('EMAIL_TAKEN')
        }

        const hashedPassword = await this.passwordToHash(dto.password)

        const draftUser = this.userRepository.create({
            email: dto.email,
            password: hashedPassword,
            username: dto.username
        })

        const savedUser = await this.userRepository.save(draftUser)

        return {
            id: savedUser.id,
            email: savedUser.email,
            username: savedUser.username
        }
    }

    async login(dto: LoginDto): Promise<{token: string}> {
        const user = await this.userRepository.findOneBy({email: dto.email})
        if(user === null) {
            throw new NotFoundException('USER_WITH_EMAIL_NOT_FOUND')
        }

        const doesPasswordMatch = await this.doesPasswordMatch(user, dto.password)
        if(!doesPasswordMatch) {
            throw new UnauthorizedException('WRONG_PASSWORD')
        }

        const token = await this.getTokenForUser(user)

        return {
            token: token
        }
    }

    async getAllPassionsForAnUser(userId: string): Promise<Passion[]> {
        const user = await this.userRepository.findOne({where: {id: userId}, relations: ['passions']}) 
        return user.passions
    }

    async getActivitiesForUser(userId: string): Promise<Activity[]> {
        const user = await this.userRepository.findOne({where: {id: userId}, relations: ['participatedActivities']})
        return user.participatedActivities
    }

    async getGroupsForUser(userId: string): Promise<Group[]> {
        const user = await this.userRepository.findOne({where: {id: userId}, relations: ['participatedGroups', 'participatedGroups.participants']})
        return user.participatedGroups
    }

    async addPassionToUser(user: User, dto: AddPassionDto): Promise<void> {
        const passion = await this.passionService.findOneById(dto.passionId)
        if (passion === null) {
            throw new NotFoundException('PASSION_NOT_FOUND')
        }

        if (user.passions === undefined) {
            user.passions = []
        }
        user.passions.push(passion)
        await this.save(user)
    }

    async addMultiplePassionsToUser(user: User, dtos: AddPassionDto[]): Promise<void> {
        for (const dto of dtos) {
            const passion = await this.passionService.findOneById(dto.passionId)
            if (passion === null) {
                throw new NotFoundException('PASSION_NOT_FOUND')
            }

            if (user.passions === undefined) {
                user.passions = []
            }
            user.passions.push(passion)
        }
        await this.save(user)
    }

    async overwritePassionsForUser(user: User, dtos: AddPassionDto[]): Promise<void> {
        user.passions = []
        for (const dto of dtos) {
            const passion = await this.passionService.findOneById(dto.passionId)
            if (passion === null) {
                throw new NotFoundException('PASSION_NOT_FOUND')
            }

            if (user.passions === undefined) {
                user.passions = []
            }
            user.passions.push(passion)
        }
        await this.save(user)
    }
    
    async joinActivity(user: User, activityId: string): Promise<void> {

        const activity = await this.activityService.findOneById(activityId)
        if (activity === null) {
            throw new NotFoundException('ACTIVITY_NOT_FOUND')
        }

        user = await this.userRepository.findOne({where: {id: user.id}, relations: ['participatedActivities']})

        if (activity.participants !== undefined) {
            if (activity.participants.length >= activity.maxParticipants) {
                throw new UnprocessableEntityException('ACTIVITY_FULL')
            }
        }

        if (activity.endDate < new Date()) {
            throw new UnprocessableEntityException('ACTIVITY_ENDED')
        }

        if (user.participatedActivities === undefined) {
            user.participatedActivities = []
        }

        user.participatedActivities.push(activity)
        await this.save(user)
    }

    async leaveActivity(user: User, activityId: string): Promise<void> {
        const activity = await this.activityService.findOneById(activityId)
        if (activity === null) {
            throw new NotFoundException('ACTIVITY_NOT_FOUND')
        }

        user = await this.userRepository.findOne({where: {id: user.id}, relations: ['participatedActivities']})

        if (activity.endDate < new Date()) {
            throw new UnprocessableEntityException('ACTIVITY_ENDED')
        }

        if (user.participatedActivities === undefined) {
            return
        }
        user.participatedActivities = user.participatedActivities.filter(a => a.id !== activity.id)
        await this.save(user)
    }

    async joinGroup(user: User, groupId: string): Promise<void> {

        const group = await this.groupService.findOneById(groupId)
        if (group === null) {
            throw new NotFoundException('GROUP_NOT_FOUND')
        }

        user = await this.userRepository.findOne({where: {id: user.id}, relations: ['participatedGroups']})

        if (user.participatedGroups === undefined) {
            user.participatedGroups = []
        }

        user.participatedGroups.push(group)
        await this.save(user)
    }

    async leaveGroup(user: User, groupId: string): Promise<void> {
        const group = await this.groupService.findOneById(groupId)
        if (group === null) {
            throw new NotFoundException('GROUP_NOT_FOUND')
        }

        user = await this.userRepository.findOne({where: {id: user.id}, relations: ['participatedGroups']})

        if (user.participatedGroups === undefined) {
            return
        }
        user.participatedGroups = user.participatedGroups.filter(a => a.id !== group.id)
        await this.save(user)
    }

    async getRelations(user: User): Promise<Relation[]> {
        user = await this.userRepository.findOne({where: {id: user.id}, relations: ['baseUserRelations']})
        let relations = []

        for (let relation of user.baseUserRelations) {
            relations.push(await this.relationService.findOneByIdWithUserMet(relation.id))
        }

        return relations
    }

    async findOneById(id: string): Promise<User | null> {
        return this.userRepository.findOneBy({id})
    }

    async findOneByIdWithRelations(id: string): Promise<User | null> {
        return this.userRepository.findOne({where: {id: id}, relations: ['baseUserRelations', 'userMetRelations']})
    }

    async save(user: User): Promise<User> {
        return this.userRepository.save(user)
    }

    /* === PRIVATE METHODS === */

    private async passwordToHash(password: string): Promise<string> {
        const saltOrRounds = 10;
        const hash = await bcrypt.hash(password, saltOrRounds);
        return hash;
    }

    private async isEmailAvailable(email: string): Promise<boolean> {
        const count = await this.userRepository.count({ where: { email } });
        return count === 0;
    }

    private async doesPasswordMatch(user: User, triedPassword: string) {
        return bcrypt.compare(triedPassword, user.password)
    }

    private async getTokenForUser(user: User) {
        const payload = this.userToPayload(user)

        return this.jwtService.signAsync(payload, {
            expiresIn: '24h',
            secret: this.configService.get<string>('JWT_SECRET'),
          });
    }

    private userToPayload(user: User) {
        const payload = {
            id: user.id,
        };
    
        return payload;
      }
}
