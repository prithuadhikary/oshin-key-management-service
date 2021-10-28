import { User } from "./User";

export interface LoadUsersResponse {
    items: Array<User>;
    totalItems: number;
}