import { BadRequestException, Injectable, NotFoundException, UnauthorizedException, UnprocessableEntityException } from '@nestjs/common';
import { User } from '../user/user.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { CreateMessageDto } from './dto/createMessage.dto';
import { Passion } from '../passion/passion.entity';
import { Message } from './Message.entity';
import { Group } from '../group/group.entity';
import { UserService } from '../user/user.service';
import { GroupService } from '../group/group.service';

interface CreatedMessage {
    id: string;
    content: string;
    sendedAt: Date;
    createdBy: User;
    group: Group;
}

@Injectable()
export class MessageService {
    constructor(
        @InjectRepository(Message)
        private messageRepository: Repository<Message>,
        private userService: UserService,
        private groupService: GroupService
    ) {}

    async createMessage(dto: CreateMessageDto): Promise<CreatedMessage> {

        const user = await this.userService.findOneById(dto.createdBy.id)
        if (!user) {
            throw new NotFoundException('User not found')
        }

        const group = await this.groupService.findOneById(dto.group.id)
        if (!group) {
            throw new NotFoundException('Group not found')
        }

        if (!group.participants.filter(participant => participant.id === user.id)) {
            throw new UnauthorizedException('User is not a participant of this group')
        }

        const draftMessage = this.messageRepository.create({
            content: dto.content,
            sendedAt: dto.sendedAt,
            createdBy: dto.createdBy,
            group: dto.group
        })

        const savedMessage = await this.messageRepository.save(draftMessage)

        return savedMessage
    }

    async findOneById(id: string): Promise<Message | null> {
        const message = await this.messageRepository.findOne({where: {id: id}}) 
        return message
    }

    async findAllByGroupId(user: User, groupId: string): Promise<Message[] | null> {
        const group = await this.groupService.findOneById(groupId)
        if (!group) {
            throw new NotFoundException('Group not found')
        }

        if (!group.participants.filter(participant => participant.id === user.id)) {
            throw new UnauthorizedException('User is not a participant of this group')
        }

        return this.messageRepository.find({where: {group: group}})
    }

    async save(Message: Message): Promise<Message> {
        return this.messageRepository.save(Message)
    }
}
