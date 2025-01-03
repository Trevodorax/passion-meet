import { BadRequestException, Injectable, NotFoundException, UnauthorizedException, UnprocessableEntityException } from '@nestjs/common';
import { CreateUserDto } from './dto/CreateUser.dto';
import { User } from './user.entity';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import * as bcrypt from 'bcrypt';
import { LoginDto } from './dto/Login.dto';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { Passion } from '../passion/passion.entity';
import { PassionService } from '../passion/passion.service';
import { AddPassionDto } from './dto/addPassion.dto';

interface CreatedUser {
    id: string;
    email: string;
    username: string;
}

@Injectable()
export class UserService {
    constructor(
        @InjectRepository(User)
        private userRepository: Repository<User>,
        private jwtService: JwtService,
        private configService: ConfigService,
        private passionService: PassionService,
    ) {}

    async createUser(dto: CreateUserDto): Promise<CreatedUser> {
        const isEmailAvailable = await this.isEmailAvailable(dto.email)
        if(!isEmailAvailable) {
            throw new BadRequestException('EMAIL_TAKEN')
        }

        const hashedPassword = await this.passwordToHash(dto.password)

        const draftUser = this.userRepository.create({
            email: dto.email,
            password: hashedPassword,
            username: dto.username
        })

        const savedUser = await this.userRepository.save(draftUser)

        return {
            id: savedUser.id,
            email: savedUser.email,
            username: savedUser.username
        }
    }

    async login(dto: LoginDto): Promise<{token: string}> {
        const user = await this.userRepository.findOneBy({email: dto.email})
        if(user === null) {
            throw new NotFoundException('USER_WITH_EMAIL_NOT_FOUND')
        }

        const doesPasswordMatch = await this.doesPasswordMatch(user, dto.password)
        if(!doesPasswordMatch) {
            throw new UnauthorizedException('WRONG_PASSWORD')
        }

        const token = await this.getTokenForUser(user)

        return {
            token: token
        }
    }

    async getAllPassionsForAnUser(userId: string): Promise<{passions: Passion[]}> {
        const user = await this.userRepository.findOne({where: {id: userId}, relations: ['passions']}) 
        return {
            passions: user.passions
        }
    }

    async addPassionToUser(user: User, dto: AddPassionDto): Promise<void> {
        const passion = await this.passionService.findOneById(dto.passionId)
        if (passion === null) {
            console.log('PASSION_NOT_FOUND')
            throw new NotFoundException('PASSION_NOT_FOUND')
        }

        if (user.passions === undefined) {
            user.passions = []
        }
        user.passions.push(passion)
        await this.save(user)
    }

    async findOneById(id: string): Promise<User | null> {
        return this.userRepository.findOneBy({id})
    }

    async save(user: User): Promise<User> {
        return this.userRepository.save(user)
    }

    /* === PRIVATE METHODS === */

    private async passwordToHash(password: string): Promise<string> {
        const saltOrRounds = 10;
        const hash = await bcrypt.hash(password, saltOrRounds);
        return hash;
    }

    private async isEmailAvailable(email: string): Promise<boolean> {
        const count = await this.userRepository.count({ where: { email } });
        return count === 0;
    }

    private async doesPasswordMatch(user: User, triedPassword: string) {
        return bcrypt.compare(triedPassword, user.password)
    }

    private async getTokenForUser(user: User) {
        const payload = this.userToPayload(user)

        return this.jwtService.signAsync(payload, {
            expiresIn: '24h',
            secret: this.configService.get<string>('JWT_SECRET'),
          });
    }

    private userToPayload(user: User) {
        const payload = {
            id: user.id,
        };
    
        return payload;
      }
}
