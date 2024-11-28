import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostComponent } from './post.component';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { User } from '../../../../core/models/user.interface';
import { Topic } from '../../../../core/models/topic.interface';

describe('PostComponent', () => {
  let component: PostComponent;
  let fixture: ComponentFixture<PostComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostComponent],
      providers: [
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostComponent);
    component = fixture.componentInstance;
    component.post = {id: 1, title: 'Test post', content: 'Test post content', createdAt: '2024-11-10T11:10:52', updatedAt: '2024-11-10T11:10:52',
      author: {id: 1, username: 'testuser'} as User,
      topic: {id: 1, title: 'Test topic'} as Topic
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
