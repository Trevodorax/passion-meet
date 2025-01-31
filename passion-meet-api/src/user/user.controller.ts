import { Body, Controller, Delete, Get, Post } from '@nestjs/common';
import { UserService } from './user.service';
import { CreateUserDto } from './dto/createUser.dto';
import { LoginDto } from './dto/Login.dto';
import { Public } from './decorators/public.decorator';
import { User } from './user.entity';
import { GetUser } from './decorators/get-user.decorator';
import { Passion } from '../passion/passion.entity';
import { AddPassionDto } from './dto/addPassion.dto';

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
  async addPassionToUser(@GetUser() user: User, @Body() body: AddPassionDto): Promise<void> {
    await this.userService.addPassionToUser(user, body)
  }

  @Post('me/activities')
  async joinActivity(@GetUser() user: User, @Body() body: {activityId: string}): Promise<void> {
    await this.userService.joinActivity(user, body.activityId)
  }

  @Delete('me/activities')
  async leaveActivity(@GetUser() user: User, @Body() body: {activityId: string}): Promise<void> {
    await this.userService.leaveActivity(user, body.activityId)
  }

  @Post('me/groups')
  async joinGroup(@GetUser() user: User, @Body() body: {groupId: string}): Promise<void> {
    await this.userService.joinGroup(user, body.groupId)
  }

  @Delete('me/groups')
  async leaveGroup(@GetUser() user: User, @Body() body: {groupId: string}): Promise<void> {
    await this.userService.leaveGroup(user, body.groupId)
  }

  @Post('me/groups/messages')
  async sendMessageToGroup(@GetUser() user: User, @Body() body: {groupId: string}): Promise<void> {
    await this.userService.joinGroup(user, body.groupId)
  }

}
