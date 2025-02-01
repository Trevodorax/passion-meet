import { on } from "events";
import { Activity } from "../activity/activity.entity";
import { Passion } from "../passion/passion.entity";
import { Column, Entity, ManyToMany, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { Group } from "../group/group.entity";
import { Message } from "../message/message.entity";
import { Relation } from "../relation/relation.entity";

@Entity()
export class User {
    @PrimaryGeneratedColumn('uuid')
    id: string;

    @Column({unique: true})
    email: string;

    @Column()
    password: string;

    @Column()
    username: string;

    @ManyToMany(() => Passion, (passion) => passion.users, {onDelete: 'CASCADE'})
    passions: Passion[];

    @OneToMany(() => Activity, (activity) => activity.createdBy, {onDelete: 'CASCADE'})
    createdActivities: Activity[];

    @ManyToMany(() => Activity, (activity) => activity.participants, {onDelete: 'CASCADE'})
    participatedActivities: Activity[];

    @OneToMany(() => Group, (group) => group.createdBy, {onDelete: 'CASCADE'})
    createdGroups: Group[];

    @ManyToMany(() => Group, (group) => group.participants, {onDelete: 'CASCADE'})
    participatedGroups: Group[];

    @OneToMany(() => Message, (message) => message.createdBy, {onDelete: 'CASCADE'})
    messages: Message[];

    @OneToMany(() => Relation, (relation) => relation.userMet, {onDelete: 'CASCADE'})
    userMetRelations: Relation[];

    @OneToMany(() => Relation, (relation) => relation.baseUser, {onDelete: 'CASCADE'})
    baseUserRelations: Relation[];
}
