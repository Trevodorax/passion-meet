import { Body, Controller, Get, Param, Post } from '@nestjs/common';
import { User } from '../user/user.entity';
import { Group } from '../group/group.entity';
import { CreateMessageDto } from './dto/createMessage.dto';
import { MessageService } from './message.service';
import { GetUser } from '../user/decorators/get-user.decorator';

interface MessageResponse {
  id: string;
  content: string;
  sendedAt: Date;
  createdBy: User;
  group: Group;
}

@Controller('groups/:groupId/messages')
export class MessageController {
  constructor(private readonly messageService: MessageService) {}

  @Post()
  async createMessage(@Body() body: CreateMessageDto): Promise<MessageResponse> {
    const message = await this.messageService.createMessage(body)

    return message
  }

  @Get()
  async getMessages(@GetUser() user: User, @Param('groupId') groupId: string): Promise<MessageResponse[]> {
    const messages = await this.messageService.findAllByGroupId(user, groupId)
    return messages
  }
}