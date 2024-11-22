import { Topic } from "./topic.interface";
import { User } from "./user.interface";

export interface Post {
    id: number,
    title: string,
    content: string,
    createdAt: string,
    updatedAt: string,
    author: User,
    topic: Topic
}