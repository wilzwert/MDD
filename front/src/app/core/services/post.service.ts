import { environment } from '../../../environments/environment';
import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, shareReplay, switchMap, tap } from 'rxjs';
import { Post } from '../models/post.interface';
import { HttpClient } from '@angular/common/http';
import { CreatePostRequest } from '../models/create-post-request.interface';
import { PostSort } from '../models/post-sort.interface';
import { CurrentUserService } from './current-user.service';
import { Subscription } from '../models/subscription.interface';
import { CurrentUserSubscriptionService } from './current-user-subscription.service';

@Injectable({
  providedIn: 'root'
})
export class PostService {

  private apiPath:string = 'api/posts';
  private posts$: BehaviorSubject<Post[] |null> | null = null;
  private userSubscriptions$: Observable<Subscription[] | null> | null = null;
  private cachedAt: number = 0;
  private isReloading: boolean = false;
  
  constructor(private httpClient: HttpClient, private subscriptionService: CurrentUserSubscriptionService) {
   }

   private getPostsSubject() : BehaviorSubject<Post[] | null> {
    // cache local cache to force reload on further posts retrieval when current user subscriptions get updated
    if(this.userSubscriptions$ === null) {
      this.userSubscriptions$ = this.subscriptionService.getCurrentUserSubscriptions().pipe(tap((s) => {        
        if(this.posts$ !== null) {
          this.clearCache();
        }
      }));
      this.userSubscriptions$.subscribe();
    }

    if(this.posts$ === null) {
      this.posts$ = new BehaviorSubject<Post[] | null>(null);
    }
    return this.posts$;
  }

  clearCache(): void {
    this.posts$ = null;
  }

  /**
   * Sort posts according to sort data
   * @param posts 
   * @param sortData 
   * @returns sorted posts
   */
  sortPosts(posts: Post[], sortData?: PostSort): Post[] {
    if(!sortData) {
      return posts;
    }
    let sortedPosts: Post[] = posts;

    // author + post creation date
      return sortedPosts.sort((a, b) => {
        let authorOrder, postOrder = 0;
        if(sortData.sortByAuthor) {
          authorOrder = a.author.username.localeCompare(b.author.username);
          if(sortData.orderByAuthorAscending === false) {
            authorOrder = (authorOrder === -1 ? 1 : authorOrder === 0 ? 0 : -1);
          }
        }

        // no need to check if user explicitly asked to sort by post creation, as it must always be used for sorting
        // event when user asked for a sort by author  
        // if(sortData.sortByPost) {
          postOrder = a.createdAt.localeCompare(b.createdAt);
          if(sortData.orderByPostAscending === undefined || sortData.orderByPostAscending === false) {
            postOrder = (postOrder === -1 ? 1 : postOrder === 0 ? 0 : -1);
          }
        // }
        return authorOrder || postOrder;
      });
  }

  public shouldReload(): boolean {
    return !this.isReloading && new Date().getTime() - this.cachedAt > environment.serviceCacheMaxAgeMs;
  }

  /**
   * Retrieves the sorted posts after reloading them from the backend if needed
   * @returns the posts 
   */
  getAllPosts(sortData?: PostSort): Observable<Post[]> {
    // map posts to null when no posts present or reloading needed
    return this.getPostsSubject().pipe(
      switchMap((posts: Post[] | null) => {
        if (posts && !this.shouldReload()) {
          // send current posts if already present
          return of(this.sortPosts(posts, sortData));
        } else {
          this.isReloading = true;
          return this.httpClient.get<Post[]>(`${this.apiPath}`).pipe(
            switchMap((fetchedPosts: Post[]) => {
              this.cachedAt = new Date().getTime();
              this.isReloading = false;
              this.getPostsSubject().next(fetchedPosts); // update BehaviorSubject
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
        if(this.getPostsSubject().getValue() != null) {
          this.getPostsSubject().next([p, ...this.getPostsSubject().getValue() as Post[]]);
        }
        return p;
      }),
    );
  }
}
