import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PostDetailComponent } from './post-detail.component';
import { PostService } from '../../../core/services/post.service';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { CommentService } from '../../../core/services/comment.service';
import { of, throwError } from 'rxjs';
import { ApiError } from '../../../core/errors/api-error';
import { Post } from '../../../core/models/post.interface';
import { Topic } from '../../../core/models/topic.interface';
import { User } from '../../../core/models/user.interface';
import { Comment } from '../../../core/models/comment.interface';
import { NotificationService } from '../../../core/services/notification.service';

describe('PostDetailComponent', () => {
  let component: PostDetailComponent;
  let fixture: ComponentFixture<PostDetailComponent>;
  let postServiceMock: jest.Mocked<PostService>;
  let commentServiceMock: jest.Mocked<CommentService>;
  let routerMock: jest.Mocked<Router>;
  let activatedRouteMock: jest.Mocked<ActivatedRoute>;
  let notificationServiceMock: jest.Mocked<NotificationService>;

  beforeEach(async () => {
    postServiceMock = {
      getPostById: jest.fn()
    } as unknown as jest.Mocked<PostService>;

    commentServiceMock = {
      createPostComment: jest.fn(),
      getPostComments: jest.fn()
    } as unknown as jest.Mocked<CommentService>;

    routerMock = { 
      navigate: jest.fn()
    } as unknown as jest.Mocked<Router>;

    activatedRouteMock = {
      params: of({'id': '1'})
    } as unknown as jest.Mocked<ActivatedRoute>;

    notificationServiceMock = {
      confirmation: jest.fn()
    } as unknown as jest.Mocked<NotificationService>;

    await TestBed.configureTestingModule({
      imports: [PostDetailComponent],
      providers: [
        { provide: Router, useValue: routerMock },
        { provide: PostService, useValue: postServiceMock },
        { provide: CommentService, useValue: commentServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: NotificationService, useValue: notificationServiceMock }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });
  
  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
  it('should redirect to posts list when post not found', () => {
    postServiceMock.getPostById.mockReturnValue(throwError(() => new ApiError({status: 404} as HttpErrorResponse)));
    
    component.ngOnInit();
    component.post$.subscribe();
    expect(routerMock.navigate).toHaveBeenCalledTimes(1);
    expect(routerMock.navigate).toHaveBeenCalledWith(["/posts"]);
  })
  
  it('should load post and comments list', (done) => {
    // we have to manually reset mocks because they should have been called in ngOnInit in beforeEach, thus before this test
    postServiceMock.getPostById.mockClear();
    commentServiceMock.getPostComments.mockClear();

    const postMock: Post = {id: 1, title: 'Test post', content: 'Test post content', createdAt: '2024-11-03T23:12:12', updatedAt: '2024-11-03T23:12:12', topic: {id: 2, title: 'TestTopic'} as Topic, author: {id: 1, username: 'testuser'} as User};
    postServiceMock.getPostById.mockReturnValue(of(postMock));
    
    const commentsMock: Comment[] = [
      {id: 1, content: 'Comment content', postId: 1, author: {id: 2, username: 'otheruser'} as User} as Comment,
      {id: 2, content: 'Second comment content', postId: 1, author: {id: 3, username: 'thirduser'} as User} as Comment
    ]
    commentServiceMock.getPostComments.mockReturnValue(of(commentsMock));

    component.ngOnInit();
    component.post$.subscribe((post: Post) => {
      expect(post).toEqual(postMock);
    });
    component.comments$.subscribe((comments: Comment[]) => {
      expect(comments).toEqual(commentsMock);
      done();
    });
    expect(postServiceMock.getPostById).toHaveBeenCalledTimes(1);
    expect(postServiceMock.getPostById).toHaveBeenCalledWith("1");
    expect(commentServiceMock.getPostComments).toHaveBeenCalledTimes(1);
    expect(commentServiceMock.getPostComments).toHaveBeenCalledWith("1");
  })

  it('should get the content form element when post found', () => {
    // we have to manually reset mocks because they should have been called in ngOnInit in beforeEach, thus before this test
    postServiceMock.getPostById.mockClear();

    const postMock: Post = {id: 1, title: 'Test post', content: 'Test post content', createdAt: '2024-11-03T23:12:12', updatedAt: '2024-11-03T23:12:12', topic: {id: 2, title: 'TestTopic'} as Topic, author: {id: 1, username: 'testuser'} as User};
    postServiceMock.getPostById.mockReturnValue(of(postMock));
    
    component.ngOnInit();
    expect(component.content).not.toBeNull();
  });

  it('should create new comment for the content post', () => {
    // we have to manually reset mocks because they should have been called in ngOnInit in beforeEach, thus before this test
    postServiceMock.getPostById.mockClear();
    commentServiceMock.getPostComments.mockClear();

    const postMock: Post = {id: 1, title: 'Test post', content: 'Test post content', createdAt: '2024-11-03T23:12:12', updatedAt: '2024-11-03T23:12:12', topic: {id: 2, title: 'TestTopic'} as Topic, author: {id: 1, username: 'testuser'} as User};
    postServiceMock.getPostById.mockReturnValue(of(postMock));
    
    const commentsMock: Comment[] = [
      {id: 1, content: 'Comment content', postId: 1, author: {id: 2, username: 'otheruser'} as User} as Comment,
      {id: 2, content: 'Second comment content', postId: 1, author: {id: 3, username: 'thirduser'} as User} as Comment
    ]
    commentServiceMock.getPostComments.mockReturnValue(of(commentsMock));

    const newCommentMock: Comment =  {id: 3, content: 'Newly created comment content', postId: 1, author: {id: 3, username: 'testuser'} as User} as Comment;
    commentServiceMock.createPostComment.mockReturnValue(of(newCommentMock));
    
    component.ngOnInit();

    component.form.controls['content'].setValue("Newly created comment content");
    component.submit();

    expect(notificationServiceMock.confirmation).toHaveBeenCalledWith("Merci pour votre commentaire !");
  });

});
