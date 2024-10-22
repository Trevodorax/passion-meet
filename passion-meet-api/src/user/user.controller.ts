import { Body, Controller, Post } from '@nestjs/common';
import { UserService } from './user.service';
import { CreateUserDto } from './dto/createUser.dto';

interface CreateUserBody {
  email: string;
  password: string;
  username: string;
}

interface CreateUserResponse {
  id: string;
  email: string;
  username: string;
}

@Controller('user')
export class UserController {
  constructor(private readonly userService: UserService) {
  }

  @Post()
  async createUser(@Body() body: CreateUserDto): Promise<CreateUserResponse> {
    const user = this.userService.createUser(body)

    console.log(user)

    return {
      id: user.id,
      email: user.email,
      username: user.username
    }
  }
}
