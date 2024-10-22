import { Injectable } from '@nestjs/common';
import { CreateUserDto } from './dto/createUser.dto';

interface CreatedUser {
    id: string;
    email: string;
    username: string;
}

@Injectable()
export class UserService {
    createUser(dto: CreateUserDto): CreatedUser {
        return {
            id: 'random-id',
            email: dto.email,
            username: dto.username
        }
    }
}
