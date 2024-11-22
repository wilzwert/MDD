import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, shareReplay, switchMap } from 'rxjs';
import { Post } from '../models/post.interface';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class PostService {

  private apiPath:string = '/api/posts';
  private topics$: BehaviorSubject<Post[] |null> = new BehaviorSubject<Post[] | null >(null);

  constructor(private httpClient: HttpClient) { }

  getAllPosts(): Observable<Post[]> {    
    return this.topics$.pipe(
      switchMap((posts: Post[] | null) => {
        if (posts) {
          // send current topics if already present
          return of(posts);
        } else {
          return this.httpClient.get<Post[]>(`${this.apiPath}`).pipe(
            shareReplay(1),
            switchMap((fetchedPosts: Post[]) => {
              this.topics$.next(fetchedPosts); // update BehaviorSubject
              return of(fetchedPosts);
            })
          );
        }
      }));
  }

  getPostById(postId: string): Observable<Post> {
    return this.httpClient.get<Post>(`${this.apiPath}/${postId}`);
  }

  
}
