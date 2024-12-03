import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginRequest } from '../models/login-request.interface';
import { SessionInformation } from '../models/session-information.interface';
import { RegisterRequest } from '../models/register-request.interface';
import { RefreshTokenRequest } from '../models/refresh-token-request.interface';
import { RefreshTokenResponse } from '../models/refresh-token-response.interface';
import { DataService } from './data.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiPath = 'auth';

  constructor(private dataService: DataService) { }

  public register(registerRequest: RegisterRequest): Observable<null> {
    return this.dataService.post<null>(`${this.apiPath}/register`, registerRequest);
  }

  public login(loginRequest: LoginRequest): Observable<SessionInformation> {
    return this.dataService.post<SessionInformation>(`${this.apiPath}/login`, loginRequest);
  }

  public refreshToken(refreshTokenRequest: RefreshTokenRequest): Observable<RefreshTokenResponse> {
    return this.dataService.post<RefreshTokenResponse>(`${this.apiPath}/refreshToken`, refreshTokenRequest);
  }
}
