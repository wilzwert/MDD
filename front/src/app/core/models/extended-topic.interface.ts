import { Subscription } from "./subscription.interface";
import { Topic } from "./topic.interface";

export interface ExtendedTopic extends Topic {
    subscription: Subscription | undefined;
}