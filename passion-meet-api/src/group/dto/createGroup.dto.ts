import { IsDate, IsEmail, IsNotEmpty, IsNumber, IsString, MinLength } from 'class-validator';
import { User } from '../../user/user.entity';
import { Passion } from '../../passion/passion.entity';

export class CreateGroupDto {

    @IsString()
    @IsNotEmpty()
    name: string;
    
    @IsString()
    @IsNotEmpty()
    description: string;

    @IsNotEmpty()
    passion: Passion
    
    @IsString()
    @IsNotEmpty()
    imageUrl: string;

    @IsNotEmpty()
    createdBy: User;
}
