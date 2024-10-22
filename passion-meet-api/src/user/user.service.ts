import { Injectable } from '@nestjs/common';
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

    async createUser(dto: CreateUserDto): Promise<CreatedUser> {
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
