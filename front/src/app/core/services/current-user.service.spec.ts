import { TestBed } from '@angular/core/testing';

import { CurrentUserService } from './current-user.service';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting, TestRequest } from '@angular/common/http/testing';
import { User } from '../models/user.interface';
import { Subscription } from '../models/subscription.interface';

describe('CurrentUserService', () => {
  let service: CurrentUserService;
  let mockHttpController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(CurrentUserService);
    mockHttpController = TestBed.inject(HttpTestingController);
  });
  
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get current user', (done) => {
    const mockUser: User = {userName: "user1", email: "user1@example.com"} as User;

    service.getCurrentUser().subscribe(
      (user: User) => {
        expect(user).toEqual(mockUser);
        done();
      }
    );

    const testRequest: TestRequest = mockHttpController.expectOne("api/user/me");
    expect(testRequest.request.method).toEqual("GET");
    testRequest.flush(mockUser);
  });

  it('should retrieve current user from local cache when present', (done) => {
    const mockUser: User = {userName: "user1", email: "user1@example.com"} as User;

    service.getCurrentUser().subscribe(
      (user: User) => {
        expect(user).toEqual(mockUser);
      }
    );

    const testRequest: TestRequest = mockHttpController.expectOne("api/user/me");
    expect(testRequest.request.method).toEqual("GET");
    testRequest.flush(mockUser);

    // second retrieval should not trigger any API request
    service.getCurrentUser().subscribe(
      (user: User) => {
        expect(user).toEqual(mockUser);
        done();
      }
    );

    mockHttpController.expectNone("api/user/me");
  });
  
  it('should update current user and populate or update local cache', (done) => {
    const mockUser: User = {userName: "newusername", email: "newuser@example.com"} as User;

    service.updateCurrentUser({userName: "newusername", email: "newuser@example.com"}).subscribe(
      (user: User) => {
        expect(user).toEqual(mockUser);
      }
    );
    const testRequest: TestRequest = mockHttpController.expectOne("api/user/me");
    expect(testRequest.request.method).toEqual("PUT");
    testRequest.flush(mockUser);

    service.getCurrentUser().subscribe(
      (user: User) => {
        expect(user).toEqual(mockUser);
        done();
      }
    )
    // current user should be available from local cache, no API request should have been triggerd 
    mockHttpController.expectNone("api/user/me");
  });
  
  it('should clear local cache', (done) => {
    // to check if local cache reset works, we have to populate it by getting current user
    // then clear local cache and check if further retrieval triggers requests

    // first retrieval should trigger requests, as no local cache is present on first call
    const mockUser: User = {id: 1, userName: "user1", email: "user1@example.com", createdAt: "2024-11-28T08:00:00"};
    service.getCurrentUser().subscribe(
      (user: User) => {
        expect(user).toEqual(mockUser);
      }
    );
    const userRequest: TestRequest = mockHttpController.expectOne("api/user/me");
    expect(userRequest.request.method).toEqual("GET");
    userRequest.flush(mockUser);

    // let's clear local cache
    service.clearCache();
    // it makes sense to check that no API request has been made on cache clearing
    // as earlier versions of the user service did trigger requests because of a misuse of the local BehaviorSubject
    mockHttpController.expectNone("api/user/me");

    // then we check if request are triggered on further data retrieval, which wouldn't happen if local cache was still present 
    service.getCurrentUser().subscribe(
      (user: User) => {
        expect(user).toEqual(mockUser);
        done();
      }
    );
    const newUserRequest: TestRequest = mockHttpController.expectOne("api/user/me");
    expect(newUserRequest.request.method).toEqual("GET");
    newUserRequest.flush(mockUser);
  });
});