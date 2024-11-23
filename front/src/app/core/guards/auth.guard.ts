import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, Router, RouterStateSnapshot } from '@angular/router';
import { SessionService } from '../services/session.service';
import { map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router, private sessionService: SessionService) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): MaybeAsync<GuardResult> {
    return this.sessionService.$isLogged().pipe(
      map((loggedIn: boolean) => {
        if(!loggedIn) {
            this.router.navigate([""]);
        }
        return loggedIn;
      })
    );
  }
  
}
