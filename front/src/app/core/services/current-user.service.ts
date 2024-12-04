import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, switchMap } from 'rxjs';
import { User } from '../models/user.interface';
import { UpdateUserRequest } from '../models/update-user-request';
import { DataService } from './data.service';
import { SessionCacheableService } from './session-cacheable-service';
import { SessionService } from './session.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentUserService extends SessionCacheableService<User> {

  private apiPath = 'user';
  
  constructor(private dataService: DataService, protected override sessionService: SessionService) { 
    super(sessionService);
  }

  protected override initCacheSubject() : BehaviorSubject<User | null> {
    return new BehaviorSubject<User | null>(null);
  }

  public getCurrentUser() : Observable<User> {
    
    return this.getCacheSubject().pipe(
      switchMap((user: User | null) => {
        if (user && !this.shouldReload()) {
          // send current user if already present
          return of(user);
        } else {
          // load from backend otherwise
          this.setReloading();
          return this.dataService.get<User>(`${this.apiPath}/me`).pipe(
            switchMap((fetchedUser: User) => {
              this.setCached();
              this.getCacheSubject().next(fetchedUser); // update BehaviorSubject
              return of(fetchedUser);
            })
          );
        }
      })
    );
  }

  public updateCurrentUser(updateUserRequest: UpdateUserRequest): Observable<User> {
    // load from backend otherwise
    return this.dataService.put<User>(`${this.apiPath}/me`, updateUserRequest).pipe(
      switchMap((updatedUser: User) => {
        this.setCached();
        this.getCacheSubject().next(updatedUser); // reset BehaviorSubject
        return of(updatedUser);
      })
    );
  }
  
}
