import { Injectable } from '@angular/core';
import { SessionInformation } from '../models/sessionInformation.interface';
import { RefreshTokenResponse } from '../models/refreshTokenResponse.interface';

const TOKEN_KEY = 'auth-token';
const TOKEN_TYPE_KEY = 'auth-token-type';
const REFRESH_TOKEN_KEY = 'refresh-token';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  constructor() { }

  public getToken(): string | null {
    return window.localStorage.getItem(TOKEN_KEY);
  }

  public getTokenType(): string | null {
    return window.localStorage.getItem(TOKEN_TYPE_KEY);
  }

  public getRefreshToken(): string | null {
    return window.localStorage.getItem(REFRESH_TOKEN_KEY);
  }

  public saveToken(token: string) :void {
    window.localStorage.removeItem(TOKEN_KEY);
    window.localStorage.setItem(TOKEN_KEY, token);
  }

  public saveTokenType(tokenType: string) :void {
    window.localStorage.removeItem(TOKEN_TYPE_KEY);
    window.localStorage.setItem(TOKEN_TYPE_KEY, tokenType);
  }

  public saveRefreshToken(refreshToken: string) :void {
    window.localStorage.removeItem(REFRESH_TOKEN_KEY);
    window.localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
  }

  public clearSessionInformation() :void {
    window.localStorage.removeItem(TOKEN_KEY);
    window.localStorage.removeItem(TOKEN_TYPE_KEY);
    window.localStorage.removeItem(REFRESH_TOKEN_KEY);
  }

  public saveSessionInformation(data : SessionInformation) :void {
    this.saveToken(data.token);
    this.saveTokenType(data.type);
    this.saveRefreshToken(data.refreshToken);
  }

  public saveTokenAfterRefresh(data: RefreshTokenResponse): void {
    this.clearSessionInformation();
    this.saveToken(data.token);
    this.saveTokenType(data.type);
    this.saveRefreshToken(data.refreshToken);
  }
}