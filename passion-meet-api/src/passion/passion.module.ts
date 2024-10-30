import { Module } from '@nestjs/common';
import { PassionService } from './passion.service';
import { PassionController } from './passion.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Passion } from './passion.entity';
import { UserModule } from '../user/user.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([Passion]),
  ],
  controllers: [PassionController],
  providers: [
    PassionService,
  ],
  exports: [PassionService],
})
export class PassionModule {}
