import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, switchMap } from 'rxjs';
import { Topic } from '../models/topic.interface';
import { DataService } from './data.service';
import { SessionCacheableService } from './session-cacheable-service';
import { SessionService } from './session.service';

@Injectable({
  providedIn: 'root'
})
/**
   * Handles topics loading and caching through a BehaviorSubject
  */
export class TopicService extends SessionCacheableService<Topic[]> {
  private apiPath = 'topics';
  
  constructor(private dataService: DataService, protected override sessionService: SessionService) { 
    super(sessionService);
  }

  protected override initCacheSubject() : BehaviorSubject<Topic[] | null > {
    return new BehaviorSubject<Topic[] | null>(null);
  }

  /**
   * Retrieves the topics after reloading them from the backend if needed
   * @returns the topics 
   */
  getAllTopics(): Observable<Topic[]> {    
    return this.getCacheSubject().pipe(
      switchMap((topics: Topic[] | null) => {
        if (topics && !this.shouldReload()) {
          // send current topics if already present
          return of(topics);
        } else {
          this.setReloading();
          // fetch from data service
          return this.dataService.get<Topic[]>(`${this.apiPath}`).pipe(
            switchMap((fetchedTopics: Topic[]) => {
              this.setCached();
              this.getCacheSubject().next(fetchedTopics); // update BehaviorSubject
              return of(fetchedTopics);
            })
          );
        }
      }));
  }
}