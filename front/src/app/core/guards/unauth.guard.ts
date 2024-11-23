import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, Router, RouterStateSnapshot } from '@angular/router';
import { SessionService } from '../services/session.service';
import { map, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UnauthGuard implements CanActivate {
  constructor(private router: Router, private sessionService: SessionService) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): MaybeAsync<GuardResult> {
    return this.sessionService.$isLogged().pipe(
      map((logged:boolean) => {
        // logged in user should be redirected to their posts
        if(logged) {this.router.navigate(['posts']);} 
        return !logged;
      })
    );
  }
}
