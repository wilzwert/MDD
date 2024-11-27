import { environment } from '../../../environments/environment';
import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, switchMap } from 'rxjs';
import { Topic } from '../models/topic.interface';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TopicService {

  private apiPath:string = '/api/topics';
  private topics$: BehaviorSubject<Topic[] |null> | null = null;
  private cachedAt: number = 0;

  constructor(private httpClient: HttpClient) { }

  private getTopicsSubject() : BehaviorSubject<Topic[] | null > {
    if(this.topics$ === null) {
      this.topics$ = new BehaviorSubject<Topic[] | null>(null);
    }
    return this.topics$;
  }

  public shouldReload(): boolean {
    return new Date().getTime() - this.cachedAt > environment.serviceCacheMaxAgeMs;
  }

  getAllTopics(): Observable<Topic[]> {    
    return this.getTopicsSubject().pipe(
      // map topics to null no topics present or reloading needed
      map((topics: Topic[] | null) =>  {
        if(topics) {
          if(!this.shouldReload()) {
            return topics;
          }
          // force reload
          return null;
        }
        return topics;
      }),
      switchMap((topics: Topic[] | null) => {
        if (topics) {
          // send current topics if already present
          return of(topics);
        } else {
          // fetch from backend
          return this.httpClient.get<Topic[]>(`${this.apiPath}`).pipe(
            switchMap((fetchedTopics: Topic[]) => {
              this.cachedAt = new Date().getTime();
              this.getTopicsSubject().next(fetchedTopics); // update BehaviorSubject
              return of(fetchedTopics);
            })
          );
        }
      }));
  }

  clearCache(): void {
    this.topics$ = null;
  }
}