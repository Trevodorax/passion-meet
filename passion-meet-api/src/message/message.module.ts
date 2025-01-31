import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Message } from './message.entity';
import { MessageController } from './message.controller';
import { MessageService } from './message.service';
import { UserModule } from '../user/user.module';
import { GroupModule } from '../group/group.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([Message]),
    UserModule,
    GroupModule
  ],
  controllers: [MessageController],
  providers: [
    MessageService,
  ],
  exports: [MessageService],
})
export class MessageModule {}
