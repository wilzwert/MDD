import { Component, OnInit } from '@angular/core';
import { CurrentUserService } from '../../core/services/current-user.service';
import { User } from '../../core/models/user.interface';
import { Observable } from 'rxjs';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-me',
  standalone: true,
  imports: [AsyncPipe],
  templateUrl: './me.component.html',
  styleUrl: './me.component.scss'
})
export class MeComponent implements OnInit {
  public user$!: Observable<User>;

  constructor(private userService: CurrentUserService) {
    this.user$ = this.userService.getCurrentUser();
  }

  public ngOnInit(): void {
    // this.userService.getCurrentUser().pipe().subscribe((user: User) => {console.log("got ",user);this.user = user});
  }

}
