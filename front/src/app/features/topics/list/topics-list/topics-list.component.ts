import { Component, inject, OnInit } from '@angular/core';
import { TopicService } from '../../../../core/services/topic.service';
import { Observable, of, take, tap } from 'rxjs';
import { Topic } from '../../../../core/models/topic.interface';
import { TopicComponent } from "../topic/topic.component";
import { AsyncPipe } from '@angular/common';
import { animate, style, transition, trigger } from '@angular/animations';
import { Subscription } from '../../../../core/models/subscription.interface';
import { CurrentUserService } from '../../../../core/services/current-user.service';
import { SessionService } from '../../../../core/services/session.service';
import { FilterByTopicPipe } from '../../../../core/pipes/filter-by';
import {MatGridListModule} from '@angular/material/grid-list';
import { NotificationService } from '../../../../core/services/notification.service';
import { CurrentUserSubscriptionService } from '../../../../core/services/current-user-subscription.service';


@Component({
  selector: 'app-topics-list',
  standalone: true,
  imports: [TopicComponent, AsyncPipe, FilterByTopicPipe, MatGridListModule],
  templateUrl: './topics-list.component.html',
  styleUrl: './topics-list.component.scss',
  animations: [
    trigger('fadeOut', [
      transition(':leave', [
        animate('300ms', style({ opacity: 0, transform: 'translateX(50px)' })),
      ]),
    ]),
  ]
})
export class TopicsListComponent implements OnInit {

  // keep a reference on an observable provided by topic service in case topics list is updated (this may happen in the future on topic deletion for example)
  // public topics$: Observable<Topic[]> = this.topicService.topics$;
  // for now we use our own behaviorsubject
  public topics$!: Observable<Topic[]>;
  public subscriptions$!: Observable<Subscription[]>;
  public loggedIn$: Observable<boolean>;

  constructor(private topicService: TopicService, private userSubscriptionService: CurrentUserSubscriptionService, private sessionService:SessionService, private notificationService: NotificationService){
    this.topics$ = this.topicService.getAllTopics();
    this.sessionService = sessionService;
    this.loggedIn$ = sessionService.$isLogged();
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

      // get current user subscriptions only if user is logged in 
      this.loggedIn$.pipe(
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
}