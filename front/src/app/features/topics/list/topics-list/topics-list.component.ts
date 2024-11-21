import { Component, inject, OnInit } from '@angular/core';
import { TopicService } from '../../../../core/services/topic.service';
import { BehaviorSubject, combineLatest, forkJoin, map, Observable } from 'rxjs';
import { Topic } from '../../../../core/models/topic.interface';
import { TopicComponent } from "../topic/topic.component";
import { AsyncPipe } from '@angular/common';
import { animate, style, transition, trigger } from '@angular/animations';
import { AppNotification } from '../../../../core/models/app-notification.interface';
import { NotificationService } from '../../../../core/services/notification.service';
import { Subscription } from '../../../../core/models/subscription.interface';
import { CurrentUserService } from '../../../../core/services/current-user.service';
import { ExtendedTopic } from '../../../../core/models/extended-topic.interface';


@Component({
  selector: 'app-topics-list',
  standalone: true,
  imports: [TopicComponent, AsyncPipe],
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
export class TopicsListComponent implements OnInit{
  
  private topicService: TopicService = inject(TopicService);
  // keep a reference on an observable provided by topic service in case topics list is updated (this may happen in the future on topic deletion for example)
  // public topics$: Observable<Topic[]> = this.topicService.topics$;
  // for now we use our own behaviorsubject
  public topics$: BehaviorSubject<ExtendedTopic[]> = new BehaviorSubject<ExtendedTopic[]>([]);

  constructor(private notificationService: NotificationService, private userService: CurrentUserService){}

  /* 
  onTopicDelete(topicId: number) : void {
    this.topicService.delete(topicId).subscribe(() => {
      this.notificationService.handleNotification({type: 'confirmation', message: "Topic has been deleted."} as AppNotification);
    })
  }*/

    onSubscribe(topicId: number) :void {
        this.topicService.subscribe(topicId).subscribe((subscription) => {
          const updated = this.topics$.getValue().map(topic => topic.id === topicId ? { ...topic, subscription: subscription} : topic);
          this.topics$.next(updated);
        });
    }

    onUnsubscribe(topicId: number) :void {
      this.topicService.unSubscribe(topicId).subscribe(() => {
        const updated = this.topics$.getValue().map(topic => topic.id === topicId ? { ...topic, subscription: undefined} : topic);
        this.topics$.next(updated);
      });
  }

  ngOnInit(): void {
    // initial data
    /*
    forkJoin({
      topics: this.topicService.getAll(),
      user: this.userService.getCurrentUser()
    }).subscribe({
      next: (results) => {        
        this.topics$.next(results.topics.map((t: Topic) => ({...t, subscription: results.user.subscriptions.find(s => s.topic.id == t.id)})));
      }
    })*/

    combineLatest({
        topics: this.topicService.getAllTopics(),
        user: this.userService.getCurrentUser()
    }).subscribe({  
      next: (results) => {        
        console.log(results);
        // this.topics$.next(results.topics.map((t: Topic) => ({...t, subscription: results.user.subscriptions.find(s => s.topic.id == t.id)})));
      }
    });
  }
}
