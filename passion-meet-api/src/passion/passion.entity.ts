import { Column, Entity, JoinTable, ManyToMany, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { PassionType } from "./enum/passionType";
import { User } from "../user/user.entity";
import { Group } from "../group/group.entity";

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

    @OneToMany(() => Group, (group) => group.passion)
    groups: Group[];
}
