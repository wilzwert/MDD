import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { SessionInformation } from '../models/session-information.interface';
import { TokenStorageService } from './token-storage.service';
import { RefreshTokenResponse } from '../models/refresh-token-response.interface';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  public logged = false;

  private isLoggedSubject = new BehaviorSubject<boolean>(this.logged);

  constructor(
    private tokenStorageService: TokenStorageService, 
    private router: Router) {
    if(this.tokenStorageService.getToken() != null) {
      this.logged = true;
      this.next();
    }
  }

  public isLogged() :boolean {
    return this.logged;
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
    this.logged = true;
    this.next();
  }

  public logOut(): void {
    // clear user and session related data from storage
    this.tokenStorageService.clearSessionInformation();
    this.logged = false;
    this.next();
    this.router.navigate(['']);
  }

  private next(): void {
    this.isLoggedSubject.next(this.logged);
  }
}
