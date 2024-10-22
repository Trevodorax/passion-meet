import { Body, Controller, Post } from '@nestjs/common';
import { UserService } from './user.service';
import { CreateUserDto } from './dto/createUser.dto';

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
    const user = await this.userService.createUser(body)

    return {
      id: user.id,
      email: user.email,
      username: user.username
    }
  }
}
