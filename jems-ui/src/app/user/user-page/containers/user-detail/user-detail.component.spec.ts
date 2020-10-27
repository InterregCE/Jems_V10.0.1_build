import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {UserModule} from '../../../user.module';
import {TestModule} from '../../../../common/test-module';
import {UserDetailComponent} from './user-detail.component';
import {ActivatedRoute} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';

describe('UserDetailComponent', () => {
  let component: UserDetailComponent;
  let fixture: ComponentFixture<UserDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UserDetailComponent],
      imports: [
        UserModule,
        TestModule,
        RouterTestingModule.withRoutes(
          [{path: 'app/project/detail/1/applicationForm', component: UserDetailComponent}])
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
  }));

  beforeEach(() => {

    fixture = TestBed.createComponent(UserDetailComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});

