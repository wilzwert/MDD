import { User } from "./user.interface";

export interface Comment {
    id: number,
    author: User,
    postId: number,
    content: string,
    createdAt: string,
    updatedAt: string
}