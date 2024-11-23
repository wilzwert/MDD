import { Component, OnInit } from '@angular/core';
import { CurrentUserService } from '../../core/services/current-user.service';
import { User } from '../../core/models/user.interface';
import { BehaviorSubject, map, Observable, take, tap } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { TopicComponent } from '../topics/list/topic/topic.component';
import { Subscription } from '../../core/models/subscription.interface';
import { MatGridListModule } from '@angular/material/grid-list';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { UpdateUserRequest } from '../../core/models/update-user-request';
import { SessionService } from '../../core/services/session.service';
import { SessionInformation } from '../../core/models/sessionInformation.interface';

@Component({
  selector: 'app-me',
  standalone: true,
  imports: [
    TopicComponent, 
    AsyncPipe, 
    MatGridListModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule
  ],
  templateUrl: './me.component.html',
  styleUrl: './me.component.scss'
})
export class MeComponent implements OnInit{
  public cols!:number;
  public user$!: Observable<User>;
  public subscriptions$!: Observable<Subscription[]>;
  public form!: FormGroup;

  constructor(private userService: CurrentUserService, private fb: FormBuilder, private sessionService:SessionService) {
    
  }

  public updateCurrentUser() :void {
    this.userService.updateCurrentUser(this.form.value as UpdateUserRequest).subscribe({
      next: (sessionInfo: SessionInformation) => {
        console.log('new session info with token '+sessionInfo.token);
        this.sessionService.logIn(sessionInfo);
        this.userService.logout();
        console.log('getting new /me');
        this.userService.getCurrentUser().pipe(take(1)).subscribe();
      }
    });
  }

  public onUnsubscribe(topicId: number) :void {
    this.userService.unSubscribe(topicId).subscribe();
  }

  onResize(event: any) {
    this.cols = (event.target.innerWidth <= 400) ? 1 : 2;
  }

  ngOnInit(): void {
    this.cols = (window.innerWidth <= 400) ? 1 : 2;

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
          userName: [
            user.userName, 
            [
              Validators.required,
              Validators.min(6)
            ]
          ]
        });
      }
    ));
    this.subscriptions$ = this.userService.getCurrentUserSubscriptions();

    
    
  }
}
