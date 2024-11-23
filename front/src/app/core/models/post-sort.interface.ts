import { Post } from "./post.interface";
import { User } from "./user.interface";

export interface PostSort {
    sortByPost: boolean | undefined,
    orderByPostAscending: boolean | undefined,
    sortByAuthor: boolean | undefined,
    orderByAuthorAscending: boolean | undefined
}