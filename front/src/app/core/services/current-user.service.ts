import { environment } from '../../../environments/environment';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, switchMap } from 'rxjs';
import { User } from '../models/user.interface';
import { UpdateUserRequest } from '../models/update-user-request';
import { DataService } from './data.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentUserService {

  private apiPath = 'user';

  private user$: BehaviorSubject<User | null> | null = null;
  private cachedAt = 0;
  private isReloading = false;
  
  constructor(private dataService: DataService) { }

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
          return this.dataService.get<User>(`${this.apiPath}/me`).pipe(
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
    return this.dataService.put<User>(`${this.apiPath}/me`, updateUserRequest).pipe(
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
