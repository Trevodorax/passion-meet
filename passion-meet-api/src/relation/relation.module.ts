import { forwardRef, Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Relation } from './relation.entity';
import { RelationController } from './relation.controller';
import { RelationService } from './relation.service';
import { UserModule } from '../user/user.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([Relation]),
    forwardRef(() => UserModule),
  ],
  controllers: [RelationController],
  providers: [
    RelationService,
  ],
  exports: [RelationService],
})
export class RelationModule {}
