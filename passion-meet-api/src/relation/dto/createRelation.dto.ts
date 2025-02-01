import { IsNotEmpty } from 'class-validator';
import { User } from '../../user/user.entity';

export class CreateRelationDto {

    @IsNotEmpty()
    baseUser: User;

    @IsNotEmpty()
    userMet: User;
}
