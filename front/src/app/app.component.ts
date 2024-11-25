import { Component, CUSTOM_ELEMENTS_SCHEMA, OnDestroy, OnInit  } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { MenuComponent } from './layout/menu/menu.component';
import { NotificationComponent } from "./layout/notification/notification.component";
import { BackButtonComponent } from "./layout/back-button/back-button.component";
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { filter, Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, MenuComponent, NotificationComponent, BackButtonComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit, OnDestroy {
  public showMainMenu: boolean = true;

  private destroy$: Subject<boolean> = new Subject<boolean>();

  title = 'mdd';

  constructor(private router: Router, iconRegistry: MatIconRegistry, sanitizer: DomSanitizer) {
    iconRegistry.addSvgIcon(
      'user',
      sanitizer.bypassSecurityTrustResourceUrl('assets/icons/user_icon.svg')
    ).addSvgIcon(
      'arrow-left',
      sanitizer.bypassSecurityTrustResourceUrl('assets/icons/arrow_left_icon.svg')
    ).addSvgIcon(
      'arrow-down',
      sanitizer.bypassSecurityTrustResourceUrl('assets/icons/arrow_down_icon.svg')
    ).addSvgIcon(
      'arrow-up',
      sanitizer.bypassSecurityTrustResourceUrl('assets/icons/arrow_up_icon.svg')
    ).addSvgIcon(
      'date',
      sanitizer.bypassSecurityTrustResourceUrl('assets/icons/date_icon.svg')
    ).addSvgIcon(
      'send',
      sanitizer.bypassSecurityTrustResourceUrl('assets/icons/send_icon.svg')
    );

  }

  public ngOnInit() :void {
    this.router.events
    .pipe(
      // unsubscribe on component destruction
      takeUntil(this.destroy$), 
      filter(event => event instanceof NavigationEnd)
    )
    .subscribe(() => {
      console.log(this.router.url);
      this.showMainMenu = !this.router.url.match(/^\/$/);
    });
  }

  public ngOnDestroy(): void {
    // emit to Subject to unsubscribe from olympic service observable
    this.destroy$.next(true);
  }
}
