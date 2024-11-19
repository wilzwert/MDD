import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, of, tap, throwError } from 'rxjs';
import { Topic } from '../models/topic.interface';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TopicService {

  private apiPath:string = '/api/topics';
  private topicsSubject = new BehaviorSubject<Topic[]>([]);
  topics$: Observable<Topic[]> = this.topicsSubject.asObservable();

  constructor(private httpClient: HttpClient) { }

  getAll(): void {    
      this.httpClient.get<Topic[]>(this.apiPath).pipe(
        catchError((error) => {
          console.error('Erreur lors de la récupération des topics', error);
          return throwError(() => new Error('Erreur lors de la récupération des topics'))
        })
      ).subscribe((topics: Topic[]) => {
        this.topicsSubject.next(topics);
      })
  }

  delete(topicId: number): Observable<any> {
    return this.httpClient.delete(`${this.apiPath}/${topicId}`).pipe(
      tap(() => {
        const updatedTopics = this.topicsSubject.getValue().filter((t: Topic) => t.id != topicId);
        this.topicsSubject.next(updatedTopics);
      }
    ),
      catchError((error) => {
        console.error('Erreur lors de la suppression du topic', error);
        return throwError(() => new Error('Erreur lors de la suppression du topic'));
      }
    ));
  }
}
