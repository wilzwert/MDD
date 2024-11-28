import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, shareReplay, switchMap } from 'rxjs';
import { User } from '../models/user.interface';
import { Subscription } from '../models/subscription.interface';
import { UpdateUserRequest } from '../models/update-user-request';

@Injectable({
  providedIn: 'root'
})
export class CurrentUserService {

  private apiPath = 'api/user';

  private user$: BehaviorSubject<User | null> | null = null;
  private cachedAt: number = 0;
  private isReloading: boolean = false;
  
  constructor(private httpClient: HttpClient) { }

  private getUserSubject() : BehaviorSubject<User | null> {
    if(this.user$ === null) {
      this.user$ = new BehaviorSubject<User | null>(null);
    }
    return this.user$;
  }

  public shouldReload(): boolean {
    return !this.isReloading && new Date().getTime() - this.cachedAt > environment.serviceCacheMaxAgeMs;
  }

  public getCurrentUser() : Observable<User> {
    
    return this.getUserSubject().pipe(
      switchMap((user: User | null) => {
        if (user && !this.shouldReload()) {
          // send current user if already present
          return of(user);
        } else {
          // load from backend otherwise
          this.isReloading = true;
          return this.httpClient.get<User>(`${this.apiPath}/me`).pipe(
            switchMap((fetchedUser: User) => {
              this.cachedAt = new Date().getTime();
              this.isReloading = false;
              this.getUserSubject().next(fetchedUser); // update BehaviorSubject
              return of(fetchedUser);
            })
          );
        }
      })
    );
  }

  public updateCurrentUser(updateUserRequest: UpdateUserRequest): Observable<User> {
    // load from backend otherwise
    return this.httpClient.put<User>(`${this.apiPath}/me`, updateUserRequest).pipe(
      switchMap((updatedUser: User) => {
        this.cachedAt = new Date().getTime();
        this.isReloading = false;
        this.getUserSubject().next(updatedUser); // reset BehaviorSubject
        return of(updatedUser);
      })
    );
  }
  public clearCache(): void {
    this.user$ = null;
  }
}
