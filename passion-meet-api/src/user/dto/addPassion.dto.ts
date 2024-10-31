import { IsNotEmpty, IsString } from 'class-validator';

export class AddPassionDto {

  @IsNotEmpty()
  @IsString()
  passionId: string
  
}
