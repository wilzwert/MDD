import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, switchMap, tap } from 'rxjs';
import { Post } from '../models/post.interface';
import { CreatePostRequest } from '../models/create-post-request.interface';
import { PostSort } from '../models/post-sort.interface';
import { Subscription } from '../models/subscription.interface';
import { CurrentUserSubscriptionService } from './current-user-subscription.service';
import { DataService } from './data.service';
import { SessionCacheableService } from './session-cacheable-service';
import { SessionService } from './session.service';

@Injectable({
  providedIn: 'root'
})
export class PostService extends SessionCacheableService<Post[]> {

  private apiPath = 'posts';
  private userSubscriptions$: Observable<Subscription[] | null> | null = null;
  
  constructor(private dataService: DataService, private subscriptionService: CurrentUserSubscriptionService, protected override sessionService:SessionService) {
    super(sessionService);
   }

   protected override initCacheSubject(): BehaviorSubject<Post[] | null> {    
     return new BehaviorSubject<Post[] | null>(null);
   }

   protected override getCacheSubject() : BehaviorSubject<Post[] | null> {
    // clear local cache to force reload on further posts retrieval when current user subscriptions get updated
    if(this.userSubscriptions$ === null) {
      this.userSubscriptions$ = this.subscriptionService.getCurrentUserSubscriptions().pipe(tap(() => {     
        if(this.cache$ !== null) {
          this.clearCache();
        }
      }));
      this.userSubscriptions$.subscribe();
    }
    return super.getCacheSubject();
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
    
    // author + post creation date
      return posts.sort((a, b) => {
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

  /**
   * Retrieves the sorted posts after reloading them from the backend if needed
   * @returns the posts 
   */
  getAllPosts(sortData?: PostSort): Observable<Post[]> {
    // map posts to null when no posts present or reloading needed
    return this.getCacheSubject().pipe(
      switchMap((posts: Post[] | null) => {
        if (posts && !this.shouldReload()) {
          // send current posts if already present
          return of(this.sortPosts(posts, sortData));
        } else {
          this.setReloading();
          return this.dataService.get<Post[]>(`${this.apiPath}`).pipe(
            switchMap((fetchedPosts: Post[]) => {
              this.setCached();
              this.getCacheSubject().next(fetchedPosts); // update BehaviorSubject
              return of(fetchedPosts);
            })
          );
        }
      }));
  }

  getPostById(postId: string): Observable<Post> {
    return this.dataService.get<Post>(`${this.apiPath}/${postId}`);
  }

  createPost(createPostRequest: CreatePostRequest) :Observable<Post> {
    return this.dataService.post<Post>(`${this.apiPath}`, createPostRequest).pipe(
      map((p: Post) => {
        if(this.getCacheSubject().getValue() != null) {
          this.getCacheSubject().next([p, ...this.getCacheSubject().getValue() as Post[]]);
        }
        return p;
      }),
    );
  }
}