import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, shareReplay, switchMap } from 'rxjs';
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
      switchMap(() => {
        return this.httpClient.get<Comment[]>(`${this.apiPath}/${postId}/comments`);
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
