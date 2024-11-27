import { TestBed } from '@angular/core/testing';

import { CurrentUserSubscriptionService } from './current-user-subscription.service';
import { HttpTestingController, provideHttpClientTesting, TestRequest } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { Subscription } from '../models/subscription.interface';

describe('CurrentUserSubscriptionService', () => {
  let service: CurrentUserSubscriptionService;
  let mockHttpController: HttpTestingController;
  let spyOnClearCache: any;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(CurrentUserSubscriptionService);
    mockHttpController = TestBed.inject(HttpTestingController);
    spyOnClearCache = jest.spyOn(service, 'clearCache');
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
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

  it('should retrieve current user subscriptions from API when cache present but expired', (done) => {
    const mockSubscriptions: Subscription[] = [
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 1, title: 'Test topic', description: 'This is a test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}},
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 2, title: 'Other test topic', description: 'This is another test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}}
    ];

    // first posts retrieval should trigger an API request and populate cache
    service.getCurrentUserSubscriptions().subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toEqual(mockSubscriptions);
      }
    );
    const retrievalRequest: TestRequest = mockHttpController.expectOne("api/user/me/subscriptions");
    expect(retrievalRequest.request.method).toEqual("GET");
    retrievalRequest.flush(mockSubscriptions);

    let hasBeenCalled: boolean = false;
    // shouldReload will return true one time only
    const spyOnShouldReload = jest.spyOn(service, 'shouldReload').mockImplementation(() => {const res = hasBeenCalled ? false : true; hasBeenCalled = true; return res;});

    // second posts retrieval when cache expired should trigger an API request and repopulate cache
    service.getCurrentUserSubscriptions().subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toEqual(mockSubscriptions);
        done();
      }
    );
    const secondRetrievalRequest: TestRequest = mockHttpController.expectOne("api/user/me/subscriptions");
    expect(secondRetrievalRequest.request.method).toEqual("GET");
    secondRetrievalRequest.flush(mockSubscriptions);

    expect(spyOnShouldReload).toHaveBeenCalled();
  });

  it('should clear local cache and trigger api request on next retrieval', (done) => {
    // to check if local cache reset works, we have to populate id by getting current user subscriptions
    // then clear local cache and check if further retrieval triggers requests

    // first retrieval should trigger requests, as no local cache is present on first call
    const mockSubscriptions: Subscription[] = [
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 1, title: 'Test topic', description: 'This is a test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}},
      {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 2, title: 'Other test topic', description: 'This is another test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}}
    ];

    // ... and for the user subscriptions
    service.getCurrentUserSubscriptions().subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toEqual(mockSubscriptions);
      }
    );
    const subscriptionsRequest: TestRequest = mockHttpController.expectOne("api/user/me/subscriptions");
    expect(subscriptionsRequest.request.method).toEqual("GET");
    subscriptionsRequest.flush(mockSubscriptions);

    // cache has thus no reason to get cleared
    expect(spyOnClearCache).not.toHaveBeenCalled();

    // let's clear local cache
    service.clearCache();
    // clean up the spy to avoid previous invocation to interfere with further tests
    spyOnClearCache.mockClear();

    // it makes sense to check that no API request has been made on cache clearing
    // as earlier versions of the user service did trigger requests because of a misuse of the local BehaviorSubject
    mockHttpController.expectNone("api/user/me/subscriptions");

    // then we check if request are triggered on further data retrieval, which wouldn't happen if local cache was still present 
    service.getCurrentUserSubscriptions().subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toEqual(mockSubscriptions);
        done();
      }
    );
    const newSubscriptionsRequest: TestRequest = mockHttpController.expectOne("api/user/me/subscriptions");
    expect(newSubscriptionsRequest.request.method).toEqual("GET");
    newSubscriptionsRequest.flush(mockSubscriptions);
    expect(spyOnClearCache).not.toHaveBeenCalled();
  });
});
