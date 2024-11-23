import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, shareReplay, switchMap, tap } from 'rxjs';
import { Comment } from '../models/comment.interface';
import { CreateCommentRequest } from '../models/create-comment-request.interface';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  private apiPath:string = '/api/posts';
  private comments$: BehaviorSubject<Comment[] |null> = new BehaviorSubject<Comment[] | null >(null);
  private postId!: number;

  constructor(private httpClient:HttpClient) { }

  getPostComments(postId: number): Observable<Comment[]> {
    return this.comments$.pipe(
      switchMap((commments: Comment[] | null) => {
        // we only send current value if we want to get comments for the same post
        if (commments && postId == this.postId) {
          // send current posts if already present
          return of(commments);
        } else {
          this.postId = postId;
          return this.httpClient.get<Comment[]>(`${this.apiPath}/${postId}/comments`).pipe(
            shareReplay(1),
            switchMap((fetchedComments: Comment[]) => {
              this.comments$.next(fetchedComments); // update BehaviorSubject
              return of(fetchedComments);
            })
          );
        }
      }));  
    return this.httpClient.get<Comment[]>(`${this.apiPath}/${postId}/comments`).pipe(
      tap((c: Comment[]) => {console.log(c);this.comments$.next(c); })
    );
    // return this.httpClient.get<Comment[]>(`${this.apiPath}/${postId}/comments`);  
    return this.comments$.pipe(
      switchMap(() => {
        return this.httpClient.get<Comment[]>(`${this.apiPath}/${postId}/comments`)/*.pipe(
          tap((c: Comment[]) => {console.log(c);this.comments$.next(c); })
        )*/;
      })
    );
  }

  createPostComment(postId: number, createCommentRequest: CreateCommentRequest) : Observable<Comment>{
    return this.httpClient.post<Comment>(`${this.apiPath}/${postId}/comments`, createCommentRequest).pipe(
      map((c: Comment) => {
        this.comments$.next(this.comments$.getValue() != null ? [c, ...this.comments$.getValue() as Comment[]] : [c]);
        return c;
      }),
    );
  }
}
