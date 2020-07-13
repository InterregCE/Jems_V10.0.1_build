import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {UserModule} from '../../../user.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';
import {InputUserUpdate} from '@cat/api';
import {UserDetailComponent} from './user-detail.component';
import {ActivatedRoute} from '@angular/router';

describe('UserDetailComponent', () => {
  let httpTestingController: HttpTestingController;
  let component: UserDetailComponent;
  let fixture: ComponentFixture<UserDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UserDetailComponent],
      imports: [
        UserModule,
        TestModule
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {params: {userId: '1'}}
          }
        }
      ]
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {

    fixture = TestBed.createComponent(UserDetailComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should update a user', fakeAsync(() => {
    const user = {email: 'test@test.com'} as InputUserUpdate;

    component.saveUser$.next(user);
    let success = false;
    component.userSaveSuccess$.subscribe(result => success = result);

    httpTestingController.expectOne({
      method: 'GET',
      url: `//api/user/1`
    }).flush(user);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/user`
    }).flush(user);
    httpTestingController.verify();

    tick();
    expect(success).toBeTruthy();
  }));
});

