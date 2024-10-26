import { Body, Controller, Post } from '@nestjs/common';
import { UserService } from './user.service';
import { CreateUserDto } from './dto/CreateUser.dto';
import { LoginDto } from './dto/Login.dto';

interface CreateUserResponse {
  id: string;
  email: string;
  username: string;
}

interface LoginResponse {
  token: string
}

@Controller('user')
export class UserController {
  constructor(private readonly userService: UserService) {
  }

  @Post()
  async createUser(@Body() body: CreateUserDto): Promise<CreateUserResponse> {
    const user = await this.userService.createUser(body)

    return {
      id: user.id,
      email: user.email,
      username: user.username
    }
  }

  @Post('login')
  async loginUser(@Body() body: LoginDto): Promise<LoginResponse> {
    const loginResponse = await this.userService.login(body)

    return loginResponse
  }
}
