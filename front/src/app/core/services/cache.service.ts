import { Injectable } from '@angular/core';
import { CurrentUserService } from './current-user.service';
import { PostService } from './post.service';
import { TopicService } from './topic.service';
import { CurrentUserSubscriptionService } from './current-user-subscription.service';


/*
  Handle clearing cache of services using local caches for user-related data
*/

@Injectable({
  providedIn: 'root'
})
export class CacheService {

  constructor(private postService: PostService, private userService: CurrentUserService, private subscriptionService: CurrentUserSubscriptionService) { }

  public clearCache(): void {
    this.postService.clearCache();
    this.userService.clearCache();
    this.subscriptionService.clearCache();
  }
}
