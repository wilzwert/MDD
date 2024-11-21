import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, of, tap, throwError } from 'rxjs';
import { Topic } from '../models/topic.interface';
import { HttpClient } from '@angular/common/http';
import { Subscription } from '../models/subscription.interface';

@Injectable({
  providedIn: 'root'
})
export class TopicService {

  private apiPath:string = '/api/topics';
  private topicsSubject = new BehaviorSubject<Topic[]>([]);
  topics$: Observable<Topic[]> = this.topicsSubject.asObservable();

  constructor(private httpClient: HttpClient) { }

  getAllTopics(): BehaviorSubject<Topic[]> {    
    this.httpClient.get<Topic[]>(this.apiPath).pipe(
      tap((topics) =>  this.topicsSubject.next(topics)),
      catchError((error) => {
        console.error('Erreur lors de la récupération des topics', error);
        return throwError(() => new Error('Erreur lors de la récupération des topics'))
      })
    ).subscribe(); 
    /*
    result.subscribe((topics: Topic[]) => {
      this.topicsSubject.next(topics);
    })
    */
    return this.topicsSubject;
}



  getAll(): Observable<Topic[]> {    
      const result: Observable<Topic[]> = this.httpClient.get<Topic[]>(this.apiPath).pipe(
        catchError((error) => {
          console.error('Erreur lors de la récupération des topics', error);
          return throwError(() => new Error('Erreur lors de la récupération des topics'))
        })
      );
      
      result.subscribe((topics: Topic[]) => {
        this.topicsSubject.next(topics);
      })

      return result;
  }

  delete(topicId: number): Observable<any> {
    return this.httpClient.delete(`${this.apiPath}/${topicId}`).pipe(
      tap(() => {
        const updatedTopics = this.topicsSubject.getValue().filter((t: Topic) => t.id != topicId);
        this.topicsSubject.next(updatedTopics);
      }
    ),
      catchError((error) => {
        return throwError(() => new Error('Erreur lors de la suppression du topic'));
      }
    ));
  }

  subscribe(topicId: number): Observable<Subscription> {
    return this.httpClient.post<Subscription>(`${this.apiPath}/${topicId}/subscription`, '');
  }

  unSubscribe(topicId: number): Observable<any> {
    return this.httpClient.delete(`${this.apiPath}/${topicId}/subscription`);
  }
}
