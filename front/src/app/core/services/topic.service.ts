import { environment } from '../../../environments/environment';
import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, switchMap } from 'rxjs';
import { Topic } from '../models/topic.interface';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
/**
   * Handles topics loading and caching through a BehaviorSubject
  */
export class TopicService {
  private apiPath:string = 'api/topics';
  private topics$: BehaviorSubject<Topic[] |null> | null = null;
  private cachedAt: number = 0;
  private isReloading: boolean = false;

  constructor(private httpClient: HttpClient) { }

  /**
   * Creates or gets the existing BahaviorSubject used for local caching 
   * @returns the BahaviorSubject 
   */
  private getTopicsSubject() : BehaviorSubject<Topic[] | null > {
    if(this.topics$ === null) {
      this.topics$ = new BehaviorSubject<Topic[] | null>(null);
    }
    return this.topics$;
  }

  /**
   * 
   * @returns true if reloading needed, false otherwise
   */
  public shouldReload(): boolean {
    return !this.isReloading && new Date().getTime() - this.cachedAt > environment.serviceCacheMaxAgeMs;
  }

  /**
   * Retrieves the topics after reloading them from the backend if needed
   * @returns the topics 
   */
  getAllTopics(): Observable<Topic[]> {    
    return this.getTopicsSubject().pipe(
      switchMap((topics: Topic[] | null) => {
        if (topics && !this.shouldReload()) {
          // send current topics if already present
          return of(topics);
        } else {
          this.isReloading = true;
          // fetch from backend
          return this.httpClient.get<Topic[]>(`${this.apiPath}`).pipe(
            switchMap((fetchedTopics: Topic[]) => {
              this.cachedAt = new Date().getTime();
              this.isReloading = false;
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