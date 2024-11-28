import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MeComponent } from './me.component';
import { TopicComponent } from '../topics/list/topic/topic.component';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { SessionService } from '../../core/services/session.service';
import { NotificationService } from '../../core/services/notification.service';
import { CurrentUserService } from '../../core/services/current-user.service';
import { CurrentUserSubscriptionService } from '../../core/services/current-user-subscription.service';
import { User } from '../../core/models/user.interface';
import { of, throwError } from 'rxjs';
import { Subscription } from '../../core/models/subscription.interface';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let sessionServiceMock: jest.Mocked<SessionService>;
  let notificationServiceMock: jest.Mocked<NotificationService>;
  let userServiceMock: jest.Mocked<CurrentUserService>;
  let subscriptionServiceMock: jest.Mocked<CurrentUserSubscriptionService>;
  const mockUser: User = {id: 1, username: 'testuser', email: 'test@example.com', createdAt: '2024-11-01T10:00:00'};
  const mockSubscriptions: Subscription[] = [
    {userId: 1, createdAt: '2024-11-02T10:00:00', topic: {id: 1, title: 'Topic title', description: 'Topic description', createdAt: '2024-01-12T10:00:00', updatedAt: '2024-01-13T11:00:00'}},
    {userId: 1, createdAt: '2024-11-02T10:00:00', topic: {id: 2, title: 'Second topic title', description: 'Second topic description', createdAt: '2024-01-14T10:00:00', updatedAt: '2024-01-15T11:00:00'}},
  ];

  beforeEach(async () => {
    sessionServiceMock = {
      logOut: jest.fn(),
    } as unknown as jest.Mocked<SessionService>;

    notificationServiceMock = {
      confirmation: jest.fn(),
    } as unknown as jest.Mocked<NotificationService>;

    userServiceMock = {
      getCurrentUser: jest.fn(),
      updateCurrentUser: jest.fn()
    }as unknown as jest.Mocked<CurrentUserService>;

    subscriptionServiceMock = {
      getCurrentUserSubscriptions: jest.fn(),
      unSubscribe: jest.fn()
    }as unknown as jest.Mocked<CurrentUserSubscriptionService>;

    await TestBed.configureTestingModule({
      imports: [
        MeComponent,
        TopicComponent, 
        MatButtonModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        NoopAnimationsModule
      ],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: SessionService,
          useValue: sessionServiceMock
        },
        {
          provide: NotificationService,
          useValue: notificationServiceMock
        },
        {
          provide: CurrentUserService,
          useValue: userServiceMock
        },
        {
          provide: CurrentUserSubscriptionService,
          useValue: subscriptionServiceMock
        }
      ]
    })
    .compileComponents();

    
    userServiceMock.getCurrentUser.mockReturnValue(of(mockUser));
    subscriptionServiceMock.getCurrentUserSubscriptions.mockReturnValue(of(mockSubscriptions));

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    // TestBed.inject(CurrentUserService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should logout', () => {
    component.logout();
    expect(sessionServiceMock.logOut).toHaveBeenCalled();
  });

  it('should init with user and subscriptions data', (done) => {
    component.user$.subscribe((user: User) => {
      expect(user).toEqual(mockUser);
    });
    component.subscriptions$.subscribe((subscriptions: Subscription[]) => {
      expect(subscriptions).toEqual(mockSubscriptions);
      done();
    });
  });

  it('should update user', () => {
    userServiceMock.updateCurrentUser.mockReturnValue(of(mockUser));
    
    component.updateCurrentUser();

    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith("Modifications enregistrées");
  });

  it('should not display confirmation when user update fails', () => {
    userServiceMock.updateCurrentUser.mockReturnValue(throwError(() => new Error()));
    
    component.updateCurrentUser();

    expect(notificationServiceMock.confirmation).not.toHaveBeenCalled();
  });

  it('should unsubscribe', () => {
    subscriptionServiceMock.unSubscribe.mockReturnValue(of(null));
    
    component.onUnsubscribe(1);

    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith("Désabonnement pris en compte");
  });

  it('should not display confirmation when unsubscribe fails', () => {
    subscriptionServiceMock.unSubscribe.mockReturnValue(throwError(() => new Error()));
    
    component.onUnsubscribe(1);

    expect(notificationServiceMock.confirmation).not.toHaveBeenCalled();
  });
});
