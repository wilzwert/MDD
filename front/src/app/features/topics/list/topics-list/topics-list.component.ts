import { Component, EventEmitter, inject, OnInit } from '@angular/core';
import { TopicService } from '../../../../core/services/topic.service';
import { filter, map, Observable, take, tap } from 'rxjs';
import { Topic } from '../../../../core/models/topic.interface';
import { TopicComponent } from "../topic/topic.component";
import { AsyncPipe } from '@angular/common';
import { animate, style, transition, trigger } from '@angular/animations';
import { AppNotification } from '../../../../core/models/app-notification.interface';
import { NotificationService } from '../../../../core/services/notification.service';

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
  public topics$: Observable<Topic[]> = this.topicService.topics$;

  constructor(private notificationService: NotificationService){}

  onTopicDelete(topicId: number) : void {
    this.topicService.delete(topicId).subscribe(() => {
      this.notificationService.handleNotification({type: 'confirmation', message: "Topic has been deleted."} as AppNotification);
    })
  }

  ngOnInit(): void {
    this.topicService.getAll();
  }
}
