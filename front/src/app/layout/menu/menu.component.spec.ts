import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuComponent } from './menu.component';
import { SessionService } from '../../core/services/session.service';
import { of } from 'rxjs';
import { provideRouter, RouterLink } from '@angular/router';

describe('MenuComponent', () => {
  let component: MenuComponent;
  let fixture: ComponentFixture<MenuComponent>;
  let sessionServiceMock: jest.Mocked<SessionService>;

  beforeEach(async () => {
    sessionServiceMock = {
      $isLogged: jest.fn().mockReturnValue(of(true))
    } as unknown as jest.Mocked<SessionService>;

    await TestBed.configureTestingModule({
      imports: [
        MenuComponent
      ],
      providers: [
        provideRouter([]),
        { provide: SessionService, useValue: sessionServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
