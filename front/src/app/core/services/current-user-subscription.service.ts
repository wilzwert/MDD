import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, switchMap } from 'rxjs';
import { Subscription } from '../models/subscription.interface';
import { DataService } from './data.service';
import { SessionCacheableService } from './session-cacheable-service';
import { SessionService } from './session.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentUserSubscriptionService extends SessionCacheableService<Subscription[]> {

  
  constructor(private dataService: DataService, protected override sessionService: SessionService) { 
    super(sessionService);
  }

  protected override initCacheSubject() : BehaviorSubject<Subscription[] | null> {
    return  new BehaviorSubject<Subscription[] | null>(null);
  }

  
  public getCurrentUserSubscriptions() : Observable<Subscription[]>{
    return this.getCacheSubject().pipe(
      switchMap((subscriptions: Subscription[] | null) => {
        if (subscriptions && !this.shouldReload()) {
          // send current subscriptions if already present
          return of(subscriptions);
        } else {
          // load from backend otherwise
          this.setReloading();
          return this.dataService.get<Subscription[]>(`user/me/subscriptions`).pipe(
            switchMap((fetchedSubscriptions: Subscription[]) => {
              this.setCached();
              this.getCacheSubject().next(fetchedSubscriptions); // update BehaviorSubject
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
        if(this.getCacheSubject().getValue() != null) {
          this.getCacheSubject().next([...this.getCacheSubject().getValue() as Subscription[], t]);
        }
        return t;
      }),
    );
  }

  unSubscribe(topicId: number): Observable<null> {
    return this.dataService.delete<null>(`topics/${topicId}/subscription`).pipe(
      map(() => {
        // do not populate local cache when it was empty because next subscriptions retrieval must make an API request
        if(this.getCacheSubject().getValue() != null) {
          this.getCacheSubject().next(this.getCacheSubject().getValue()!.filter(s => s.topic.id != topicId));
        }
        return null;
      }),
    );
  }
}
