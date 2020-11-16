import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {TestModule} from '../../../../../../common/test-module';
import {ProjectModule} from '../../../../../project.module';
import {ProjectApplicationFormPartnerDetailComponent} from './project-application-form-partner-detail.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';

describe('ProjectApplicationFormPartnerDetailComponent', () => {
  let component: ProjectApplicationFormPartnerDetailComponent;
  let fixture: ComponentFixture<ProjectApplicationFormPartnerDetailComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule,
        RouterTestingModule.withRoutes(
          [{path: 'app/project/detail/1/applicationForm', component: ProjectApplicationFormPartnerDetailComponent}])
      ],
      declarations: [ProjectApplicationFormPartnerDetailComponent],
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormPartnerDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
