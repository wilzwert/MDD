import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TopicsListComponent } from './topics-list.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TopicService } from '../../../../core/services/topic.service';
import { CurrentUserSubscriptionService } from '../../../../core/services/current-user-subscription.service';
import { SessionService } from '../../../../core/services/session.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { of, throwError } from 'rxjs';
import { Topic } from '../../../../core/models/topic.interface';
import { Subscription } from '../../../../core/models/subscription.interface';

describe('TopicsListComponent', () => {
  let component: TopicsListComponent;
  let fixture: ComponentFixture<TopicsListComponent>;

  let topicServiceMock: jest.Mocked<TopicService>;
  let subscriptionServiceMock: jest.Mocked<CurrentUserSubscriptionService>;
  let sessionServiceMock: jest.Mocked<SessionService>;
  let notificationServiceMock: jest.Mocked<NotificationService>;
  const topicsMock: Topic[] = [
    {id: 1, title: 'Test topic'} as Topic,
    {id: 2, title: 'Second test Topic'} as Topic
  ];
  const subscriptionsMock: Subscription[] = [
    {userId: 1, createdAt: '2024-11-02T10:00:00', topic: {id: 1, title: 'Topic title', description: 'Topic description', createdAt: '2024-01-12T10:00:00', updatedAt: '2024-01-13T11:00:00'}},
    {userId: 1, createdAt: '2024-11-02T10:00:00', topic: {id: 2, title: 'Second topic title', description: 'Second topic description', createdAt: '2024-01-14T10:00:00', updatedAt: '2024-01-15T11:00:00'}},
  ];

  beforeEach(async () => {
    topicServiceMock = {
      getAllTopics: jest.fn().mockReturnValue(of(topicsMock))
    } as unknown as jest.Mocked<TopicService>;

    sessionServiceMock = {
      $isLogged: jest.fn().mockReturnValue(of(true)),
    } as unknown as jest.Mocked<SessionService>;

    notificationServiceMock = {
      confirmation: jest.fn(),
    } as unknown as jest.Mocked<NotificationService>;

    subscriptionServiceMock = {
      getCurrentUserSubscriptions: jest.fn().mockReturnValue(of(subscriptionsMock)),
      unSubscribe: jest.fn(),
      subscribe: jest.fn()
    }as unknown as jest.Mocked<CurrentUserSubscriptionService>;

    await TestBed.configureTestingModule({
      imports: [TopicsListComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TopicService, useValue: topicServiceMock},
        { provide: SessionService, useValue: sessionServiceMock},
        { provide: NotificationService, useValue: notificationServiceMock},
        { provide: CurrentUserSubscriptionService, useValue: subscriptionServiceMock},
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TopicsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  /*
  it('should create', () => {
    expect(component).toBeTruthy();
  });*/

  it('should load as expected when user logged in', (done) => {
    // by default, user should be logged in, topics and user subscriptions should be retrieved
    component.loggedIn$.subscribe(
      (loggedIn: boolean) => {
        expect(loggedIn).toBe(true);
      }
    );

    component.topics$.subscribe(
      (topics: Topic[]) => {
        expect(topics).toEqual(topicsMock);
      }
    );

    component.subscriptions$.subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toEqual(subscriptionsMock);
        done();
      }
    );

    expect(sessionServiceMock.$isLogged).toHaveBeenCalledTimes(1);
    expect(topicServiceMock.getAllTopics).toHaveBeenCalledTimes(1);
    expect(subscriptionServiceMock.getCurrentUserSubscriptions).toHaveBeenCalledTimes(1);
  });

  it('should load as expected when user not logged in', (done) => {
    // we have to manually reset mocks because they should have been called in ngOnInit in beforeEach, thus before this test
    topicServiceMock.getAllTopics.mockClear();
    sessionServiceMock.$isLogged.mockClear();
    subscriptionServiceMock.getCurrentUserSubscriptions.mockClear();

    sessionServiceMock.$isLogged.mockReturnValue(of(false));

    component.ngOnInit();

    // by default, user should be logged in, topics and user subscriptions should be retrieved
    component.loggedIn$.subscribe(
      (loggedIn: boolean) => {
        expect(loggedIn).toBe(false);
      }
    );

    component.topics$.subscribe(
      (topics: Topic[]) => {
        expect(topics).toEqual(topicsMock);
      }
    );

    component.subscriptions$.subscribe(
      (subscriptions: Subscription[]) => {
        expect(subscriptions).toEqual([]);
        done();
      }
    );

    expect(sessionServiceMock.$isLogged).toHaveBeenCalledTimes(1);
    expect(topicServiceMock.getAllTopics).toHaveBeenCalledTimes(1);
    expect(subscriptionServiceMock.getCurrentUserSubscriptions).not.toHaveBeenCalled();
  });

  it('should subscribe to topic and display confirmation', () => {
    subscriptionServiceMock.subscribe.mockReturnValue(of({userId: 1, createdAt: '2024-11-02T10:00:00', topic: {id: 2, title: 'Second topic title', description: 'Second topic description', createdAt: '2024-01-14T10:00:00', updatedAt: '2024-01-15T11:00:00'}}))

    component.onSubscribe(1);

    expect(notificationServiceMock.confirmation).toHaveBeenCalledTimes(1);
    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith("Abonnement pris en compte");

  });

  it('should do nothing when subscription fails', () => {
    subscriptionServiceMock.subscribe.mockReturnValue(throwError(() => new Error()));

    component.onSubscribe(1);

    expect(notificationServiceMock.confirmation).not.toHaveBeenCalled();
  });

  it('should unsubscribe from topic and display confirmation', () => {
    subscriptionServiceMock.unSubscribe.mockReturnValue(of(null));

    component.onUnsubscribe(1);

    expect(notificationServiceMock.confirmation).toHaveBeenCalledTimes(1);
    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith("Désabonnement pris en compte");
  });

  it('should do nothing when unsubscribe fails', () => {
    subscriptionServiceMock.unSubscribe.mockReturnValue(throwError(() => new Error()));

    component.onUnsubscribe(1);

    expect(notificationServiceMock.confirmation).not.toHaveBeenCalled();
  });
});
