import { Subscription } from "./subscription.interface";

export interface User {
  id: number;
  email: string;
  userName: string;
  createdAt: string;
  updatedAt?: string;
}
