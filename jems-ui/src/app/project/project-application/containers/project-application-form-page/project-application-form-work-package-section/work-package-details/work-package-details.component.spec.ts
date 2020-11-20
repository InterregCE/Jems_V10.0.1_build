import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {TestModule} from '../../../../../../common/test-module';
import {ProjectModule} from '../../../../../project.module';
import {WorkPackageDetailsComponent} from './work-package-details.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';

describe('WorkPackageDetailsComponent', () => {
  let component: WorkPackageDetailsComponent;
  let fixture: ComponentFixture<WorkPackageDetailsComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule,
        RouterTestingModule.withRoutes(
          [{path: 'app/project/detail/1/applicationForm', component: WorkPackageDetailsComponent}])
      ],
      declarations: [WorkPackageDetailsComponent],
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkPackageDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
