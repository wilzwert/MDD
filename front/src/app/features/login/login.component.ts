import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { SessionService } from '../../core/services/session.service';
import { LoginRequest } from '../../core/models/login-request.interface';
import { AuthService } from '../../core/services/auth.service';
import { SessionInformation } from '../../core/models/session-information.interface';
import { catchError, throwError } from 'rxjs';
import { ApiError } from '../../core/errors/api-error';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    ReactiveFormsModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  public hide: boolean = true;
  public onError: boolean = false;

  public form:FormGroup;

  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router,
    private sessionService: SessionService) {
      this.form = this.fb.group({
        email: [
          '', 
          [
            Validators.required,
            Validators.email
          ]
        ],
        password: [
          '',
          [
            Validators.required,
            Validators.min(3)
          ]
        ]
      });
  }

  public submit() :void {
      const loginRequest: LoginRequest = this.form.value as LoginRequest;
      this.authService.login(loginRequest)
        .pipe(
          catchError(
            (error: ApiError) => {
              return throwError(() => new Error(
                'La connexion a échoué.'+(error.httpStatus === 401 ? ' Email ou mot de passe incorrect' : '')
              ));
            }
          )
        )
        .subscribe(
          (response: SessionInformation) => {
            this.sessionService.logIn(response);
            this.router.navigate(['/'])
          }
        )
  }
}
