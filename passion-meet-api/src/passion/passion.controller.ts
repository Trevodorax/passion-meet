import { Body, Controller, Get, Post } from '@nestjs/common';
import { PassionService } from './passion.service';
import { Public } from './../decorators/public.decorator';
import { CreatePassionDto } from './dto/createPassion.dto';
import { PassionType } from './enum/passionType';

interface PassionResponse {
  id: string;
  name: string;
  picture: string;
  type: PassionType;
  description: string;
}

@Controller('passion')
export class PassionController {
  constructor(private readonly passionService: PassionService) {
  }

  @Public()
  @Post()
  async createPassion(@Body() body: CreatePassionDto): Promise<PassionResponse> {
    const passion = await this.passionService.createPassion(body)

    return {
      id: passion.id,
      name: passion.name,
      picture: passion.picture,
      type: passion.type,
      description: passion.description
    }
  }
}