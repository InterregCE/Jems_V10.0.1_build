import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {UserPageComponent} from './user-page.component';
import {UserModule} from '../../../user.module';
import {TestModule} from '../../../../common/test-module';

describe('UserPageComponent', () => {
  let component: UserPageComponent;
  let fixture: ComponentFixture<UserPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        UserModule,
      ],
      declarations: [UserPageComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
