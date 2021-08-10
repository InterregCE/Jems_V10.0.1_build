import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TestModule} from '@common/test-module';
import {ProjectModule} from '@project/project.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProjectPartnerSummaryDTO} from '@cat/api';
import {ProjectApplicationFormPartnerSectionComponent} from './project-application-form-partner-section.component';
import {RouterTestingModule} from '@angular/router/testing';

describe('ProjectApplicationFormPartnerSectionComponent', () => {
  let component: ProjectApplicationFormPartnerSectionComponent;
  let fixture: ComponentFixture<ProjectApplicationFormPartnerSectionComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule,
        RouterTestingModule.withRoutes(
          [{path: 'app/project/detail/1/applicationForm', component: ProjectApplicationFormPartnerSectionComponent}])
      ],
      declarations: [ProjectApplicationFormPartnerSectionComponent],
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormPartnerSectionComponent);
    component = fixture.componentInstance;
    component.projectId = 1;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  // it('should list project partners', fakeAsync(() => {
  //   let results: OutputProjectPartner[] = [];
  //   component.partnerPage$.subscribe(result => results = result.content);
  //
  //   const projectPartners = [
  //     {abbreviation: 'test1'} as OutputProjectPartner,
  //     {abbreviation: 'test2'} as OutputProjectPartner
  //   ];
  //
  //   httpTestingController.match({
  //     method: 'GET',
  //     url: `//api/project/1/partner?page=0&size=25&sort=role,asc&sort=sortNumber,asc`
  //   })
  //     .forEach(req => req.flush({content: projectPartners}));
  //
  //   tick();
  //   expect(results).toEqual(projectPartners);
  // }));
  //
  // it('should delete a project partner', fakeAsync(() => {
  //   component.deletePartner(1);
  //
  //   httpTestingController.expectOne({
  //     method: 'DELETE',
  //     url: `//api/project/1/partner/1`
  //   });
  //   httpTestingController.expectOne({
  //     method: 'GET',
  //     url: `//api/project/1/partner?page=0&size=25&sort=role,asc&sort=sortNumber,asc`
  //   });
  // }));

});
