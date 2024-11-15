import { HTTP_INTERCEPTORS, HttpClient,  } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { SessionService } from '../services/session.service';
import { TestBed } from "@angular/core/testing";
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { JwtInterceptor } from "./jwt.interceptor";
import { SessionInformation } from "../interfaces/sessionInformation.interface";

describe('JwtInterceptor', () => {
  let httpTestingController: HttpTestingController;
  let httpClient: HttpClient;
  let sessionService: SessionService;
  let interceptor: JwtInterceptor;

  
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
      ],
    });

    interceptor = TestBed.inject(JwtInterceptor);
    sessionService = TestBed.inject(SessionService);
    httpTestingController = TestBed.inject(HttpTestingController);
    httpClient = TestBed.inject(HttpClient);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });

  it('should not add bearer to token in request headers if not logged in', () => {
    const url = '/api/me';
    httpClient.get(url).subscribe();
    const req = httpTestingController.expectOne(url);
    expect(req.request.headers.get('Authorization')).toBeNull();
  });

  it('should add bearer to token in request headers if logged in', () => {
    const mockSessionInformation: SessionInformation = {
      admin: true,
      id: 1,
      username: 'johndoe',
      token: 'token123',
      firstName: 'John',
      lastName: 'Doe',
      type: 'Bearer'
    }

    sessionService.logIn(mockSessionInformation);

    const url = '/api/me';
    httpClient.get(url).subscribe();

    const req = httpTestingController.expectOne(url);
    expect(req.request.headers.get('Authorization')).toBe(`${mockSessionInformation.type} ${mockSessionInformation.token}`);
  });

})