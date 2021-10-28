import { Address } from "./Address";

export interface UpdateUserRequest {

    firstName: string;

    lastName: string;

    address: Address;

    dateOfBirth: any;

    gender: string;

    avatar: string;

    enabled: boolean;

}