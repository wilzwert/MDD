import { environment } from '../../../environments/environment';
import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, switchMap } from 'rxjs';
import { Subscription } from '../models/subscription.interface';
import { DataService } from './data.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentUserSubscriptionService {

  private subscriptions$: BehaviorSubject<Subscription[] |null> | null = null;
  private cachedAt = 0;
  private isReloading = false;
  
  constructor(private dataService: DataService) { }

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
          this.isReloading = true;
          return this.dataService.get<Subscription[]>(`user/me/subscriptions`).pipe(
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
    return this.dataService.post<Subscription>(`topics/${topicId}/subscription`, '').pipe(
      map((t: Subscription) => {
        // do not populate local cache when it was empty because next subscriptions retrieval must make an API request
        if(this.getSubscriptionSubject().getValue() != null) {
          this.getSubscriptionSubject().next([...this.getSubscriptionSubject().getValue() as Subscription[], t]);
        }
        return t;
      }),
    );
  }

  unSubscribe(topicId: number): Observable<null> {
    return this.dataService.delete<null>(`topics/${topicId}/subscription`).pipe(
      map(() => {
        // do not populate local cache when it was empty because next subscriptions retrieval must make an API request
        if(this.getSubscriptionSubject().getValue() != null) {
          this.getSubscriptionSubject().next(this.getSubscriptionSubject().getValue()!.filter(s => s.topic.id != topicId));
        }
        return null;
      }),
    );
  }

  public clearCache(): void {
    this.subscriptions$ = null;
  }
}
