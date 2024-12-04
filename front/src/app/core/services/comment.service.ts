import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, switchMap } from 'rxjs';
import { Comment } from '../models/comment.interface';
import { CreateCommentRequest } from '../models/create-comment-request.interface';
import { DataService } from './data.service';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  private apiPath = 'posts';
  private comments$: BehaviorSubject<Comment[] |null> = new BehaviorSubject<Comment[] | null >(null);
  private postId!: number;

  constructor(private dataService: DataService) { }

  getPostComments(postId: number): Observable<Comment[]> {
    return this.comments$.pipe(
      switchMap((commments: Comment[] | null) => {
        // we only send current value if we want to get comments for the same post
        if (commments && postId == this.postId) {
          // send current posts if already present
          return of(commments);
        } else {
          this.postId = postId;
          return this.dataService.get<Comment[]>(`${this.apiPath}/${postId}/comments`).pipe(
            switchMap((fetchedComments: Comment[]) => {
              this.comments$.next(fetchedComments); // update BehaviorSubject
              return of(fetchedComments);
            })
          );
        }
      }));
  }

  createPostComment(postId: number, createCommentRequest: CreateCommentRequest) : Observable<Comment>{
    return this.dataService.post<Comment>(`${this.apiPath}/${postId}/comments`, createCommentRequest).pipe(
      map((c: Comment) => {
        // we do not check whether postId has changed because all comments are created only from the post detail page
        this.comments$.next(this.comments$.getValue() != null ? [c, ...this.comments$.getValue() as Comment[]] : [c]);
        return c;
      }),
    );
  }
}
