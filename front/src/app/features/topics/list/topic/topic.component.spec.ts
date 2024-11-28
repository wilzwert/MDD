import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TopicComponent } from './topic.component';

describe('TopicComponent', () => {
  let component: TopicComponent;
  let fixture: ComponentFixture<TopicComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TopicComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TopicComponent);
    component = fixture.componentInstance;
    component.topic = {id: 1, title: 'Test topic', description: 'Test topic description', createdAt: '2024-03-10T12:12:12', updatedAt: '2024-03-13T11:10:09'};
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
