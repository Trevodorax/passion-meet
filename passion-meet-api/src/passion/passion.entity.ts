import { Column, Entity, JoinTable, ManyToMany, PrimaryGeneratedColumn } from "typeorm";
import { PassionType } from "./enum/passionType";
import { User } from "../user/user.entity";

@Entity()
export class Passion {
    @PrimaryGeneratedColumn('uuid')
    id: string;

    @Column({unique: true})
    name: string;

    @Column()
    description: string;

    @Column()
    picture: string;

    @Column()
    type: PassionType

    @ManyToMany(() => User, (user) => user.passions)
    @JoinTable()
    users: User[];
}
