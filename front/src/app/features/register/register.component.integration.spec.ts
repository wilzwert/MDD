import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';
import { RegisterComponent } from './register.component';
import { AuthService } from 'src/app/core/services/auth.service';
import { provideRouter, Router } from '@angular/router';
import { firstValueFrom, of } from 'rxjs';
import { RegisterRequest } from 'src/app/core/models/register-request.interface';
import { LoginComponent } from '../login/login.component';
import { HttpTestingController, provideHttpClientTesting, TestRequest } from '@angular/common/http/testing';
import { DomSanitizer } from '@angular/platform-browser';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let router: Router;
  let mockHttpController: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([
          { path: "", component: RegisterComponent },
          { path: 'login', component: LoginComponent }
        ]),
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
        BrowserAnimationsModule,
        ReactiveFormsModule,  
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    mockHttpController = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  it('should handle successful registration and navigate to login', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation(() => firstValueFrom(of(true)));

    component.form.setValue({
      email: "test@example.com",
      username: "testuser",
      password: "abcd1234"
    });
    component.submit();

    const testRequest: TestRequest = mockHttpController.expectOne("api/auth/register");
    expect(testRequest.request.method).toEqual("POST");
    expect(testRequest.request.body).toEqual(component.form.value as RegisterRequest);
    testRequest.flush(null);

    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  })

  it('should handle registration error', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation(() => firstValueFrom(of(true)));
    component.form.controls['email'].setValue("test@example.com");
    component.form.controls['username'].setValue('testuser');
    component.form.controls['password'].setValue('abcd1234');
    component.submit();

    const testRequest: TestRequest = mockHttpController.expectOne("api/auth/register");
    expect(testRequest.request.method).toEqual("POST");
    expect(testRequest.request.body).toEqual(component.form.value as RegisterRequest);
    testRequest.flush(null, {status: 500, statusText: 'Internal server error'});
    
    fixture.detectChanges();

    expect(navigateSpy).not.toHaveBeenCalled();
  })
});
