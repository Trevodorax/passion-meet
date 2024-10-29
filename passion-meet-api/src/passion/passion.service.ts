import { BadRequestException, Injectable, NotFoundException, UnauthorizedException, UnprocessableEntityException } from '@nestjs/common';
import { CreatePassionDto } from './dto/CreatePassion.dto';
import { Passion } from './passion.entity';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import * as bcrypt from 'bcrypt';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { PassionType } from './enum/passionType';

interface CreatedPassion {
    id: string;
    name: string;
    type: PassionType;
    description: string;
    picture: string;
}

@Injectable()
export class PassionService {
    constructor(
        @InjectRepository(Passion)
        private passionRepository: Repository<Passion>,
    ) {}

    async createPassion(dto: CreatePassionDto): Promise<CreatedPassion> {
        const isNameAvailable = await this.isNameAvailable(dto.name)
        if(!isNameAvailable) {
            throw new BadRequestException('Name for passion is already taken')
        }

        const draftPassion = this.passionRepository.create({
            name: dto.name,
            picture: dto.picture,
            type: dto.type,
            description: dto.description,
        })

        const savedUser = await this.passionRepository.save(draftPassion)

        return {
            id: savedUser.id,
            name: savedUser.name,
            picture: savedUser.picture,
            type: savedUser.type,
            description: savedUser.description,
        }
    }

    async findOneById(id: string): Promise<Passion | null> {
        return this.passionRepository.findOneBy({id})
    }

    async findOneByName(name: string): Promise<Passion | null> {
        return this.passionRepository.findOneBy({name})
    }

    async findManyByName(name: string): Promise<Passion[] | null> {
        return this.passionRepository.createQueryBuilder('passion')
        .where('passion.name like :name', {name: `%${name}%`})
        .getMany();
    }

        /* === PRIVATE METHODS === */
    
        private async isNameAvailable(name: string): Promise<boolean> {
            const count = await this.passionRepository.count({ where: { name } });
            return count === 0;
        }
}
