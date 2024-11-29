import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostsListComponent } from './posts-list.component';
import { Post } from '../../../../core/models/post.interface';
import { Topic } from '../../../../core/models/topic.interface';
import { User } from '../../../../core/models/user.interface';
import { provideRouter, RouterLink } from '@angular/router';
import { PostComponent } from '../post/post.component';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { AsyncPipe } from '@angular/common';
import { HttpTestingController, provideHttpClientTesting, TestRequest } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { PostService } from '../../../../core/services/post.service';
import { Subscription } from '../../../../core/models/subscription.interface';
import { By, DomSanitizer } from '@angular/platform-browser';
import { of } from 'rxjs';
import { DebugElement } from '@angular/core';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('PostsListComponent integration tests', () => {
  let component: PostsListComponent;
  let fixture: ComponentFixture<PostsListComponent>;
  let httpControllerMock: HttpTestingController;

  const post1: Post = {id: 1, title: "Test post", content: "Test post content", createdAt: '2024-11-02T11:00:00', author: {id: 1, "username": "btestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
  const post2: Post = {id: 2, title: "Second test post", content: "Second test post content", createdAt: '2024-10-28T08:00:00', author: {id: 1, "username": "btestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
  const post3: Post = {id: 3, title: "Third test post", content: "Third test post content", createdAt: '2024-09-26T08:00:00', author: {id: 2, "username": "atestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
  const post4: Post = {id: 4, title: "Fourth test post", content: "Fourth test post content", createdAt: '2024-09-28T08:00:00', author: {id: 2, "username": "atestuser"} as User, topic: {id: 1, title: "Test topic"} as Topic} as Post;
  const defaultPosts: Post[] = [post1, post2, post4, post3];

  const mockSubscriptions: Subscription[] = [
    {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 1, title: 'Test topic', description: 'This is a test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}},
    {userId: 1, createdAt: '2024-11-27T10:03', topic: {id: 2, title: 'Other test topic', description: 'This is another test topic', createdAt: '2024-01-28T11:00:00', updatedAt: '2024-01-28T11:00:00'}}
  ];
  
  beforeEach(async () => {
    
    await TestBed.configureTestingModule({
      imports: [
        PostsListComponent,
        PostComponent,
        AsyncPipe,
        RouterLink,
        MatButtonModule, 
        MatTooltipModule, 
        MatMenuModule,
        MatIconModule,
        NoopAnimationsModule
      ],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        PostService,
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

    fixture = TestBed.createComponent(PostsListComponent);
    httpControllerMock = TestBed.inject(HttpTestingController);
    TestBed.inject(PostService);
    // posts are loaded at init
    component = fixture.componentInstance;
    component.ngOnInit();

    const subscriptionsRequest: TestRequest = httpControllerMock.expectOne("api/user/me/subscriptions");
    expect(subscriptionsRequest.request.method).toBe("GET");
    subscriptionsRequest.flush(mockSubscriptions);
    fixture.detectChanges();

    const postsRequest: TestRequest = httpControllerMock.expectOne("api/posts");
    expect(postsRequest.request.method).toBe("GET");
    postsRequest.flush(defaultPosts);
    fixture.detectChanges();
  });
  
  it('should have default posts on init', () => {
    const h2Elements = fixture.debugElement.queryAll(By.css('app-post h2'));
    
    expect(h2Elements.length).toBe(4);

    // check posts order
    h2Elements.forEach((h2: DebugElement, key: number) => {
      expect(h2.nativeElement.textContent).toEqual(defaultPosts[key].title);
    });
  })
  
  it('should have posts sorted by creation date asc', () => {
    fixture.debugElement.query(By.css('[data-mat-icon-name="arrow-up"]')).nativeElement.click();
    fixture.detectChanges();

    const h2Elements = fixture.debugElement.queryAll(By.css('app-post h2'));
    expect(h2Elements.length).toBe(4);

    // check posts order
    const sortedPosts: Post[] = [post3, post4, post2, post1];
    h2Elements.forEach((h2: DebugElement, key: number) => {
      expect(h2.nativeElement.textContent).toEqual(sortedPosts[key].title);
    });
  })

  
  // happens when articles are sorted by default then the use sorts by author
  it('should have posts sorted by author asc and creation date desc', () => {
    // open menu
    fixture.debugElement.query(By.css('.sort-actions>a')).nativeElement.click();
    // then sort by author
    fixture.debugElement.query(By.css('.mat-mdc-menu-content>button:first-child a')).nativeElement.click();
    fixture.detectChanges();

    const h2Elements = fixture.debugElement.queryAll(By.css('app-post h2'));
    expect(h2Elements.length).toBe(4);

     // check posts order
     const sortedPosts: Post[] = [post4, post3, post1, post2];
     h2Elements.forEach((h2: DebugElement, key: number) => {
       expect(h2.nativeElement.textContent).toEqual(sortedPosts[key].title);
     });

  })

  it('should have posts sorted by author asc and creation date asc', () => {
    // first sort by post creation asc
    fixture.debugElement.query(By.css('[data-mat-icon-name="arrow-up"]')).nativeElement.click();
    fixture.detectChanges();

    const h2Elements = fixture.debugElement.queryAll(By.css('app-post h2'));
    expect(h2Elements.length).toBe(4);

    // check posts order
    const sortedPosts: Post[] = [post3, post4, post2, post1];
    h2Elements.forEach((h2: DebugElement, key: number) => {
      expect(h2.nativeElement.textContent).toEqual(sortedPosts[key].title);
    });

    //then by author asc
    // open menu
    fixture.debugElement.query(By.css('.sort-actions>a')).nativeElement.click();
    // then sort by author
    fixture.debugElement.query(By.css('.mat-mdc-menu-content>button:first-child a')).nativeElement.click();
    fixture.detectChanges();
    // check posts order
    const nextSortedPosts: Post[] = [post3, post4, post2, post1];
    h2Elements.forEach((h2: DebugElement, key: number) => {
      expect(h2.nativeElement.textContent).toEqual(nextSortedPosts[key].title);
    });
  })

  
  it('should have posts sorted by author desc and creation date asc', () => {
    // first sort by post creation asc
    fixture.debugElement.query(By.css('[data-mat-icon-name="arrow-up"]')).nativeElement.click();
    fixture.detectChanges();

    const h2Elements = fixture.debugElement.queryAll(By.css('app-post h2'));
    expect(h2Elements.length).toBe(4);

    // check posts order by post creation asc
    const sortedPosts: Post[] = [post3, post4, post2, post1];
    h2Elements.forEach((h2: DebugElement, key: number) => {
      expect(h2.nativeElement.textContent).toEqual(sortedPosts[key].title);
    });

    //then by author asc
    // open menu
    fixture.debugElement.query(By.css('.sort-actions>a')).nativeElement.click();
    // then sort by author asc (and date)
    fixture.debugElement.query(By.css('.mat-mdc-menu-content>button:first-child a')).nativeElement.click();
    fixture.detectChanges();
    // check posts order by author asc and creation date asc
    const nextSortedPosts: Post[] = [post3, post4, post2, post1];
    h2Elements.forEach((h2: DebugElement, key: number) => {
      expect(h2.nativeElement.textContent).toEqual(nextSortedPosts[key].title);
    });

    //then sort by author desc
    fixture.debugElement.query(By.css('[data-mat-icon-name="arrow-down"]')).nativeElement.click();
    fixture.detectChanges();

    // check order
    const thirdSortedPosts: Post[] = [post2, post1, post3, post4];
    h2Elements.forEach((h2: DebugElement, key: number) => {
      expect(h2.nativeElement.textContent).toEqual(thirdSortedPosts[key].title);
    });
  })

  it('should have posts sorted by creation date asc after multiple sort changes', () => {
    // first sort by post creation asc
    fixture.debugElement.query(By.css('[data-mat-icon-name="arrow-up"]')).nativeElement.click();
    fixture.detectChanges();

    const h2Elements = fixture.debugElement.queryAll(By.css('app-post h2'));
    expect(h2Elements.length).toBe(4);

    // check posts order by post creation asc
    const sortedPosts: Post[] = [post3, post4, post2, post1];
    h2Elements.forEach((h2: DebugElement, key: number) => {
      expect(h2.nativeElement.textContent).toEqual(sortedPosts[key].title);
    });

    //then by author asc
    // open menu
    fixture.debugElement.query(By.css('.sort-actions>a')).nativeElement.click();
    // then sort by author asc (and date)
    fixture.debugElement.query(By.css('.mat-mdc-menu-content>button:first-child a')).nativeElement.click();
    fixture.detectChanges();
    // check posts order by author asc and creation date asc
    const nextSortedPosts: Post[] = [post3, post4, post2, post1];
    h2Elements.forEach((h2: DebugElement, key: number) => {
      expect(h2.nativeElement.textContent).toEqual(nextSortedPosts[key].title);
    });

    //then by date
     // open menu
     fixture.debugElement.query(By.css('.sort-actions>a')).nativeElement.click();
     // then sort by author asc (and date)
     fixture.debugElement.query(By.css('.mat-mdc-menu-content>button:last-child a')).nativeElement.click();
     fixture.detectChanges();
     // check posts order by author asc and creation date asc
    h2Elements.forEach((h2: DebugElement, key: number) => {
      expect(h2.nativeElement.textContent).toEqual(defaultPosts[key].title);
    });
  })
});
