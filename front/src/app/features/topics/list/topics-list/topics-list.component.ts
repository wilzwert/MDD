import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { TopicService } from '../../../../core/services/topic.service';
import { Observable, of, Subject, take, takeUntil, tap } from 'rxjs';
import { Topic } from '../../../../core/models/topic.interface';
import { TopicComponent } from "../topic/topic.component";
import { AsyncPipe } from '@angular/common';
import { Subscription } from '../../../../core/models/subscription.interface';
import { SessionService } from '../../../../core/services/session.service';
import { FilterByTopicPipe } from '../../../../core/pipes/filter-by-topic';
import { NotificationService } from '../../../../core/services/notification.service';
import { CurrentUserSubscriptionService } from '../../../../core/services/current-user-subscription.service';


@Component({
  selector: 'app-topics-list',
  standalone: true,
  imports: [TopicComponent, AsyncPipe, FilterByTopicPipe],
  templateUrl: './topics-list.component.html',
  styleUrl: './topics-list.component.scss'
})
export class TopicsListComponent implements OnInit, OnDestroy {

  private destroy$: Subject<boolean> = new Subject<boolean>;

  public topics$!: Observable<Topic[]>;
  public subscriptions$!: Observable<Subscription[]>;
  // keep a reference on an observable provided by SessionService in case user loggedin status changes (this may happen in the future on topic deletion for example)
  public loggedIn$!: Observable<boolean>;

  constructor(private topicService: TopicService, private userSubscriptionService: CurrentUserSubscriptionService, private sessionService:SessionService, private notificationService: NotificationService){
  }
  
    public onSubscribe(topicId: number) :void {
        this.userSubscriptionService.subscribe(topicId).pipe(
          take(1),
          tap(() => this.notificationService.confirmation("Abonnement pris en compte"))
        )
        .subscribe();
    }

    public onUnsubscribe(topicId: number) :void {
      this.userSubscriptionService.unSubscribe(topicId).pipe(
        take(1),
        tap(() => this.notificationService.confirmation("Désabonnement pris en compte"))
      ).subscribe();
    }

    ngOnInit(): void {
      this.topics$ = this.topicService.getAllTopics();
      this.loggedIn$ = this.sessionService.$isLogged();

      // fetch current user subscriptions from backend only if user is acutally logged in 
      // note : for now, user HAS to be loggedin to access topic list
      // but it may change in the future ; for that, see routes and guards configuration
      // both possibilities should be tested anyway
      // note2 : this could be done only by using the isLogged() method of the SessionService
      // but it's safer to actually observe it in case something happens elsewhere that disconnects the user
      this.loggedIn$.pipe(
        takeUntil(this.destroy$),
        tap((v: boolean) => {
          if(v) {
            this.subscriptions$ = this.userSubscriptionService.getCurrentUserSubscriptions();
          }
          else {
            this.subscriptions$ = of([]);
          }
        })
      ).subscribe();
    }

    ngOnDestroy(): void {
      this.destroy$.next(true);
    }
}