import { IsDate, IsEmail, IsNotEmpty, IsNumber, IsString, MinLength } from 'class-validator';
import { User } from '../../user/user.entity';
import { Group } from '../../group/group.entity';

export class CreateMessageDto {

    @IsString()
    @IsNotEmpty()
    content: string;

    @IsNotEmpty()
    sendedAt: Date;

    @IsNotEmpty()
    createdBy: User;

    @IsNotEmpty()
    group: Group;
}
