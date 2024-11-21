import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, combineLatest, forkJoin, map, mergeMap, Observable, of, shareReplay, switchMap, tap, throwError } from 'rxjs';
import { Topic } from '../models/topic.interface';
import { HttpClient } from '@angular/common/http';
import { CurrentUserService } from './current-user.service';
import { SessionService } from './session.service';

@Injectable({
  providedIn: 'root'
})
export class TopicService {

  private apiPath:string = '/api/topics';
  private topics$: BehaviorSubject<Topic[] |null> = new BehaviorSubject<Topic[] | null >(null);

  constructor(private httpClient: HttpClient, private sessionService: SessionService, private userService: CurrentUserService) { }

  getAllTopics(): Observable<Topic[]> {    
    return this.topics$.pipe(
      switchMap((topics: Topic[] | null) => {
        if (topics) {
          // send current topics if already present
          return of(topics);
        } else {
          return this.httpClient.get<Topic[]>(`${this.apiPath}`).pipe(
            shareReplay(1),
            switchMap((fetchedTopics: Topic[]) => {
              this.topics$.next(fetchedTopics); // update BehaviorSubject
              return of(fetchedTopics);
            })
          );
        }
      }));
  }

  delete(topicId: number): Observable<any> {
    return this.httpClient.delete(`${this.apiPath}/${topicId}`).pipe(
      tap(() => {
        const updatedTopics = this.topics$.getValue()?.filter((t: Topic) => t.id != topicId);
        this.topics$.next(updatedTopics == undefined ? null : updatedTopics);
      }
    ),
      catchError((error) => {
        return throwError(() => new Error('Unable to delete topic'));
      }
    ));
  }
}