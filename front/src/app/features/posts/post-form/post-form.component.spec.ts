import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PostFormComponent } from './post-form.component';
import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { of, throwError } from 'rxjs';
import { NotificationService } from '../../../core/services/notification.service';
import { PostService } from '../../../core/services/post.service';
import { TopicService } from '../../../core/services/topic.service';
import { Router } from '@angular/router';
import { Topic } from '../../../core/models/topic.interface';
import { ApiError } from '../../../core/errors/api-error';
import { Post } from '../../../core/models/post.interface';
import { User } from '../../../core/models/user.interface';
import { CreatePostRequest } from '../../../core/models/create-post-request.interface';

describe('PostFormComponent', () => {
  let component: PostFormComponent;
  let fixture: ComponentFixture<PostFormComponent>;
  let postServiceMock: jest.Mocked<PostService>;
  let topicServiceMock: jest.Mocked<TopicService>;
  let notificationServiceMock: jest.Mocked<NotificationService>;
  let routerMock: jest.Mocked<Router>;
  const topicsMock: Topic[] = [
    {id: 1, title: 'Test topic'} as Topic,
    {id: 2, title: 'Second test Topic'} as Topic
  ];
  

  beforeEach(async () => {
    postServiceMock = {
      createPost: jest.fn()
    } as unknown as jest.Mocked<PostService>;
    topicServiceMock = {
      getAllTopics: jest.fn().mockReturnValue(of([topicsMock]))
    } as unknown as jest.Mocked<TopicService>;
    notificationServiceMock = {
      confirmation: jest.fn(),
      error: jest.fn()
    } as unknown as jest.Mocked<NotificationService>;
    routerMock = {
      navigate: jest.fn()
    } as unknown as jest.Mocked<Router>;

    await TestBed.configureTestingModule({
      imports: [
        PostFormComponent,
        NoopAnimationsModule
      ],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: PostService, useValue: postServiceMock},
        { provide: TopicService, useValue: topicServiceMock},
        { provide: NotificationService, useValue: notificationServiceMock},
        { provide: Router, useValue: routerMock},
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
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  })

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to /posts when no topic available', () => {
    // we have to manually reset mocks because they should have been called in ngOnInit in beforeEach, thus before this test
    topicServiceMock.getAllTopics.mockClear();
    // by default, mocked topicService returns topicsMock
    // we thus have to set mocked topic service to return an empty list
    topicServiceMock.getAllTopics.mockReturnValue(of([]));
    
    // then manually run ngOnInit
    component.ngOnInit();
    // and manually subscribe to the topics$ observable because it is done in the template with the async pipe
    component.topics$.subscribe();

    expect(routerMock.navigate).toHaveBeenCalledTimes(1);
    expect(routerMock.navigate).toHaveBeenCalled();
    expect(notificationServiceMock.error).toHaveBeenCalledTimes(1);
    expect(notificationServiceMock.error).toHaveBeenCalledWith("Aucun thème disponible, impossible de créer un article.");
  });

  it('should redirect to /posts when topic retrieval throws error', () => {
    // we have to manually reset mocks because they should have been called in ngOnInit in beforeEach, thus before this test
    topicServiceMock.getAllTopics.mockClear();
    // by default, mocked topicService returns topicsMock
    // we thus have to set mocked topic service to throw an error on retrieval
    topicServiceMock.getAllTopics.mockReturnValue(throwError(() => new ApiError({status: 500} as HttpErrorResponse)));
    
    // then manually run ngOnInit
    component.ngOnInit();
    // and manually subscribe to the topics$ observable because it is done in the template with the async pipe
    component.topics$.subscribe();

    expect(routerMock.navigate).toHaveBeenCalledTimes(1);
    expect(routerMock.navigate).toHaveBeenCalledWith(["/posts"]);
    expect(notificationServiceMock.error).toHaveBeenCalledTimes(1);
    expect(notificationServiceMock.error).toHaveBeenCalledWith("Aucun thème disponible, impossible de créer un article.");
  });

  it('should load post creation form', () => {
    // by default, mocked topicService returns topicsMock, so the form should be generated as expected
    expect(routerMock.navigate).not.toHaveBeenCalled();
    expect(notificationServiceMock.error).not.toHaveBeenCalled();
    expect(component.form).not.toBeNull();
    expect(component.topicId).not.toBeNull();
    expect(component.title).not.toBeNull();
    expect(component.content).not.toBeNull();
  })

  it('should create post', () => {
    const date: string = '2024-11-28T11:00:00';
    const createdPostMock: Post = {
      id: 1, title: 'Test post', content: 'Test post content', createdAt: date, updatedAt: date, 
      author: {id: 1, username: 'testuser'} as User,
      topic: topicsMock[0]
    };
    postServiceMock.createPost.mockReturnValue(of(createdPostMock))

    component.form = {
      value: {
        topicId: 1,
        title: 'Post title',
        content: 'Post content'
      }
    } as any;
    component.submit();

    expect(postServiceMock.createPost).toHaveBeenCalledTimes(1);
    expect(postServiceMock.createPost).toHaveBeenCalledWith({
      topicId: 1,
      title: 'Post title',
      content: 'Post content'
    } as CreatePostRequest);
    expect(routerMock.navigate).toHaveBeenCalledTimes(1);
    expect(routerMock.navigate).toHaveBeenCalledWith(["/posts"]);
    expect(notificationServiceMock.confirmation).toHaveBeenCalledTimes(1);
    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith("Votre article a bien été créé");
  })

  it('should do nothing when post creation fails', () => {
    postServiceMock.createPost.mockReturnValue(throwError(() => new ApiError({status: 500} as HttpErrorResponse)));
    component.form = {
      value: {
        topicId: 1,
        title: 'Post title',
        content: 'Post content'
      }
    } as any;

    component.submit();
    
    expect(routerMock.navigate).not.toHaveBeenCalled();
    expect(notificationServiceMock.confirmation).not.toHaveBeenCalled();
  })
});
