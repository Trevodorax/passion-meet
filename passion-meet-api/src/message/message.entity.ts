import { Group } from "../group/group.entity";
import { User } from "../user/user.entity";
import { Column, Entity, ManyToOne, PrimaryGeneratedColumn } from "typeorm";

@Entity()
export class Message {
    @PrimaryGeneratedColumn('uuid')
    id: string;

    @Column()
    content: string;

    @Column()
    sendedAt: Date;

    @ManyToOne(() => User, (user) => user.messages)
    createdBy: User;

    @ManyToOne(() => Group, (group) => group.messages)
    group: Group;
}
