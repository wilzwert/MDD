import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, shareReplay, switchMap } from 'rxjs';
import { Post } from '../models/post.interface';
import { HttpClient } from '@angular/common/http';
import { CreatePostRequest } from '../models/createPostRequest.interface';
import { Topic } from '../models/topic.interface';

@Injectable({
  providedIn: 'root'
})
export class PostService {

  private apiPath:string = '/api/posts';
  private posts$: BehaviorSubject<Post[] |null> = new BehaviorSubject<Post[] | null >(null);

  constructor(private httpClient: HttpClient) { }

  getAllPosts(): Observable<Post[]> {    
    return this.posts$.pipe(
      switchMap((posts: Post[] | null) => {
        if (posts) {
          // send current topics if already present
          return of(posts);
        } else {
          return this.httpClient.get<Post[]>(`${this.apiPath}`).pipe(
            shareReplay(1),
            switchMap((fetchedPosts: Post[]) => {
              this.posts$.next(fetchedPosts); // update BehaviorSubject
              return of(fetchedPosts);
            })
          );
        }
      }));
  }

  getPostById(postId: string): Observable<Post> {
    return this.httpClient.get<Post>(`${this.apiPath}/${postId}`);
  }

  createPost(createPostRequest: CreatePostRequest) :Observable<Post> {
    return this.httpClient.post<Post>(`${this.apiPath}`, createPostRequest).pipe(
      map((p: Post) => {
        console.log(this.posts$.getValue() != null ? [p, ...this.posts$.getValue() as Post[]] : [p]);
        this.posts$.next(this.posts$.getValue() != null ? [p, ...this.posts$.getValue() as Post[]] : [p]);
        return p;
      }),
    );
  }

  updatePost(postId:number, createPostRequest: CreatePostRequest) :Observable<Post> {
    return this.httpClient.put<Post>(`${this.apiPath}/${postId}`, createPostRequest).pipe(
      map((updatedPost: Post) => {
        if(this.posts$.getValue()) {
          this.posts$.next(this.posts$.getValue()!.map(p => {return p.id == postId ? updatedPost : p;}));
        }
        else {
          this.posts$.next([updatedPost]);
        }
        return updatedPost;
      }),
    );
  }
}
