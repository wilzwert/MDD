import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, shareReplay, switchMap } from 'rxjs';
import { User } from '../models/user.interface';
import { Subscription } from '../models/subscription.interface';

@Injectable({
  providedIn: 'root'
})
export class CurrentUserService {

  private apiPath = '/api/user';

  private user$: BehaviorSubject<User | null> = new BehaviorSubject<User |null>(null);
  private subscriptions$: BehaviorSubject<Subscription[] |null> = new BehaviorSubject<Subscription[] |null>(null);

  constructor(private httpClient: HttpClient) { }

  public getCurrentUser() : Observable<User>{
    return this.user$.pipe(
      switchMap((user) => {
        if (user) {
          // send current user if already present
          return of(user);
        } else {
          // load from backend otherwise
          return this.httpClient.get<User>(`${this.apiPath}/me`).pipe(
            shareReplay(1), // Share result to avoid simultaneous requests
            switchMap((fetchedUser: User) => {
              this.user$.next(fetchedUser); // update BehaviorSubject
              return of(fetchedUser);
            })
          );
        }
      })
    );
  }

  public getCurrentUserSubscriptions() : Observable<Subscription[]>{
    return this.subscriptions$.pipe(
      switchMap((subscriptions) => {
        if (subscriptions) {
          // send current subscriptions if already present
          return of(subscriptions);
        } else {
          // load from backend otherwise
          return this.httpClient.get<Subscription[]>(`${this.apiPath}/me/subscriptions`).pipe(
            shareReplay(1), // Share result to avoid simultaneous requests
            switchMap((fetchedSubscriptions: Subscription[]) => {
              this.subscriptions$.next(fetchedSubscriptions); // update BehaviorSubject
              return of(fetchedSubscriptions);
            })
          );
        }
      })
    );
  }

  public logout(): void {
    this.user$.next(null);
    this.subscriptions$.next(null);
  }
  
  updateUserProfile(updatedUser: User): void {
    this.user$.next(updatedUser);
  }

}
