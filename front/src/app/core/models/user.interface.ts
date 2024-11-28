import { Subscription } from "./subscription.interface";

export interface User {
  id: number;
  email: string;
  username: string;
  createdAt: string;
  updatedAt?: string;
}
