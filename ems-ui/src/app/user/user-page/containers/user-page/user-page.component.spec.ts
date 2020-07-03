import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {UserModule} from '../../../user.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';
import {UserPageComponent} from './user-page.component';
import {InputUserCreate} from '@cat/api';

describe('UserPageComponent', () => {
  let httpTestingController: HttpTestingController;
  let component: UserPageComponent;
  let fixture: ComponentFixture<UserPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UserPageComponent],
      imports: [
        UserModule,
        TestModule
      ],
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should create a user', fakeAsync(() => {
    const user = {email: 'test@test.com'} as InputUserCreate;

    component.createUser(user);
    let success = false;
    component.userSaveSuccess$.subscribe(result => success = result);

    httpTestingController.expectOne({
      method: 'POST',
      url: `//api/user`
    }).flush(user);
    httpTestingController.verify();

    tick();
    expect(success).toBeTruthy();
  }));
});

