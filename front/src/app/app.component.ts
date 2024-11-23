import { Component, CUSTOM_ELEMENTS_SCHEMA  } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MenuComponent } from './layout/menu/menu.component';
import { NotificationComponent } from "./layout/notification/notification.component";
import { BackButtonComponent } from "./layout/back-button/back-button.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, MenuComponent, NotificationComponent, BackButtonComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'mdd';

  constructor(
    
  ) {

  }
}
