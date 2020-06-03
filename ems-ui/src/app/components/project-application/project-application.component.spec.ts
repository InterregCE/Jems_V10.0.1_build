import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {ProjectApplicationComponent} from './project-application.component';
import {ProjectApplicationService} from '../../services/project-application.service';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { ProjectService, InputProject } from '@cat/api';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {ProjectApplicationSubmissionComponent} from './project-application-submission/project-application-submission.component';
import {ProjectApplicationSubmissionStubComponent} from './project-application-submission/project-application-submission-stub.component';
import {throwError} from 'rxjs';

describe('ProjectApplicationComponent', () => {

  let fixture: ComponentFixture<ProjectApplicationComponent>;
  let projectApplicationComponent: ProjectApplicationComponent;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ],
      declarations: [
        ProjectApplicationComponent
      ],
      providers: [
        {
          provide: ProjectApplicationService,
          useClass: ProjectApplicationService
        },
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

  it('should get 100 paged projects from server', async () => {
      projectApplicationComponent.getProjectsFromServer();
      httpTestingController.expectOne({
        method: 'GET',
        url: `/api/projects?size=${100}&sort=id,desc`
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

  it('should not submit a project application with too long a name.', fakeAsync(() => {
    const project = {acronym: 'testForOverLimitOfCharacters', submissionDate: '2020-12-12'} as InputProject;
    spyOn((projectApplicationComponent as any).projectService , 'addProject').and.returnValue(
      throwError(new ErrorEvent('backend error', {error: {acronym: ['long']}})));
    projectApplicationComponent.submitProjectApplication(project);
    tick();
    expect(projectApplicationComponent.error).toBeTruthy();
    expect(projectApplicationComponent.errorMessages).toContain(projectApplicationComponent.ERROR_MESSAGE_ACRONYM_TOO_LONG);
  }));

  it('should not submit a project application on Bad request', fakeAsync(() => {
    const project = {acronym: 'testForOverLimitOfCharacters', submissionDate: '2020-12-12'} as InputProject;
    spyOn((projectApplicationComponent as any).projectService , 'addProject').and.returnValue(
      throwError(new ErrorEvent('Bad Request', {error: 'Bad Request'})));
    projectApplicationComponent.submitProjectApplication(project);
    tick();
    expect(projectApplicationComponent.error).toBeTruthy();
    expect(projectApplicationComponent.errorMessages).toContain(projectApplicationComponent.ERROR_MESSAGE_BAD_REQUEST);
  }));

  it('should not submit a project application with missing data', fakeAsync(() => {
    const project = {acronym: '', submissionDate: ''} as InputProject;
    projectApplicationComponent.submitProjectApplication(project);
    tick();
    expect(projectApplicationComponent.error).toBeTruthy();
    expect(projectApplicationComponent.errorMessages).toContain(projectApplicationComponent.ERROR_MESSAGE_FIELDS_REQUIRED);
  }));
});
