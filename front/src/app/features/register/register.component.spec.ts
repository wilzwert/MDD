import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterComponent } from './register.component';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { RegisterRequest } from '../../core/models/register-request.interface';
import { of, throwError } from 'rxjs';
import { NotificationService } from '../../core/services/notification.service';
import { DomSanitizer } from '@angular/platform-browser';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: AuthService;
  let notificationService: NotificationService;
  let router: Router;
  

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
        {provide: Router, useValue: mockRouter},
        {
          provide: MatIconRegistry,
          useValue: {
            addSvgIcon: jest.fn(),
            getNamedSvgIcon: jest.fn().mockReturnValue(of('<svg></svg>')),
          },
        },
        {
          provide: DomSanitizer,
          useValue: {
            bypassSecurityTrustResourceUrl: jest.fn(),
          },
        },
      ],
      imports: [
        RegisterComponent,
        MatButtonModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        NoopAnimationsModule,
        ReactiveFormsModule
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    notificationService = TestBed.inject(NotificationService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  })

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to login on sucessful registration', () => {
    const authSpy = jest.spyOn(authService, 'register').mockReturnValue(of(null));
    const routerSpy = jest.spyOn(router, 'navigate').mockImplementation();
    const notificationSpy = jest.spyOn(notificationService, 'confirmation').mockImplementation();
    
    component.submit();
    expect(authSpy).toHaveBeenCalledWith(component.form.value as RegisterRequest);    
    expect(routerSpy).toHaveBeenCalledWith(['/login']);
    expect(notificationSpy).toHaveBeenCalledTimes(1);
    expect(notificationSpy).toHaveBeenCalledWith("Votre inscription a bien été enregistrée, vous pouvez maintenant vous connecter");
  })

  it('should do nothing when registration fails', () => {
    const authSpy = jest.spyOn(authService, 'register').mockReturnValue(throwError(() => new Error('login failed')));
    const routerSpy = jest.spyOn(router, 'navigate').mockImplementation();
    const notificationSpy = jest.spyOn(notificationService, 'confirmation').mockImplementation();
    component.submit();
    expect(authSpy).toHaveBeenCalledTimes(1);
    expect(authSpy).toHaveBeenCalledWith(component.form.value as RegisterRequest);
    expect(routerSpy).not.toHaveBeenCalled();
    expect(notificationSpy).not.toHaveBeenCalled();
  })
});
