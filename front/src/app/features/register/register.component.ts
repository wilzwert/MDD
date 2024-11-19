import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '../../core/services/auth.service';
import { RegisterRequest } from '../../core/models/registerRequest.interface';
import { MatSnackBar } from '@angular/material/snack-bar';
import { take } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    ReactiveFormsModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  public hide: boolean = true;
  public error: string |null = null;

  public form: FormGroup;

  constructor(
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder,
    private matSnackBar: MatSnackBar,
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
          Validators.min(6)
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

  submit() :void {
    this.error = null;
    this.authService.register(this.form.value as RegisterRequest)
    .pipe(take(1))
    .subscribe({
      next: data => {
        this.matSnackBar.open("Your account has been created, please login now.", 'Close', { duration: 3000 })
        this.router.navigate(["/login"])
      },
      error: err => {this.error = err.error?.message}
    });
  }
}
