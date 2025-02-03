import { Group } from "../group/group.entity";
import { User } from "../user/user.entity";
import { Column, Entity, JoinTable, ManyToMany, ManyToOne, PrimaryGeneratedColumn } from "typeorm";

@Entity()
export class Activity {
    @PrimaryGeneratedColumn('uuid')
    id: string;

    @Column()
    name: string;

    @Column()
    description: string;

    @Column()
    type: string;

    @Column()
    startDate: Date;

    @Column()
    endDate: Date;

    @Column()
    location: string;

    @Column()
    maxParticipants: number;

    @Column()
    imageUrl: string;

    @ManyToOne(() => User, (user) => user.createdActivities)
    createdBy: User;

    @ManyToMany(() => User, (user) => user.participatedActivities)
    @JoinTable()
    participants: User[];

    @ManyToOne(() => Group, (group) => group.activities)
    group: Group;
}
