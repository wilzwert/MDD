import { Component } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { SessionService } from '../../core/services/session.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [MatToolbarModule, AsyncPipe],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent {
  constructor(
    private sessionService: SessionService
  ) {

  }

  public $isLogged(): Observable<boolean> {
    return this.sessionService.$isLogged();
  }

  public logout(): void {

  }

}
