import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, shareReplay, switchMap, tap } from 'rxjs';
import { Post } from '../models/post.interface';
import { HttpClient } from '@angular/common/http';
import { CreatePostRequest } from '../models/create-post-request.interface';
import { Topic } from '../models/topic.interface';
import { User } from '../models/user.interface';
import { PostSort } from '../models/post-sort.interface';

@Injectable({
  providedIn: 'root'
})
export class PostService {

  private apiPath:string = '/api/posts';
  private posts$: BehaviorSubject<Post[] |null> = new BehaviorSubject<Post[] | null >(null);
  
  constructor(private httpClient: HttpClient) { }

  sortPosts(posts: Post[], sortData?: PostSort): Post[] {
    if(!sortData) {
      return posts;
    }
    let sortedPosts: Post[] = posts;

    // author + post creation date
      return sortedPosts.sort((a, b) => {
        let authorOrder, postOrder = 0;
        if(sortData.sortByAuthor) {
          authorOrder = a.author.userName.localeCompare(b.author.userName);
          if(sortData.orderByAuthorAscending === false) {
            authorOrder = (authorOrder === -1 ? 1 : authorOrder === 0 ? 0 : -1);
          }
        }
        if(sortData.sortByPost) {
          postOrder = a.createdAt.localeCompare(b.createdAt);
          if(sortData.orderByPostAscending === false) {
            postOrder = (postOrder === -1 ? 1 : postOrder === 0 ? 0 : -1);
          }
        }
        return authorOrder || postOrder;
      });
  }

  getAllPosts(sortData?: PostSort): Observable<Post[]> {    
    return this.posts$.pipe(
      map((posts: Post[] | null) => {
        if(posts) {
          return (posts ? this.sortPosts(posts, sortData) : posts);
        }
        return posts;
      }),
      switchMap((posts: Post[] | null) => {
        if (posts) {
          // send current posts if already present
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
