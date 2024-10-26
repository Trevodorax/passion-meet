import { BadRequestException, Injectable, UnprocessableEntityException } from '@nestjs/common';
import { CreateUserDto } from './dto/createUser.dto';
import { User } from './user.entity';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';

interface CreatedUser {
    id: string;
    email: string;
    username: string;
}

@Injectable()
export class UserService {
    constructor(
        @InjectRepository(User)
        private userRepository: Repository<User>
    ) {}

    private async getUserById(id: string): Promise<User | null> {
        return this.userRepository.findOneBy({id})
    }

    private async isEmailAvailable(email: string): Promise<boolean> {
        const count = await this.userRepository.count({ where: { email } });
        return count === 0;
    }

    async createUser(dto: CreateUserDto): Promise<CreatedUser> {
        const isEmailAvailable = await this.isEmailAvailable(dto.email)
        if(!isEmailAvailable) {
            throw new BadRequestException('EMAIL_TAKEN')
        }
        const draftUser = this.userRepository.create({
            email: dto.email,
            password: dto.password,
            username: dto.username
        })

        const savedUser = await this.userRepository.save(draftUser)

        return {
            id: savedUser.id,
            email: savedUser.email,
            username: savedUser.username
        }
    }
}
