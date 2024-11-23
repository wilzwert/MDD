import { HttpErrorResponse, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { SessionService } from '../services/session.service';
import { AuthService } from "../services/auth.service";
import { BehaviorSubject, catchError, filter, switchMap, take, throwError } from "rxjs";
import { RefreshTokenRequest } from "../models/refresh-token-request.interface";
import { RefreshTokenResponse } from "../models/refresh-token-response.interface";


@Injectable({ providedIn: 'root' })
export class JwtInterceptor implements HttpInterceptor {
  private isRefreshing: boolean = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(private sessionService: SessionService, private authService: AuthService) {}

  public intercept(request: HttpRequest<any>, next: HttpHandler) {
    if (this.sessionService.isLogged) {      
      request = this.addTokenHeader(request, this.sessionService.getToken()!);
    }

    return next.handle(request).pipe(
      catchError(error => {
        if (error instanceof HttpErrorResponse && error.status === 401 && !request.url.includes('auth/login')) {
          return this.tryToRefreshToken(request, next);
        }

        return throwError(() => error);
      })
    );
  }

  private tryToRefreshToken(request: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefreshing) {
      console.log('should we refresh ?');
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      const token = this.sessionService.getRefreshToken();

      if (token) {
        console.log('trying to refresh');
        return this.authService.refreshToken({refreshToken: token} as RefreshTokenRequest).pipe(
          switchMap((data: RefreshTokenResponse) => {
            this.isRefreshing = false;
            this.sessionService.handleTokenAfterRefresh(data);
            this.refreshTokenSubject.next(data.token);
            console.log('it seems we got a new token ', data);
            return next.handle(this.addTokenHeader(request, data.token));
          }),
          catchError((err) => {
            this.isRefreshing = false;
            
            this.sessionService.logOut();
            return throwError(() => err);
          })
        );
      }
    }

    // let's handle original request with the new token
    return this.refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap((token) => next.handle(this.addTokenHeader(request, token)))
    );
  }

  private addTokenHeader(request: HttpRequest<any>, token: string): HttpRequest<any> {
    console.log('adding bearer '+token+' for '+request.method+' '+request.url);
    return request.clone({ headers: request.headers.set('Authorization', this.sessionService.getTokenType()!+' '+token) });
  }
}
