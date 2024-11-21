import { Topic } from "./topic.interface";

export interface Subscription {
    userId: number;
    createdAt: string;
    topic: Topic
}