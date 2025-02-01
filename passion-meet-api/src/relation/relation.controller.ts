import { Body, Controller, Get, Param, Patch, Post } from '@nestjs/common';
import { Public } from '../user/decorators/public.decorator';
import { RelationService } from './relation.service';
import { CreateRelationDto } from './dto/createRelation.dto';
import { User } from '../user/user.entity';

interface RelationResponse {
  id: string;
  createdAt: Date;
  isSeen: boolean;
  userMet: User;
}

@Controller('/users/:userId/relations')
export class RelationController {
  constructor(private readonly relationService: RelationService) {
  }

  @Post()
  async createRelation(@Body() body: CreateRelationDto): Promise<RelationResponse> {
    const relation = await this.relationService.createRelation(body)
    const otherWayRelation = await this.relationService.createRelation({
      baseUser: body.userMet,
      userMet: body.baseUser
    })

    return {
      id: relation.id,
      createdAt: relation.createdAt,
      isSeen: relation.isSeen,
      userMet: relation.userMet
    }
  }

  @Patch(':relationId')
  async markRelationAsSeen(@Param('relationId') id: string): Promise<RelationResponse> {
    const relation = await this.relationService.markRelationAsSeen(id)

    return {
      id: relation.id,
      createdAt: relation.createdAt,
      isSeen: relation.isSeen,
      userMet: relation.userMet
    }
  }
}