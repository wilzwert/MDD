import { TestBed } from '@angular/core/testing';

import { PostService } from './post.service';
import { Post } from '../models/post.interface';
import { User } from '../models/user.interface';
import { Topic } from '../models/topic.interface';
import { HttpTestingController, provideHttpClientTesting, TestRequest } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { Subscription } from '../models/subscription.interface';
import { CurrentUserSubscriptionService } from './current-user-subscription.service';
import { PostSort } from '../models/post-sort.interface';

describe('PostService', () => {
  let service: PostService;
  let mockHttpController: HttpTestingController;
  let mockSubscriptionsSubject: BehaviorSubject<Subscription[] | null> = new BehaviorSubject<Subscription[] | null>(null);
  const mockSubscriptions: Subscription[] = [
    {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 1, title: 'Test topic', description: 'This is a test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}},
    {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 2, title: 'Other test topic', description: 'This is another test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}}
  ];
  let spyOnClearCache: any;

  beforeEach(() => {
    const mockUserSubscriptionService = {
      getCurrentUserSubscriptions: jest.fn(() => {return mockSubscriptionsSubject.asObservable();}),
    };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {provide: CurrentUserSubscriptionService, useValue: mockUserSubscriptionService}
      ]
    });
    service = TestBed.inject(PostService);
    mockHttpController = TestBed.inject(HttpTestingController);
    TestBed.inject(CurrentUserSubscriptionService);

    spyOnClearCache = jest.spyOn(service, 'clearCache');
  });

  afterEach(() => {
    jest.clearAllMocks();
  });
  
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  
  it('should retrieve all posts', (done) => {
    const mockPosts: Post[] = [
      {id: 1, title: "Test post", content: "Test post content", author: {id: 1, "username": "testuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post
    ];

    service.getAllPosts().subscribe(
      (posts: Post[]) => {
        expect(posts).toEqual(mockPosts);
        done();
      }
    );

    const getRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(getRequest.request.method).toEqual("GET");
    getRequest.flush(mockPosts);
  });

  it('should retrieve all posts from cache when present', (done) => {
    const mockPosts: Post[] = [
      {id: 1, title: "Test post", content: "Test post content", author: {id: 1, "username": "testuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post
    ];

    // first retrieval should populate local cache
    service.getAllPosts().subscribe(
      (posts: Post[]) => {
        expect(posts).toEqual(mockPosts);
      }
    );

    const getRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(getRequest.request.method).toEqual("GET");
    getRequest.flush(mockPosts);

    // second retrieval should not trigger API request as local cache should be present
    service.getAllPosts().subscribe(
      (posts: Post[]) => {
        expect(posts).toEqual(mockPosts);
        done();
      }
    );

    mockHttpController.expectNone("api/posts");
  });
  
  it('should clear local cache and trigger api request on next retrieval', (done) => {
    // to manually check the effect of cache clearing on API requests,
    // we have to do a first retrieval, manually clear the local cache
    // and do another retrieval to ensure API request is triggered

    // first retrieval should trigger an API request, as no local cache is present on first call
    const mockPosts: Post[] = [
      {id: 1, title: "Test post", content: "Test post content", author: {id: 1, "username": "testuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post
    ];

    service.getAllPosts().subscribe(
      (posts: Post[]) => {
        expect(posts).toEqual(mockPosts);
      }
    );
    const getRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(getRequest.request.method).toEqual("GET");
    getRequest.flush(mockPosts);
    // cache has thus no reason to get cleared
    expect(spyOnClearCache).not.toHaveBeenCalled();
    
    // let's manually clear local cache
    service.clearCache();
    // clean up the spy to avoid previous invocation to interfere with further tests
    spyOnClearCache.mockClear();

    // it makes sense to check that no API request has been made on cache clearing
    // as earlier versions of the post service did trigger requests because of a misuse of the local BehaviorSubject
    mockHttpController.expectNone("api/posts");

    // next retrieval should trigger an API request
    service.getAllPosts().subscribe(
      (posts: Post[]) => {
        expect(posts).toEqual(mockPosts);
        done();
      }
    );
    const nextRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(nextRequest.request.method).toEqual("GET");
    nextRequest.flush(mockPosts);
    expect(spyOnClearCache).not.toHaveBeenCalled();
  });
  
  it('should clear local cache when local posts cache present and user subscriptions change ', () => {
    expect(spyOnClearCache).not.toHaveBeenCalled();

    const mockPosts: Post[] = [
      {id: 1, title: "Test post", content: "Test post content", author: {id: 1, "username": "testuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post
    ];

    // posts retrieval should trigger an API request
    service.getAllPosts().subscribe(
      (posts: Post[]) => {
        expect(posts).toEqual(mockPosts);
      }
    );
    const retrievalRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(retrievalRequest.request.method).toEqual("GET");
    retrievalRequest.flush(mockPosts);
    // by default, local cache has no reason to be cleared before first post retrieval 
    expect(spyOnClearCache).not.toHaveBeenCalled();

    // subscriptions change
    mockSubscriptionsSubject.next(mockSubscriptions);

    // then local cache should have been cleard
    expect(spyOnClearCache).toHaveBeenCalled();
  });

  it('should retrieve a Post by its id', (done) => {
    const mockPost = {id: 1, title: "Test post", content: "Test post content", author: {id: 1, "username": "testuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    service.getPostById("1").subscribe(
      (post: Post) => {
        expect(post).toEqual(mockPost);
        done();
      }
    );
    const retrievalRequest: TestRequest = mockHttpController.expectOne("api/posts/1");
    expect(retrievalRequest.request.method).toEqual("GET");
    retrievalRequest.flush(mockPost);
  });

  it('should create a new post and update local cache when present', (done) => {
    const mockPosts: Post[] = [
      {id: 1, title: "Test post", content: "Test post content", author: {id: 1, "username": "testuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post
    ];
    const mockCreatedPost: Post = {id: 2, title: "Created post", content: "Created post content", author: {id: 1, "username": "testuser"} as User, topic: {id: 2, title: "Other test topic"} as Topic} as Post;

    service.getAllPosts().subscribe(
      (posts: Post[]) => {
        expect(posts).toEqual(mockPosts);
      }
    );

    const getAllRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(getAllRequest.request.method).toEqual("GET");
    getAllRequest.flush(mockPosts);

    service.createPost({topicId: 2, title: "Created post", content:" Created post content"}).subscribe(
      (post: Post) => {
        expect(post).toEqual(mockCreatedPost);
      }
    );
    const createTestRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(createTestRequest.request.method).toEqual("POST");
    createTestRequest.flush(mockCreatedPost);

    service.getAllPosts().subscribe(
      (posts: Post[]) => {
        expect(posts).toHaveLength(2);
        expect(posts).toContain(mockCreatedPost);
        done();
      }
    );
    mockHttpController.expectNone("api/posts");
  });
  
  it('should create a new post and not populate local cache when empty', (done) => {
    const mockCreatedPost: Post = {id: 2, title: "Created post", content: "Created post content", author: {id: 1, "username": "testuser"} as User, topic: {id: 2, title: "Other test topic"} as Topic} as Post;
    const mockPosts: Post[] = [
      {id: 1, title: "Test post", content: "Test post content", author: {id: 1, "username": "testuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post,
      mockCreatedPost
    ];
    
    service.createPost({topicId: 2, title: "Created post", content: "Created post content"}).subscribe(
      (post: Post) => {
        expect(post).toEqual(mockCreatedPost);
      }
    );
    const createTestRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(createTestRequest.request.method).toEqual("POST");
    createTestRequest.flush(mockCreatedPost);

    // posts retrieval should trigger an API request, as local cache was not present when new post was created
    service.getAllPosts().subscribe(
      (posts: Post[]) => {
        expect(posts).toHaveLength(2);
        expect(posts).toContain(mockCreatedPost);
        done();
      }
    );
    const testRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(testRequest.request.method).toEqual("GET");
    testRequest.flush(mockPosts);
  });

  it('should retrieve all posts from API when cache present but expired', (done) => {
    const mockPosts: Post[] = [
      {id: 1, title: "Test post", content: "Test post content", author: {id: 1, "username": "testuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post
    ];

    // first posts retrieval should trigger an API request and populate cache
    service.getAllPosts().subscribe(
      (posts: Post[]) => {
        expect(posts).toEqual(mockPosts);
      }
    );
    const retrievalRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(retrievalRequest.request.method).toEqual("GET");
    retrievalRequest.flush(mockPosts);

    let hasBeenCalled: boolean = false;
    // shouldReload will return true one time only
    const spyOnShouldReload = jest.spyOn(service, 'shouldReload').mockImplementation(() => {const res = hasBeenCalled ? false : true; hasBeenCalled = true; return res;});

    // second posts retrieval when cache expired should trigger an API request and repopulate cache
    service.getAllPosts().subscribe(
      (posts: Post[]) => {
        expect(posts).toEqual(mockPosts);
        done();
        
      }
    );
    const secondRetrievalRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(secondRetrievalRequest.request.method).toEqual("GET");
    secondRetrievalRequest.flush(mockPosts);

    expect(spyOnShouldReload).toHaveBeenCalled();
  });

  it('should sort Posts by creation date desc ', (done) => {
    const post1: Post = {id: 1, title: "Test post", content: "Test post content", createdAt: '2024-11-02T11:00:00', author: {id: 1, "username": "testuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post2: Post = {id: 2, title: "Second test post", content: "Second test post content", createdAt: '2024-10-28T08:00:00', author: {id: 1, "username": "testuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post3: Post = {id: 3, title: "Third test post", content: "Third test post content", createdAt: '2024-09-26T08:00:00', author: {id: 2, "username": "testuser2"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post4: Post = {id: 4, title: "Fourth test post", content: "Fourth test post content", createdAt: '2024-09-28T08:00:00', author: {id: 2, "username": "testuser2"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;

    const mockPosts = [post1, post2, post3, post4];
    const expectedMockPosts = [post1, post2, post4, post3]
    // second posts retrieval when cache expired should trigger an API request and repopulate cache
    service.getAllPosts({sortByAuthor: undefined, sortByPost: true, orderByAuthorAscending: undefined, orderByPostAscending: false} as PostSort).subscribe(
      (posts: Post[]) => {
        expect(posts).toEqual(expectedMockPosts);
        done();
      }
    );

    const retrievalRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(retrievalRequest.request.method).toEqual("GET");
    retrievalRequest.flush(mockPosts);
    
  });

  it('should sort Posts by creation date asc', (done) => {
    const post1: Post = {id: 1, title: "Test post", content: "Test post content", createdAt: '2024-11-02T11:00:00', author: {id: 1, "username": "testuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post2: Post = {id: 2, title: "Second test post", content: "Second test post content", createdAt: '2024-10-28T08:00:00', author: {id: 1, "username": "testuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post3: Post = {id: 3, title: "Third test post", content: "Third test post content", createdAt: '2024-09-26T08:00:00', author: {id: 2, "username": "testuser2"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post4: Post = {id: 4, title: "Fourth test post", content: "Fourth test post content", createdAt: '2024-09-28T08:00:00', author: {id: 2, "username": "testuser2"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;

    const mockPosts = [post1, post2, post3, post4];
    const expectedMockPosts = [post3, post4, post2, post1]
    // second posts retrieval when cache expired should trigger an API request and repopulate cache
    service.getAllPosts({sortByAuthor: undefined, sortByPost: true, orderByAuthorAscending: undefined, orderByPostAscending: true} as PostSort).subscribe(
      (posts: Post[]) => {
        expect(posts).toEqual(expectedMockPosts);
        done();
      }
    );

    const retrievalRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(retrievalRequest.request.method).toEqual("GET");
    retrievalRequest.flush(mockPosts);

  });

  it('should sort Posts by username asc and creation date desc', (done) => {
    const post1: Post = {id: 1, title: "Test post", content: "Test post content", createdAt: '2024-11-02T11:00:00', author: {id: 1, "username": "btestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post2: Post = {id: 2, title: "Second test post", content: "Second test post content", createdAt: '2024-10-28T08:00:00', author: {id: 1, "username": "btestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post3: Post = {id: 3, title: "Third test post", content: "Third test post content", createdAt: '2024-09-26T08:00:00', author: {id: 2, "username": "atestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post4: Post = {id: 4, title: "Fourth test post", content: "Fourth test post content", createdAt: '2024-09-28T08:00:00', author: {id: 2, "username": "atestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;

    const mockPosts = [post1, post2, post3, post4];
    const expectedMockPosts = [post4, post3, post1, post2];
    // second posts retrieval when cache expired should trigger an API request and repopulate cache
    service.getAllPosts({sortByAuthor: true, sortByPost: undefined, orderByAuthorAscending: true, orderByPostAscending: undefined} as PostSort).subscribe(
      (posts: Post[]) => {
        expect(posts).toEqual(expectedMockPosts);
        done();
      }
    );

    const retrievalRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(retrievalRequest.request.method).toEqual("GET");
    retrievalRequest.flush(mockPosts);
  });

  it('should sort Posts by username asc and creation date asc', (done) => {
    const post1: Post = {id: 1, title: "Test post", content: "Test post content", createdAt: '2024-11-02T11:00:00', author: {id: 1, "username": "btestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post2: Post = {id: 2, title: "Second test post", content: "Second test post content", createdAt: '2024-10-28T08:00:00', author: {id: 1, "username": "btestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post3: Post = {id: 3, title: "Third test post", content: "Third test post content", createdAt: '2024-09-26T08:00:00', author: {id: 2, "username": "atestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post4: Post = {id: 4, title: "Fourth test post", content: "Fourth test post content", createdAt: '2024-09-28T08:00:00', author: {id: 2, "username": "atestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;

    const mockPosts = [post1, post2, post3, post4];
    const expectedMockPosts = [post3, post4, post2, post1];
    // second posts retrieval when cache expired should trigger an API request and repopulate cache
    service.getAllPosts({sortByAuthor: true, sortByPost: true, orderByAuthorAscending: true, orderByPostAscending: true} as PostSort).subscribe(
      (posts: Post[]) => {
        expect(posts).toEqual(expectedMockPosts);
        done();
      }
    );

    const retrievalRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(retrievalRequest.request.method).toEqual("GET");
    retrievalRequest.flush(mockPosts);

  });

  it('should sort Posts by username desc and creation date desc', (done) => {
    const post1: Post = {id: 1, title: "Test post", content: "Test post content", createdAt: '2024-11-02T11:00:00', author: {id: 1, "username": "btestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post2: Post = {id: 2, title: "Second test post", content: "Second test post content", createdAt: '2024-10-28T08:00:00', author: {id: 1, "username": "btestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post3: Post = {id: 3, title: "Third test post", content: "Third test post content", createdAt: '2024-09-26T08:00:00', author: {id: 2, "username": "atestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post4: Post = {id: 4, title: "Fourth test post", content: "Fourth test post content", createdAt: '2024-09-28T08:00:00', author: {id: 2, "username": "atestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;

    const mockPosts = [post1, post2, post3, post4];
    const expectedMockPosts = [post1, post2, post4, post3];
    // second posts retrieval when cache expired should trigger an API request and repopulate cache
    service.getAllPosts({sortByAuthor: true, sortByPost: undefined, orderByAuthorAscending: false, orderByPostAscending: undefined} as PostSort).subscribe(
      (posts: Post[]) => {
        expect(posts).toEqual(expectedMockPosts);
        done();
      }
    );

    const retrievalRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(retrievalRequest.request.method).toEqual("GET");
    retrievalRequest.flush(mockPosts);

  });

  it('should sort Posts by username desc and creation date asc', (done) => {
    const post1: Post = {id: 1, title: "Test post", content: "Test post content", createdAt: '2024-11-02T11:00:00', author: {id: 1, "username": "btestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post2: Post = {id: 2, title: "Second test post", content: "Second test post content", createdAt: '2024-10-28T08:00:00', author: {id: 1, "username": "btestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post3: Post = {id: 3, title: "Third test post", content: "Third test post content", createdAt: '2024-09-26T08:00:00', author: {id: 2, "username": "atestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
    const post4: Post = {id: 4, title: "Fourth test post", content: "Fourth test post content", createdAt: '2024-09-28T08:00:00', author: {id: 2, "username": "atestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;

    const mockPosts = [post1, post2, post3, post4];
    const expectedMockPosts = [post2, post1, post3, post4];
    // second posts retrieval when cache expired should trigger an API request and repopulate cache
    service.getAllPosts({sortByAuthor: true, sortByPost: undefined, orderByAuthorAscending: false, orderByPostAscending: true} as PostSort).subscribe(
      (posts: Post[]) => {
        expect(posts).toEqual(expectedMockPosts);
        done();
      }
    );

    const retrievalRequest: TestRequest = mockHttpController.expectOne("api/posts");
    expect(retrievalRequest.request.method).toEqual("GET");
    retrievalRequest.flush(mockPosts);

  });
});
