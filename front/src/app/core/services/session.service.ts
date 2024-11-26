import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { SessionInformation } from '../models/session-information.interface';
import { TokenStorageService } from './token-storage.service';
import { User } from '../models/user.interface';
import { RefreshTokenResponse } from '../models/refresh-token-response.interface';
import { CurrentUserService } from './current-user.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  public isLogged = false;

  private isLoggedSubject = new BehaviorSubject<boolean>(this.isLogged);

  constructor(private tokenStorageService: TokenStorageService, private router: Router) {
    if(this.tokenStorageService.getToken() != null) {
      this.isLogged = true;
      this.next();
    }
  }

  public getToken() :string | null {
    return this.tokenStorageService.getToken();
  }

  public getTokenType() :string | null {
    return this.tokenStorageService.getTokenType();
  }

  public getRefreshToken() :string | null {
    return this.tokenStorageService.getRefreshToken();
  }

  public $isLogged(): Observable<boolean> {
    return this.isLoggedSubject.asObservable();
  }

  public handleTokenAfterRefresh(data: RefreshTokenResponse): void {
    this.tokenStorageService.saveTokenAfterRefresh(data);
  }

  public logIn(data: SessionInformation): void {
    this.tokenStorageService.saveSessionInformation(data);
    this.isLogged = true;
    this.next();
  }

  public logOut(): void {
    this.tokenStorageService.clearSessionInformation();
    this.isLogged = false;
    this.next();
    this.router.navigate(['']);
  }

  private next(): void {
    this.isLoggedSubject.next(this.isLogged);
  }
}
