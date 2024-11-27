import { Component, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '../../core/services/auth.service';
import { RegisterRequest } from '../../core/models/register-request.interface';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, of, take, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { NotificationService } from '../../core/services/notification.service';
import { ApiError } from '../../core/errors/api-error';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    ReactiveFormsModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  public error: string |null = null;

  public form: FormGroup;

  constructor(
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder,
    private matSnackBar: MatSnackBar,
    private notificationService: NotificationService
  ) {
    this.form = this.fb.group({
      email: [
        '', 
        [
          Validators.required,
          Validators.email
        ]
      ],
      userName: [
        '', 
        [
          Validators.required,
          Validators.minLength(5)
        ]
      ],
      password: [
        '',
        [
          Validators.required,
          Validators.pattern('((?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,30})')
        ]
      ]
    });
  }

  get email() {
    return this.form.get('email');
  }

  get userName() {
    return this.form.get('userName');
  }

  get password() {
    return this.form.get('password');
  }


  submit() :void {
    this.error = null;
    this.authService.register(this.form.value as RegisterRequest)
    .pipe(
      take(1),
      catchError(
        (error: ApiError) => {
          return throwError(() => new Error(
            'Impossible de créer votre compte. '+(error.httpStatus === 409 ? "Email ou nom d'utilisateur déjà utilisé" : 'Une erreur est survenue')
          ));
        }
    ))
    .subscribe({
      next: data => {
        this.matSnackBar.open("Votre inscription a bien été enregistrée, vous pouvez maintenant vous connecter", 'Close', { duration: 3000 })
        this.router.navigate(["/login"])
      }
    });
  }
}
