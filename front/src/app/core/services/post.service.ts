import { environment } from '../../../environments/environment';
import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, shareReplay, switchMap, tap } from 'rxjs';
import { Post } from '../models/post.interface';
import { HttpClient } from '@angular/common/http';
import { CreatePostRequest } from '../models/create-post-request.interface';
import { PostSort } from '../models/post-sort.interface';
import { CurrentUserService } from './current-user.service';
import { Subscription } from '../models/subscription.interface';

@Injectable({
  providedIn: 'root'
})
export class PostService {

  private apiPath:string = '/api/posts';
  private posts$: BehaviorSubject<Post[] |null> = new BehaviorSubject<Post[] | null >(null);
  private subscriptions: Subscription[] = [];
  private cachedAt: number = 0;
  
  constructor(private httpClient: HttpClient, private userService: CurrentUserService) {
    this.userService.getCurrentUserSubscriptions().pipe(tap((s) => {
      this.clearCache(); 
      this.subscriptions = s
    })).subscribe();
   }

  clearCache(): void {
    if(this.posts$.getValue() !== null) {
      this.posts$.next(null);
    }
  }

  // filter posts : return only posts in topics subscribed by the current user
  filterPosts(posts: Post[]): Post[] {
    return posts.filter((post: Post) => {
      return this.subscriptions.some((subscription: Subscription) => subscription.topic.id == post.topic.id);
    });
  }

  // sort posts according to sort data
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

        // no need to check if user explicitly asked to sort by post creation, as it must always be used for sorting
        // event when user asked for a sort by author  
        // if(sortData.sortByPost) {
          postOrder = a.createdAt.localeCompare(b.createdAt);
          if(sortData.orderByPostAscending === false) {
            postOrder = (postOrder === -1 ? 1 : postOrder === 0 ? 0 : -1);
          }
        // }
        return authorOrder || postOrder;
      });
  }

  public shouldReload(): boolean {
    return new Date().getTime() - this.cachedAt > environment.postServiceCacheMaxAgeMs;
  }

  getAllPosts(sortData?: PostSort): Observable<Post[]> {    
    return this.posts$.pipe(
      map((posts: Post[] | null) => {
        if(posts) {
          if(!this.shouldReload()) {
            return this.sortPosts(this.filterPosts(posts), sortData);
          }
          // force reload
          return null;
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
              this.cachedAt = new Date().getTime();
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
}
