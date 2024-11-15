import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { User } from '../models/user.interface';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiPath = '/api/user';

  constructor(private httpClient: HttpClient) { }

  public getCurrentUser() : Observable<User>{
    return this.httpClient.get<User>(`${this.apiPath}/me`);
  }

}
