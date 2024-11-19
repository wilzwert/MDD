import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { SessionInformation } from '../models/sessionInformation.interface';
import { TokenStorageService } from './token-storage.service';
import { User } from '../models/user.interface';
import { RefreshTokenResponse } from '../models/refreshTokenResponse.interface';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  public isLogged = false;

  private isLoggedSubject = new BehaviorSubject<boolean>(this.isLogged);

  constructor(private tokenStorageService: TokenStorageService) {
    if(this.tokenStorageService.getToken() != null) {
      console.log('isLogged');
      this.isLogged = true;
      this.next();
    }
    else {
      console.log('isNotLogged');
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

  public getUser() :User | null {
    return this.tokenStorageService.getUser();
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
  }

  private next(): void {
    this.isLoggedSubject.next(this.isLogged);
  }
}
