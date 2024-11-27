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

  it('should retrieve current user from local when present', (done) => {
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

  it('should retrieve current user subscriptions', (done) => {
    const mockSubscriptions: Subscription[] = [
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 1, title: 'Test topic', description: 'This is a test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}},
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 2, title: 'Other test topic', description: 'This is another test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}}
    ];

    service.getCurrentUserSubscriptions().subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toEqual(mockSubscriptions);
        done();
      }
    );

    const testRequest: TestRequest = mockHttpController.expectOne("api/user/me/subscriptions");
    expect(testRequest.request.method).toEqual("GET");
    testRequest.flush(mockSubscriptions);
  });

  it('should retrieve current user subscriptions from local cache when present', (done) => {
    const mockSubscriptions: Subscription[] = [
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 1, title: 'Test topic', description: 'This is a test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}},
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 2, title: 'Other test topic', description: 'This is another test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}}
    ];

    service.getCurrentUserSubscriptions().subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toEqual(mockSubscriptions);
      }
    );

    const testRequest: TestRequest = mockHttpController.expectOne("api/user/me/subscriptions");
    expect(testRequest.request.method).toEqual("GET");
    testRequest.flush(mockSubscriptions);

    service.getCurrentUserSubscriptions().subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toEqual(mockSubscriptions);
        done();
      }
    );
    mockHttpController.expectNone("api/user/me/subscriptions");
  });

  it('should create a new subscription and update local cache when present', (done) => {
    const mockSubscriptions: Subscription[] = [
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 1, title: 'Test topic', description: 'This is a test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}},
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 2, title: 'Other test topic', description: 'This is another test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}}
    ];
    const mockCreatedSubscription: Subscription = {userId: 1, createdAt: '2024-11-27T10:32', topic: {id: 3, title: 'Third test topic', description: 'This is a third test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}};

    service.getCurrentUserSubscriptions().subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toEqual(mockSubscriptions);
      }
    );

    const testRequest: TestRequest = mockHttpController.expectOne("api/user/me/subscriptions");
    expect(testRequest.request.method).toEqual("GET");
    testRequest.flush(mockSubscriptions);

    service.subscribe(1).subscribe(
      (subscription: Subscription) => {
        expect(subscription).toEqual(mockCreatedSubscription);
      }
    );
    const createTestRequest: TestRequest = mockHttpController.expectOne("api/topics/1/subscription");
    expect(createTestRequest.request.method).toEqual("POST");
    createTestRequest.flush(mockCreatedSubscription);

    service.getCurrentUserSubscriptions().subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toHaveLength(3);
        expect(subscriptions).toContain(mockCreatedSubscription);
        done();
      }
    );
    mockHttpController.expectNone("api/user/me/subscriptions");
  });

  it('should create a new subscription and not populate local cache when empty', (done) => {
    const mockCreatedSubscription: Subscription = {userId: 1, createdAt: '2024-11-27T10:32', topic: {id: 3, title: 'Third test topic', description: 'This is a third test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}};
    const mockSubscriptions: Subscription[] = [
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 1, title: 'Test topic', description: 'This is a test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}},
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 2, title: 'Other test topic', description: 'This is another test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}},
      mockCreatedSubscription
    ];
    
    service.subscribe(1).subscribe(
      (subscription: Subscription) => {
        expect(subscription).toEqual(mockCreatedSubscription);
      }
    );
    const createTestRequest: TestRequest = mockHttpController.expectOne("api/topics/1/subscription");
    expect(createTestRequest.request.method).toEqual("POST");
    createTestRequest.flush(mockCreatedSubscription);

    // user subscriptions should trigger an API request, as local cache was not present when new subscription was created
    service.getCurrentUserSubscriptions().subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toHaveLength(3);
        expect(subscriptions).toContain(mockCreatedSubscription);
        done();
      }
    );
    const testRequest: TestRequest = mockHttpController.expectOne("api/user/me/subscriptions");
    expect(testRequest.request.method).toEqual("GET");
    testRequest.flush(mockSubscriptions);
  });

  it('should delete a subscription and not update local cache when empty', (done) => {
    const mockSubscriptions: Subscription[] = [
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 1, title: 'Test topic', description: 'This is a test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}},
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 2, title: 'Other test topic', description: 'This is another test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}}
    ];
    service.unSubscribe(1).subscribe(
      (r) => {
        expect(r).toBeNull();
      }
    );
    const createTestRequest: TestRequest = mockHttpController.expectOne("api/topics/1/subscription");
    expect(createTestRequest.request.method).toEqual("DELETE");
    createTestRequest.flush(null);

    // user subscriptions should trigger an API request, as local cache was not present when new subscription was created
    service.getCurrentUserSubscriptions().subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toEqual(mockSubscriptions);
        done();
      }
    );
    const testRequest: TestRequest = mockHttpController.expectOne("api/user/me/subscriptions");
    expect(testRequest.request.method).toEqual("GET");
    testRequest.flush(mockSubscriptions);
  });

  it('should delete a subscription and update local when present', (done) => {
    const mockSubscriptions: Subscription[] = [
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 1, title: 'Test topic', description: 'This is a test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}},
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 2, title: 'Other test topic', description: 'This is another test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}}
    ];
    const mockSubscriptionsAfterDelete: Subscription[] = [
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 1, title: 'Test topic', description: 'This is a test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}}
    ];
    
    // user subscriptions should trigger an API request, as no local cache is present on first call
    service.getCurrentUserSubscriptions().subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toEqual(mockSubscriptions);
        done();
      }
    );
    const testRequest: TestRequest = mockHttpController.expectOne("api/user/me/subscriptions");
    expect(testRequest.request.method).toEqual("GET");
    testRequest.flush(mockSubscriptions);

    // delete subscription
    service.unSubscribe(1).subscribe(
      (r) => {
        expect(r).toBeNull();
      }
    );
    const createTestRequest: TestRequest = mockHttpController.expectOne("api/topics/1/subscription");
    expect(createTestRequest.request.method).toEqual("DELETE");
    createTestRequest.flush(null);

    // user subscriptions should not trigger an API request, as local cache was present when subscription was deleted
    service.getCurrentUserSubscriptions().subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toEqual(mockSubscriptionsAfterDelete);
        done();
      }
    );
    mockHttpController.expectNone("api/user/me/subscriptions");
  });

  it('should clear local cache', (done) => {
    // to check if local caches reset works, we have to populate them by getting current user and subscriptions
    // then clear local cache and check if further retrieval triggers requests

    // first retrieval should trigger requests, as no local cache is present on first call
    // for the current user...
    const mockUser: User = {userName: "user1", email: "user1@example.com"} as User;
    service.getCurrentUser().subscribe(
      (user: User) => {
        expect(user).toEqual(mockUser);
      }
    );
    const userRequest: TestRequest = mockHttpController.expectOne("api/user/me");
    expect(userRequest.request.method).toEqual("GET");
    userRequest.flush(mockUser);

    const mockSubscriptions: Subscription[] = [
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 1, title: 'Test topic', description: 'This is a test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}},
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 2, title: 'Other test topic', description: 'This is another test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}}
    ];

    // ... and for the user subscriptions
    service.getCurrentUserSubscriptions().subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toEqual(mockSubscriptions);
        done();
      }
    );
    const subscriptionsRequest: TestRequest = mockHttpController.expectOne("api/user/me/subscriptions");
    expect(subscriptionsRequest.request.method).toEqual("GET");
    subscriptionsRequest.flush(mockSubscriptions);

    // let's clear local cache
    service.clearCache();
    // it makes sense to check that no API request has been made on cache clearing
    // as earlier versions of the user service did trigger requests because of a misuse of the local BehaviorSubject
    mockHttpController.expectNone("api/user/me");
    mockHttpController.expectNone("api/user/me/subscriptions");

    // then we check if request are triggered on further data retrieval, which wouldn't happen if local cache was still present 
    service.getCurrentUser().subscribe();
    const newUserRequest: TestRequest = mockHttpController.expectOne("api/user/me");
    expect(subscriptionsRequest.request.method).toEqual("GET");

    service.getCurrentUserSubscriptions().subscribe(done);
    const newSubscriptionsRequest: TestRequest = mockHttpController.expectOne("api/user/me/subscriptions");
    expect(newSubscriptionsRequest.request.method).toEqual("GET");
  });


});
