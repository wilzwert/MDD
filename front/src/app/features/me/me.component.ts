import { Component, OnInit } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { take } from 'rxjs';
import { User } from '../../core/models/user.interface';

@Component({
  selector: 'app-me',
  standalone: true,
  imports: [],
  templateUrl: './me.component.html',
  styleUrl: './me.component.scss'
})
export class MeComponent implements OnInit {
  public user: User | undefined;

  constructor(private userService: UserService) {
  }

  public ngOnInit(): void {
    this.userService.getCurrentUser().pipe(take(1)).subscribe((user: User) => {console.log(user);this.user = user});
  }

}
