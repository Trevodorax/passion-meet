import { IsEnum, IsNotEmpty, IsString } from 'class-validator';
import { PassionType } from '../enum/passionType';

export class CreatePassionDto {

    @IsNotEmpty()
    @IsString()
    name: string;

    @IsNotEmpty()
    @IsEnum(PassionType)
    type: PassionType;

    @IsNotEmpty()
    @IsString()
    picture: string;

    @IsNotEmpty()
    @IsString()
    description: string;
}
