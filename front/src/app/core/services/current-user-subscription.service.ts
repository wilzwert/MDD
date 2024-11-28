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
export class CurrentUserSubscriptionService {

  private apiPath = 'api/user';

  private subscriptions$: BehaviorSubject<Subscription[] |null> | null = null;
  private cachedAt: number = 0;
  private isReloading: boolean = false;
  
  constructor(private httpClient: HttpClient) { }

  private getSubscriptionSubject() : BehaviorSubject<Subscription[] | null> {
    if(this.subscriptions$ === null) {
      this.subscriptions$ = new BehaviorSubject<Subscription[] | null>(null);
    }
    return this.subscriptions$;
  }

  public shouldReload(): boolean {
    return !this.isReloading && new Date().getTime() - this.cachedAt > environment.serviceCacheMaxAgeMs;
  }
  
  public getCurrentUserSubscriptions() : Observable<Subscription[]>{
    return this.getSubscriptionSubject().pipe(
      switchMap((subscriptions: Subscription[] | null) => {
        if (subscriptions && !this.shouldReload()) {
          // send current subscriptions if already present
          return of(subscriptions);
        } else {
          // load from backend otherwise
          return this.httpClient.get<Subscription[]>(`${this.apiPath}/me/subscriptions`).pipe(
            switchMap((fetchedSubscriptions: Subscription[]) => {
              this.cachedAt = new Date().getTime();
              this.isReloading = false;
              this.getSubscriptionSubject().next(fetchedSubscriptions); // update BehaviorSubject
              return of(fetchedSubscriptions);
            })
          );
        }
      })
    );
  }

  subscribe(topicId: number): Observable<Subscription> {
    return this.httpClient.post<Subscription>(`api/topics/${topicId}/subscription`, '').pipe(
      map((t: Subscription) => {
        // do not populate local cache when it was empty because next subscriptions retrieval must make an API request
        if(this.getSubscriptionSubject().getValue() != null) {
          this.getSubscriptionSubject().next([...this.getSubscriptionSubject().getValue() as Subscription[], t]);
        }
        return t;
      }),
    );
  }

  unSubscribe(topicId: number): Observable<any> {
    return this.httpClient.delete<void>(`api/topics/${topicId}/subscription`).pipe(
      map(() => {
        // do not populate local cache when it was empty because next subscriptions retrieval must make an API request
        if(this.getSubscriptionSubject().getValue() != null) {
          this.getSubscriptionSubject().next(this.getSubscriptionSubject().getValue()!.filter(s => s.topic.id != topicId));
        }
        return of(null);
      }),
    );
  }

  public clearCache(): void {
    this.subscriptions$ = null;
  }
}
