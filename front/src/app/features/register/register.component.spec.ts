import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterComponent } from './register.component';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { RegisterRequest } from '../../core/models/registerRequest.interface';
import { of, throwError } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  const mockAuthService = {
    register: jest.fn()
  }

  const mockRouter = {
    navigate: jest.fn()
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        {provide: AuthService, useValue: mockAuthService},
        {provide: Router, useValue: mockRouter}
      ],
      imports: [
        RegisterComponent,
        /*MatButtonModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,*/
        NoopAnimationsModule,
        ReactiveFormsModule
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to login on sucessful registration', () => {
    mockAuthService.register.mockReturnValue(of(null));
    component.submit();
    expect(mockAuthService.register).toHaveBeenCalledWith(component.form.value as RegisterRequest);    
    expect(component.error).toBeNull();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  })

  it('should handle registration error', () => {
    mockAuthService.register.mockReturnValue(throwError(() => new Error('login failed')));
    component.submit();

    expect(component.error).not.toBeNull();
  })
});
