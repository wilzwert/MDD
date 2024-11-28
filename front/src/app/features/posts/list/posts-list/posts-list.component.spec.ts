import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostsListComponent } from './posts-list.component';
import { Post } from '../../../../core/models/post.interface';
import { Topic } from '../../../../core/models/topic.interface';
import { User } from '../../../../core/models/user.interface';
import { PostService } from '../../../../core/services/post.service';
import { of } from 'rxjs';
import { ActivatedRoute, provideRouter, RouterLink } from '@angular/router';
import { PostComponent } from '../post/post.component';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { AsyncPipe } from '@angular/common';
import { PostSort } from '../../../../core/models/post-sort.interface';

describe('PostsListComponent', () => {
  let component: PostsListComponent;
  let fixture: ComponentFixture<PostsListComponent>;

  let postServiceMock: jest.Mocked<PostService>;

  const post1: Post = {id: 1, title: "Test post", content: "Test post content", createdAt: '2024-11-02T11:00:00', author: {id: 1, "username": "btestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
  const post2: Post = {id: 2, title: "Second test post", content: "Second test post content", createdAt: '2024-10-28T08:00:00', author: {id: 1, "username": "btestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
  const post3: Post = {id: 3, title: "Third test post", content: "Third test post content", createdAt: '2024-09-26T08:00:00', author: {id: 2, "username": "atestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
  const post4: Post = {id: 4, title: "Fourth test post", content: "Fourth test post content", createdAt: '2024-09-28T08:00:00', author: {id: 2, "username": "atestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
  const defaultPosts: Post[] = [post1, post2, post3, post4];
  
  beforeEach(async () => {
    postServiceMock = {
      getAllPosts: jest.fn()
    } as unknown as jest.Mocked<PostService>;

    await TestBed.configureTestingModule({
      imports: [
        PostsListComponent,
        PostComponent,
        AsyncPipe,
        RouterLink,
        MatButtonModule, 
        MatTooltipModule, 
        MatMenuModule,
        MatIconModule
      ],
      providers: [
        provideRouter([]),
        {provide: PostService, useValue: postServiceMock},
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    
    // first call always returns posts sorted by creation date desc
    postServiceMock.getAllPosts.mockReturnValueOnce(of(defaultPosts));
    component.ngOnInit();
  });

  afterEach(() => {
    jest.clearAllMocks();
  })

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have default posts on init', (done) => {
    component.posts$.subscribe((posts: Post[]) => {
      expect(posts).toEqual(defaultPosts);
      done();
    })
  })

  it('should have posts sorted by creation date asc', (done) => {
    const sortedPosts: Post[] = [post3, post4, post2, post1];
    postServiceMock.getAllPosts.mockReturnValueOnce(of(sortedPosts));
    const expectedSort: PostSort = {sortByAuthor: undefined, orderByAuthorAscending: undefined, sortByPost: true, orderByPostAscending: true};

    component.sortOrder();
    expect(component.orderAscending).toBe(true);
    expect(component.sort).toEqual(expectedSort);
    
    component.posts$.subscribe((posts: Post[]) => {
      expect(posts).toEqual(sortedPosts);
      done();
    })
  })

  it('should have posts sorted by author asc and creation date desc', (done) => {
    const sortedPosts: Post[] = [post3, post4, post1, post1];
    postServiceMock.getAllPosts.mockReturnValueOnce(of(sortedPosts));
    const expectedSort: PostSort = {sortByAuthor: true, orderByAuthorAscending: true, sortByPost: undefined, orderByPostAscending: false};

    component.sortByAuthor();
    expect(component.orderAscending).toBe(true);
    expect(component.sort).toEqual(expectedSort);
    
    component.posts$.subscribe((posts: Post[]) => {
      expect(posts).toEqual(sortedPosts);
      done();
    })
  })

  it('should have posts sorted by author asc and creation date asc', (done) => {
    // first sort by post creation asc
    const sortedPosts: Post[] = [post3, post4, post2, post1];
    postServiceMock.getAllPosts.mockReturnValueOnce(of(sortedPosts));
    const expectedSort: PostSort = {sortByAuthor: undefined, orderByAuthorAscending: undefined, sortByPost: true, orderByPostAscending: true};
    component.sortOrder();

    component.posts$.subscribe((posts: Post[]) => {
      expect(posts).toEqual(sortedPosts);
    })

    //then by author asc
    const nextSortedPosts: Post[] = [post3, post4, post2, post1];
    postServiceMock.getAllPosts.mockReturnValueOnce(of(nextSortedPosts));
    const nextExpectedSort: PostSort = {sortByAuthor: true, orderByAuthorAscending: true, sortByPost: undefined, orderByPostAscending: true};
    component.sortByAuthor();
    expect(component.orderAscending).toBe(true);
    expect(component.sort).toEqual(nextExpectedSort);
    
    component.posts$.subscribe((posts: Post[]) => {
      expect(posts).toEqual(nextSortedPosts);
      done();
    })
  })

  it('should have posts sorted by author desc and creation date asc', (done) => {
    // first sort by post creation asc
    const sortedPosts: Post[] = [post3, post4, post2, post1];
    postServiceMock.getAllPosts.mockReturnValueOnce(of(sortedPosts));
    const expectedSort: PostSort = {sortByAuthor: undefined, orderByAuthorAscending: undefined, sortByPost: true, orderByPostAscending: true};
    component.sortOrder();

    component.posts$.subscribe((posts: Post[]) => {
      expect(posts).toEqual(sortedPosts);
    })

    //then by author (default asc)
    const secondSortedPosts: Post[] = [post3, post4, post2, post1];
    postServiceMock.getAllPosts.mockReturnValueOnce(of(secondSortedPosts));
    const secondExpectedSort: PostSort = {sortByAuthor: true, orderByAuthorAscending: true, sortByPost: undefined, orderByPostAscending: true};
    component.sortByAuthor();
    expect(component.orderAscending).toBe(true);
    expect(component.sort).toEqual(secondExpectedSort);
    
    component.posts$.subscribe((posts: Post[]) => {
      expect(posts).toEqual(secondSortedPosts);
    })

    //then by author desc
    const thirdSortedPosts: Post[] = [post2, post1, post3, post4];
    postServiceMock.getAllPosts.mockReturnValueOnce(of(thirdSortedPosts));
    const thirdExpectedSort: PostSort = {sortByAuthor: true, orderByAuthorAscending: false, sortByPost: undefined, orderByPostAscending: true};
    component.sortOrder();
    expect(component.orderAscending).toBe(false);
    expect(component.sort).toEqual(thirdExpectedSort);
    
    component.posts$.subscribe((posts: Post[]) => {
      expect(posts).toEqual(thirdSortedPosts);
      done();
    })
  })

  it('should have posts sorted by creation date asc after multiple sort changes', (done) => {
    // first sort by post creation asc
    const sortedPosts: Post[] = [post3, post4, post2, post1];
    postServiceMock.getAllPosts.mockReturnValueOnce(of(sortedPosts));
    const expectedSort: PostSort = {sortByAuthor: undefined, orderByAuthorAscending: undefined, sortByPost: true, orderByPostAscending: true};
    component.sortOrder();

    component.posts$.subscribe((posts: Post[]) => {
      expect(posts).toEqual(sortedPosts);
    })

    //then by author (default asc)
    const secondSortedPosts: Post[] = [post3, post4, post2, post1];
    postServiceMock.getAllPosts.mockReturnValueOnce(of(secondSortedPosts));
    const secondExpectedSort: PostSort = {sortByAuthor: true, orderByAuthorAscending: true, sortByPost: undefined, orderByPostAscending: true};
    component.sortByAuthor();
    expect(component.orderAscending).toBe(true);
    expect(component.sort).toEqual(secondExpectedSort);
    
    component.posts$.subscribe((posts: Post[]) => {
      expect(posts).toEqual(secondSortedPosts);
    })

    //then by date
    const thirdSortedPosts: Post[] = defaultPosts;
    postServiceMock.getAllPosts.mockReturnValueOnce(of(defaultPosts));
    const thirdExpectedSort: PostSort = {sortByAuthor: undefined, orderByAuthorAscending: undefined, sortByPost: true, orderByPostAscending: false};
    component.sortByPostCreation();
    expect(component.orderAscending).toBe(false);
    expect(component.sort).toEqual(thirdExpectedSort);
    
    component.posts$.subscribe((posts: Post[]) => {
      expect(posts).toEqual(thirdSortedPosts);
      done();
    })
  })
});
