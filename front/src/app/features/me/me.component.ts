import { Component, OnInit } from '@angular/core';
import { CurrentUserService } from '../../core/services/current-user.service';
import { User } from '../../core/models/user.interface';
import { BehaviorSubject, map, Observable, tap } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { TopicComponent } from '../topics/list/topic/topic.component';
import { Subscription } from '../../core/models/subscription.interface';
import { MatGridListModule } from '@angular/material/grid-list';

@Component({
  selector: 'app-me',
  standalone: true,
  imports: [TopicComponent, AsyncPipe, MatGridListModule],
  templateUrl: './me.component.html',
  styleUrl: './me.component.scss'
})
export class MeComponent {
  public cols!:number;
  public user$!: Observable<User>;
  public subscriptions$!: Observable<Subscription[]>;

  constructor(private userService: CurrentUserService) {
    this.user$ = this.userService.getCurrentUser();
    this.subscriptions$ = this.userService.getCurrentUserSubscriptions();
  }

  public onUnsubscribe(topicId: number) :void {
    this.userService.unSubscribe(topicId).subscribe();
  }

  onResize(event: any) {
    this.cols = (event.target.innerWidth <= 400) ? 1 : 2;
  }

  ngOnInit(): void {
    this.cols = (window.innerWidth <= 400) ? 1 : 2;
  }
}
