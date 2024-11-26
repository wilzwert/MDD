import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginRequest } from '../models/login-request.interface';
import { SessionInformation } from '../models/session-information.interface';
import { RegisterRequest } from '../models/register-request.interface';
import { RefreshTokenRequest } from '../models/refresh-token-request.interface';
import { RefreshTokenResponse } from '../models/refresh-token-response.interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private pathService = 'api/auth';

  constructor(private httpClient: HttpClient) { }

  public register(registerRequest: RegisterRequest): Observable<void> {
    return this.httpClient.post<void>(`${this.pathService}/register`, registerRequest);
  }

  public login(loginRequest: LoginRequest): Observable<SessionInformation> {
    return this.httpClient.post<SessionInformation>(`${this.pathService}/login`, loginRequest);
  }

  public refreshToken(refreshTokenRequest: RefreshTokenRequest): Observable<RefreshTokenResponse> {
    return this.httpClient.post<RefreshTokenResponse>(`${this.pathService}/refreshToken`, refreshTokenRequest);
  }
}
