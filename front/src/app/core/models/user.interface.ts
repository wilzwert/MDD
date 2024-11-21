import { Subscription } from "./subscription.interface";

export interface User {
  id: number;
  email: string;
  userName: string;
  createdAt: Date;
  updatedAt?: Date;
  subscriptions: Subscription[]
}
