import { BadRequestException, forwardRef, Inject, Injectable, NotFoundException, UnauthorizedException, UnprocessableEntityException } from '@nestjs/common';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { User } from '../user/user.entity';
import { Relation } from './relation.entity';
import { UserService } from '../user/user.service';

interface CreatedRelation {
    baseUser: User;
    userMet: User;
}

@Injectable()
export class RelationService {
    constructor(
        @InjectRepository(Relation)
        private relationRepository: Repository<Relation>,
        @Inject(forwardRef(() => UserService))
        private userService: UserService,
    ) {}

    async createRelation(dto: CreatedRelation): Promise<Relation> {

        const baseUser = await this.userService.findOneById(dto.baseUser.id)
        if (!baseUser) {
            throw new NotFoundException('Base user not found')
        }

        const userMet = await this.userService.findOneById(dto.userMet.id)
        if (!userMet) {
            throw new NotFoundException('User met not found')
        }

        const draftRelation = this.relationRepository.create({
            baseUser: dto.baseUser,
            userMet: dto.userMet,
        })

        const savedRelation = await this.relationRepository.save(draftRelation)

        if (!baseUser.baseUserRelations) {
            baseUser.baseUserRelations = []
        }
        baseUser.baseUserRelations.push(savedRelation)
        await this.userService.save(baseUser)

        if (!userMet.userMetRelations) {
            userMet.userMetRelations = []
        }
        userMet.userMetRelations.push(savedRelation)
        await this.userService.save(userMet)

        return savedRelation
    }

    async markRelationAsSeen(id: string): Promise<Relation> {
        const relation = await this.relationRepository.findOne({where: {id: id}})
        if (!relation) {
            throw new NotFoundException('Relation not found')
        }

        relation.isSeen = true
        await this.relationRepository.save(relation)

        return relation
    }

    async findOneById(id: string): Promise<Relation | null> {
        return this.relationRepository.findOneBy({id})
    }

    async findOneByIdWithUserMet(id: string): Promise<Relation | null> {
        const relation = await this.relationRepository.findOne({where: {id: id}, relations: ['userMet']})
        relation.userMet.password = undefined
        return relation
    }
}
