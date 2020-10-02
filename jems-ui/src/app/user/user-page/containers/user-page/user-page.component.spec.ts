import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {UserModule} from '../../../user.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';
import {UserPageComponent} from './user-page.component';
import {InputUserCreate, OutputUser} from '@cat/api';

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

    httpTestingController.match({method: 'GET', url: `//api/auth/current`});
    httpTestingController.expectOne({method: 'POST', url: `//api/user`}).flush(user);
    httpTestingController.verify();

    tick();
    expect(success).toBeTruthy();
  }));

  it('should list users', fakeAsync(() => {
    let results: OutputUser[] = [];
    component.currentPage$.subscribe(result => results = result.content);

    const users = [
      {email: '1@1'} as OutputUser,
      {email: '2@2'} as OutputUser
    ];

    httpTestingController.match({method: 'GET', url: `//api/user?page=0&size=25&sort=id,desc`})
      .forEach(req => req.flush({content: users}));

    tick();
    expect(results).toEqual(users);
  }));

  it('should sort and page the list of users', fakeAsync(() => {
    component.currentPage$.subscribe();
    httpTestingController.match({method: 'GET', url: `//api/auth/current`});

    // initial sort and page
    httpTestingController.expectOne({method: 'GET', url: `//api/user?page=0&size=25&sort=id,desc`});

    // change sorting
    component.newSort$.next({active: 'userRole.name', direction: 'asc'})
    httpTestingController.expectOne({method: 'GET', url: `//api/user?page=0&size=25&sort=userRole.name,asc`});

    // change page index
    component.newPageIndex$.next(2)
    httpTestingController.expectOne({method: 'GET', url: `//api/user?page=2&size=25&sort=userRole.name,asc`});

    // change page size
    component.newPageSize$.next(3)
    httpTestingController.expectOne({method: 'GET', url: `//api/user?page=2&size=3&sort=userRole.name,asc`});

    httpTestingController.verify();
  }));
});

