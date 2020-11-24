import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {TestModule} from '../../../common/test-module';
import {ProjectModule} from '../../project.module';
import {ProjectPartnerDetailPageComponent} from './project-partner-detail-page.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';

describe('ProjectApplicationFormPartnerDetailComponent', () => {
  let component: ProjectPartnerDetailPageComponent;
  let fixture: ComponentFixture<ProjectPartnerDetailPageComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule,
        RouterTestingModule.withRoutes(
          [{path: 'app/project/detail/1/applicationForm', component: ProjectPartnerDetailPageComponent}])
      ],
      declarations: [ProjectPartnerDetailPageComponent],
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectPartnerDetailPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
