import { Module } from '@nestjs/common';
import { PassionService } from './passion.service';
import { PassionController } from './passion.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Passion } from './passion.entity';
import { APP_GUARD } from '@nestjs/core';
import { AuthGuard } from '../auth.guard';

@Module({
  imports: [
    TypeOrmModule.forFeature([Passion]),
  ],
  controllers: [PassionController],
  providers: [
    PassionService,
    {
      provide: APP_GUARD,
      useClass: AuthGuard,
    },
  ],
})
export class PassionModule {}
