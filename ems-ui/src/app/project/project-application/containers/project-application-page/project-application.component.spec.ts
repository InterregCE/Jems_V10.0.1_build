import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {ProjectApplicationComponent} from './project-application.component';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {InputProject, ProjectService} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProjectApplicationSubmissionComponent} from '../../components/project-application-submission/project-application-submission.component';
import {ProjectApplicationSubmissionStubComponent} from '../../components/project-application-submission/project-application-submission-stub.component';
import {TestModule} from '../../../../common/test-module';
import {throwError} from 'rxjs';

describe('ProjectApplicationComponent', () => {

  let fixture: ComponentFixture<ProjectApplicationComponent>;
  let projectApplicationComponent: ProjectApplicationComponent;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule
      ],
      declarations: [
        ProjectApplicationComponent
      ],
      providers: [
        {
          provide: ProjectService,
          useClass: ProjectService
        },
        {
          provide: ProjectApplicationSubmissionComponent,
          useClass: ProjectApplicationSubmissionStubComponent
        }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationComponent);
    projectApplicationComponent = (
      fixture.componentInstance
    ) as ProjectApplicationComponent;
    httpTestingController = TestBed.get(HttpTestingController);
    projectApplicationComponent.projectSubmissionComponent = TestBed.get(ProjectApplicationSubmissionComponent);
  });

  it('should create the project application', () => {
    expect(projectApplicationComponent).toBeTruthy();
  });

  // TODO fix test
  xit('should get 100 paged projects from server', async () => {
    projectApplicationComponent.getProjectsFromServer();
    httpTestingController.expectOne({
      method: 'GET',
      url: `/api/project?size=${100}&sort=id,desc`
    }).flush({});
    httpTestingController.verify();
  });

  it('should submit a project application', fakeAsync(() => {
    const project = {acronym: 'test', submissionDate: '2020-12-12'} as InputProject;
    projectApplicationComponent.submitProjectApplication(project);
    httpTestingController.expectOne({
      method: 'POST',
      url: `//api/project`
    }).flush(project);
    httpTestingController.verify();
    tick();
    expect(projectApplicationComponent.success).toBeTruthy();
  }));

  // TODO fix test
  xit('should not submit a project application with too long a name.', fakeAsync(() => {
    const project = {acronym: 'testForOverLimitOfCharacters', submissionDate: '2020-12-12'} as InputProject;
    spyOn((projectApplicationComponent as any).projectService, 'addProject').and.returnValue(
      throwError(new ErrorEvent('backend error', {error: {acronym: ['long']}})));
    projectApplicationComponent.submitProjectApplication(project);
    tick();
    expect(projectApplicationComponent.error).toBeTruthy();
  }));

  // TODO fix test
  xit('should not submit a project application on Bad request', fakeAsync(() => {
    const project = {acronym: 'testForOverLimitOfCharacters', submissionDate: '2020-12-12'} as InputProject;
    spyOn((projectApplicationComponent as any).projectService, 'addProject').and.returnValue(
      throwError(new ErrorEvent('Bad Request', {error: 'Bad Request'})));
    projectApplicationComponent.submitProjectApplication(project);
    tick();
    expect(projectApplicationComponent.error).toBeTruthy();
  }));

  // TODO fix test
  xit('should not submit a project application with missing data', fakeAsync(() => {
    const project = {acronym: '', submissionDate: ''} as InputProject;
    projectApplicationComponent.submitProjectApplication(project);
    tick();
    expect(projectApplicationComponent.error).toBeTruthy();
  }));
});
