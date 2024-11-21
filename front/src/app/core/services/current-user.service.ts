import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, shareReplay, switchMap, tap } from 'rxjs';
import { User } from '../models/user.interface';
import { Subscription } from '../models/subscription.interface';

@Injectable({
  providedIn: 'root'
})
export class CurrentUserService {

  private apiPath = '/api/user';

  private user$: BehaviorSubject<User | null> = new BehaviorSubject<User |null>(null);
  private userSubscriptions$: BehaviorSubject<Subscription[] |null> = new BehaviorSubject<Subscription[] |null>(null);

  constructor(private httpClient: HttpClient) { }

  public getCurrentUser() : Observable<User>{
    return this.user$.pipe(
      switchMap((user: User | null) => {
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
    return this.userSubscriptions$.pipe(
      switchMap((subscriptions: Subscription[] | null) => {
        if (subscriptions) {
          // send current subscriptions if already present
          return of(subscriptions);
        } else {
          // load from backend otherwise
          return this.httpClient.get<Subscription[]>(`${this.apiPath}/me/subscriptions`).pipe(
            shareReplay(1), // Share result to avoid simultaneous requests
            switchMap((fetchedSubscriptions: Subscription[]) => {
              this.userSubscriptions$.next(fetchedSubscriptions); // update BehaviorSubject
              return of(fetchedSubscriptions);
            })
          );
        }
      })
    );
  }

  subscribe(topicId: number): Observable<Subscription> {
    return this.httpClient.post<Subscription>(`/api/topics/${topicId}/subscription`, '').pipe(
      map((t: Subscription) => {
        this.userSubscriptions$.next(this.userSubscriptions$.getValue() != null ? [...this.userSubscriptions$.getValue() as Subscription[], t] : [t]);
        return t;
      }),
    );
  }

  unSubscribe(topicId: number): Observable<any> {
    return this.httpClient.delete<void>(`/api/topics/${topicId}/subscription`).pipe(
      map(() => {
        this.userSubscriptions$.next(this.userSubscriptions$.getValue() != null ? this.userSubscriptions$.getValue()!.filter(s => s.topic.id != topicId) : []);
        return of(null);
      }),
    );
  }

  public logout(): void {
    this.user$.next(null);
    this.userSubscriptions$.next(null);
  }
  
  updateUserProfile(updatedUser: User): void {
    this.user$.next(updatedUser);
  }

}
