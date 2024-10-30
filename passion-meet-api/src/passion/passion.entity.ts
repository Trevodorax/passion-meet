import { Column, Entity, PrimaryGeneratedColumn } from "typeorm";
import { PassionType } from "./enum/passionType";

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
}
