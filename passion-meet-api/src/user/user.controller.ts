import { Body, Controller, Get, Post } from '@nestjs/common';
import { UserService } from './user.service';
import { CreateUserDto } from './dto/CreateUser.dto';
import { LoginDto } from './dto/Login.dto';
import { Public } from './decorators/public.decorator';
import { User } from './user.entity';
import { GetUser } from './decorators/get-user.decorator';
import { Passion } from 'src/passion/passion.entity';

interface UserResponse {
  id: string;
  email: string;
  username: string;
}

interface LoginResponse {
  token: string
}

interface GetPassionResponse {
  passions: Passion[]
}



@Controller('users')
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
  async getAuthentifiedUser(@GetUser() user: User): Promise<UserResponse> {
    return user;
  }

  @Get('me/passions')
  async getAllPassionsForAnUser(@GetUser() user: User): Promise<GetPassionResponse> {
    return await this.userService.getAllPassionsForAnUser(user.id)
  }

  @Post('me/passions')
  async addPassionToUser(@GetUser() user: User, @Body() passionId: string): Promise<void> {
    await this.userService.addPassionToUser(user, passionId)
  }
}
