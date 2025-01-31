import { on } from "events";
import { Activity } from "../activity/activity.entity";
import { Passion } from "../passion/passion.entity";
import { Column, Entity, ManyToMany, OneToMany, PrimaryGeneratedColumn } from "typeorm";

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
}
