import { Address } from "./Address";

export interface UserProfile {

    id: string

    firstName: string;

    lastName: string;

    addresses: Array<Address>;

}