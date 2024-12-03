import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';
import { HttpTestingController, provideHttpClientTesting, TestRequest } from '@angular/common/http/testing';
import { LoginRequest } from '../models/login-request.interface';
import { RegisterRequest } from '../models/register-request.interface';
import { SessionInformation } from '../models/session-information.interface';

describe("AuthService unit tests", () =>  {
    let service: AuthService;
    let mockHttpController: HttpTestingController;

    beforeEach(async () => {
        TestBed.configureTestingModule({
            providers: [
                AuthService,
                provideHttpClient(),
                provideHttpClientTesting()
            ],
            imports: [],
        });
    
        service = TestBed.inject(AuthService);
        mockHttpController = TestBed.inject(HttpTestingController);
      });

      afterEach(() =>  {
        mockHttpController.verify()
      })

      it('should post login request and return session information as an observable', (done) => {
        const mockSessionInfo: SessionInformation = {id: 1, username: "johndoe", token: "token123", type: "Bearer", refreshToken: "refresh_token"};
        const loginRequest: LoginRequest = {email: "john.doe@example.com", password: "testpassword"};
        
        service.login(loginRequest).subscribe(
            {
                next: response =>  {
                expect(response).toEqual(mockSessionInfo);
                done();
            }}
        );

        const testRequest: TestRequest = mockHttpController.expectOne("api/auth/login");
        expect(testRequest.request.method).toEqual("POST");
        expect(testRequest.request.body).toEqual(loginRequest);
        testRequest.flush(mockSessionInfo);
      })

      
      it('should post register request and return void as an observable', (done) => {
        const registerRequest: RegisterRequest = {email: "john.doe@example.com", password: "testpassword", username: "username"};
        service.register(registerRequest).subscribe(response => {
            expect(response).toBeNull();
            done()
        });

        const testRequest: TestRequest = mockHttpController.expectOne("api/auth/register");
        expect(testRequest.request.method).toEqual("POST");
        expect(testRequest.request.body).toEqual(registerRequest);
        testRequest.flush(null);
      })

      it('should get http error on login failed at network level', (done) => {
         // Create mock ProgressEvent with type `error`, raised when something goes wrong
        // at network level. e.g. Connection timeout, DNS error, offline, etc.
        const mockError = new ProgressEvent('error');
        const loginRequest: LoginRequest = {email: "john.doe@example.com", password: "testpassword"};
        
        service.login(loginRequest).subscribe(
            {
                next: () =>  fail('should have failed with http error'),
                error: (error: HttpErrorResponse) => {
                    expect(error.error).toBe(mockError);
                    done()
                }
            }
        );

        const testRequest: TestRequest = mockHttpController.expectOne("api/auth/login");
        expect(testRequest.request.method).toEqual("POST");
        expect(testRequest.request.body).toEqual(loginRequest);
        testRequest.error(mockError);
      })

      it('should get http error on register failed at network level', (done) => {
        // Create mock ProgressEvent with type `error`, raised when something goes wrong
       // at network level. e.g. Connection timeout, DNS error, offline, etc.
       const mockError = new ProgressEvent('error');
       const registerRequest: RegisterRequest = {email: "john.doe@example.com", password: "testpassword", username: "username"};
        service.register(registerRequest).subscribe({
            next: () => fail('should have failed with http error'),
            error: (error: HttpErrorResponse) => {
                expect(error.error).toBe(mockError);
                done()
            }
        });

        const testRequest: TestRequest = mockHttpController.expectOne("api/auth/register");
        expect(testRequest.request.method).toEqual("POST");
        expect(testRequest.request.body).toEqual(registerRequest);
        testRequest.error(mockError);
     })
})