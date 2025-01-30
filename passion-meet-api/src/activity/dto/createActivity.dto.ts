import { IsDate, IsEmail, IsNotEmpty, IsNumber, IsString, MinLength } from 'class-validator';
import { User } from '../../user/user.entity';

export class CreateActivityDto {

    @IsString()
    @IsNotEmpty()
    name: string;
    
    @IsString()
    @IsNotEmpty()
    description: string;
    
    @IsString()
    @IsNotEmpty()
    type: string;

    @IsNotEmpty()
    startDate: Date;

    @IsNotEmpty()
    endDate: Date;

    @IsString()
    @IsNotEmpty()
    location: string;

    @IsNumber()
    @IsNotEmpty()
    maxParticipants: number;

    @IsString()
    @IsNotEmpty()
    imageUrl: string;

    @IsNotEmpty()
    createdBy: User;
}
