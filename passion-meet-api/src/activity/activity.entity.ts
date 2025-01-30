import { User } from "../user/user.entity";
import { Column, Entity, ManyToMany, ManyToOne, PrimaryGeneratedColumn } from "typeorm";

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
    participants: User[];

}
