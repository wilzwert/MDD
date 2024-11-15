import { U } from "@angular/cdk/keycodes";

export interface User {
  id: number;
  email: string;
  userName: string;
  createdAt: Date;
  updatedAt?: Date;
}
