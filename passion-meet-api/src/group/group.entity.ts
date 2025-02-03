import { Activity } from "../activity/activity.entity";
import { Message } from "../message/message.entity";
import { Passion } from "../passion/passion.entity";
import { User } from "../user/user.entity";
import { Column, Entity, JoinTable, ManyToMany, ManyToOne, OneToMany, PrimaryGeneratedColumn } from "typeorm";

@Entity()
export class Group {
    @PrimaryGeneratedColumn('uuid')
    id: string;

    @Column()
    name: string;

    @Column()
    description: string;

    @Column()
    imageUrl: string;

    @ManyToOne(() => Passion, (passion) => passion.groups)
    passion: Passion;

    @ManyToOne(() => User, (user) => user.createdGroups)
    createdBy: User;

    @ManyToMany(() => User, (user) => user.participatedGroups)
    @JoinTable()
    participants: User[];

    @OneToMany(() => Message, (message) => message.createdBy, {onDelete: 'CASCADE'})
    messages: Message[];

    @OneToMany(() => Activity, (activity) => activity.group, {onDelete: 'CASCADE'})
    activities: Activity[];
}
