import { TestBed } from '@angular/core/testing';

import { CommentService } from './comment.service';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting, TestRequest } from '@angular/common/http/testing';
import { Comment } from '../models/comment.interface';
import { User } from '../models/user.interface';
import { take } from 'rxjs';
import { CreateCommentRequest } from '../models/create-comment-request.interface';

describe('CommentService', () => {
  let service: CommentService;
  let mockHttpController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(CommentService);
    mockHttpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    jest.clearAllMocks()
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
  
  it('should retreive all post comments', (done) => {
    const mockComments:Comment[] = [
      {id: 1, author: {userName: "user1", email: "user1@example.com"} as User, postId: 1, content: "comment 1"} as Comment,
      {id: 2, author: {userName: "user2", email: "user2@example.com"} as User, postId: 1, content: "comment 2"} as Comment
    ];

    service.getPostComments(1).subscribe(
      (comments: Comment[]) => {
        expect(comments).toEqual(mockComments);
        done();
      }
    );

    const testRequest: TestRequest = mockHttpController.expectOne("api/posts/1/comments");
    expect(testRequest.request.method).toEqual("GET");
    testRequest.flush(mockComments);
  });

  it('should retreive all post comments from local cache when present', (done) => {
    const mockComments:Comment[] = [
      {id: 1, author: {userName: "user1", email: "user1@example.com"} as User, postId: 1, content: "comment 1"} as Comment,
      {id: 2, author: {userName: "user2", email: "user2@example.com"} as User, postId: 1, content: "comment 2"} as Comment
    ];

    service.getPostComments(1).pipe(take(1)).subscribe(
      (comments: Comment[]) => {
        expect(comments).toEqual(mockComments);
      }
    );
    // first call to getPostComments(1) should trigger an API request
    const testRequest: TestRequest = mockHttpController.expectOne("api/posts/1/comments");
    expect(testRequest.request.method).toEqual("GET");
    testRequest.flush(mockComments);

    service.getPostComments(1).pipe(take(1)).subscribe(
      (comments: Comment[]) => {
        expect(comments).toEqual(mockComments);
        done();
      }
    );
    // comments should be available from local cache, no api request should have been made
    mockHttpController.expectNone("api/posts/1/comments");
  });

  it('should not get comments from cache when post changes', (done) => {
    const mockComments:Comment[] = [
      {id: 1, author: {userName: "user1", email: "user1@example.com"} as User, postId: 1, content: "comment 1"} as Comment,
      {id: 2, author: {userName: "user2", email: "user2@example.com"} as User, postId: 1, content: "comment 2"} as Comment
    ];

    service.getPostComments(1).pipe(take(1)).subscribe(
      (comments: Comment[]) => {
        expect(comments).toEqual(mockComments);
      }
    );
    // first call to getPostComments(1) should trigger an API request
    const testRequest: TestRequest = mockHttpController.expectOne("api/posts/1/comments");
    expect(testRequest.request.method).toEqual("GET");
    testRequest.flush(mockComments);

    const secondMockComments:Comment[] = [
      {id: 3, author: {userName: "user3", email: "user3@example.com"} as User, postId: 2, content: "comment 3"} as Comment,
      {id: 4, author: {userName: "user4", email: "user4@example.com"} as User, postId: 2, content: "comment 4"} as Comment
    ];

    service.getPostComments(2).pipe(take(1)).subscribe(
      (comments: Comment[]) => {
        expect(comments).toEqual(secondMockComments);
        done();
      }
    );
    // an API request should have been triggered
    const secondRequest = mockHttpController.expectOne("api/posts/2/comments");
    expect(secondRequest.request.method).toEqual("GET");
    secondRequest.flush(secondMockComments);
  });

  it('should create comment', (done) => {
    const mockComment: Comment = {postId: 1, author: {userName: "testuser", email: "testuser@example.com"} as User, content: "Test comment"} as Comment;

    service.createPostComment(1, {content: "Test comment"} as CreateCommentRequest).pipe(
      take(1)
    ).subscribe((comment: Comment) => {
        expect(comment).toEqual(mockComment);
        done();
    });

    // an API request should have been triggered
    const testRequest: TestRequest = mockHttpController.expectOne("api/posts/1/comments");
    expect(testRequest.request.method).toEqual("POST");
    testRequest.flush(mockComment);
    
    // comments list should contain new Comment
    service.getPostComments(1).subscribe((comments: Comment[]) => {
      expect(comments).toContain(mockComment);
    })
  });

})

