import { Location } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import {MatTooltipModule} from '@angular/material/tooltip';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter, Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-back-button',
  standalone: true,
  imports: [MatIconModule, MatButtonModule, MatTooltipModule],
  templateUrl: './back-button.component.html',
  styleUrl: './back-button.component.scss'
})
export class BackButtonComponent implements OnInit, OnDestroy {
  private destroy$: Subject<boolean> = new Subject<boolean>();

  public goBackToRoute: string | undefined;

  constructor(private router: Router, private location: Location, private activatedRoute: ActivatedRoute) {}
  
  goBack() :void {
    this.router.navigate([this.goBackToRoute]);
  }

  updateBackButton(): void {
    // get currently active deepest route data
    let route = this.activatedRoute.firstChild;
    while (route?.firstChild) {
      route = route.firstChild;
    }
    const routeData = route?.snapshot.data;
    this.goBackToRoute =routeData?.['goBackToRoute'];
  }

  ngOnInit(): void {
    this.router.events
    .pipe(
      // unsubscribe on component destruction
      takeUntil(this.destroy$), 
      filter(event => event instanceof NavigationEnd)
    )
    .subscribe(() => {
      this.updateBackButton();
    });
  }

  ngOnDestroy(): void {
    // emit to Subject to unsubscribe from olympic service observable
    this.destroy$.next(true);
  }

}
