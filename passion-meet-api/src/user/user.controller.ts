import { Body, Controller, Get, Post } from '@nestjs/common';
import { UserService } from './user.service';
import { CreateUserDto } from './dto/CreateUser.dto';
import { LoginDto } from './dto/Login.dto';
import { Public } from './../decorators/public.decorator';

interface UserResponse {
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

  @Public()
  @Post()
  async createUser(@Body() body: CreateUserDto): Promise<UserResponse> {
    const user = await this.userService.createUser(body)

    return {
      id: user.id,
      email: user.email,
      username: user.username
    }
  }

  @Public()
  @Post('login')
  async loginUser(@Body() body: LoginDto): Promise<LoginResponse> {
    const loginResponse = await this.userService.login(body)

    return loginResponse
  }

  @Get('me')
  async getAuthentifiedUser(): Promise<UserResponse> {
    return
  }
}
