import { Address } from "./Address";

export interface AddUserRequest {
    username: string;
    password: string;
    firstName: string;
    lastName: string;
    address: Address;
    avatar: string;
}