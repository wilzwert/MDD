import { Component, OnInit } from '@angular/core';
import { CurrentUserService } from '../../core/services/current-user.service';
import { User } from '../../core/models/user.interface';
import { Observable, take, tap } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { TopicComponent } from '../topics/list/topic/topic.component';
import { Subscription } from '../../core/models/subscription.interface';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { UpdateUserRequest } from '../../core/models/update-user-request';
import { SessionService } from '../../core/services/session.service';
import { NotificationService } from '../../core/services/notification.service';
import { CurrentUserSubscriptionService } from '../../core/services/current-user-subscription.service';

@Component({
  selector: 'app-me',
  standalone: true,
  imports: [
    TopicComponent, 
    AsyncPipe,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule
  ],
  templateUrl: './me.component.html',
  styleUrl: './me.component.scss'
})
export class MeComponent implements OnInit{
  public user$!: Observable<User>;
  public subscriptions$!: Observable<Subscription[]>;
  public form!: FormGroup;

  constructor(private userService: CurrentUserService, private userSubscriptionService: CurrentUserSubscriptionService, private fb: FormBuilder, private sessionService: SessionService, private notificationService: NotificationService) {}

  public updateCurrentUser() :void {
    this.userService.updateCurrentUser(this.form.value as UpdateUserRequest).pipe(
      take(1)
    ).subscribe((user: User) => {
      this.notificationService.confirmation("Modifications enregistrées");
    });
  }

  public onUnsubscribe(topicId: number) :void {
    this.userSubscriptionService.unSubscribe(topicId).pipe(take(1)).subscribe(() => {
      this.notificationService.confirmation("Désabonnement pris en compte")
    });
  }

  ngOnInit(): void {
    this.user$ = this.userService.getCurrentUser().pipe(
      tap((user: User) =>  {
        this.form = this.fb.group({
          email: [
            user.email, 
            [
              Validators.required,
              Validators.email
            ]
          ],
          username: [
            user.username, 
            [
              Validators.required,
              Validators.min(6)
            ]
          ]
        });
      }
    ));
    this.subscriptions$ = this.userSubscriptionService.getCurrentUserSubscriptions();
  }

  public logout(): void {
    this.sessionService.logOut();
  }
}
