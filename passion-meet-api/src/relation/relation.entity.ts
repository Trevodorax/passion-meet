import { Column, Entity, ManyToOne, PrimaryGeneratedColumn } from "typeorm";
import { User } from "../user/user.entity";

@Entity()
export class Relation {
    @PrimaryGeneratedColumn('uuid')
    id: string;

    @Column({default: new Date()})
    createdAt: Date

    @Column({default: false})
    isSeen: boolean;

    @ManyToOne(() => User, (user) => user.baseUserRelations)
    baseUser: User;

    @ManyToOne(() => User, (user) => user.userMetRelations)
    userMet: User;
}
