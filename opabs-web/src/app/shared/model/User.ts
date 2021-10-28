import { Address } from "./Address";

export interface User {

    id: string;

    username: string;

    firstName: string;

    lastName: string;

    enabled: boolean;

    avatar: string;

    dateOfBirth: any;

    gender: string;

    address: Address;
    
}