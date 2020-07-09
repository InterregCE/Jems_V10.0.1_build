import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {ProjectApplicationComponent} from './project-application.component';
import {InputProject, OutputProject} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';
import {ProjectModule} from '../../../project.module';

describe('ProjectApplicationComponent', () => {

  let httpTestingController: HttpTestingController;
  let component: ProjectApplicationComponent;
  let fixture: ComponentFixture<ProjectApplicationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectApplicationComponent],
      imports: [
        ProjectModule,
        TestModule
      ],
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should create a project', fakeAsync(() => {
    const project = {acronym: 'test'} as InputProject;

    component.createApplication(project);
    let success = false;
    component.applicationSaveSuccess$.subscribe(result => success = result);

    httpTestingController.expectOne({method: 'GET', url: `//api/project?page=0&size=25&sort=id,desc`});
    httpTestingController.expectOne({method: 'POST', url: `//api/project`}).flush(project);
    httpTestingController.expectOne({method: 'GET', url: `//api/project?page=0&size=25&sort=id,desc`});
    httpTestingController.verify();

    tick();
    expect(success).toBeTruthy();
  }));

  it('should list projects', fakeAsync(() => {
    let results: OutputProject[] = [];
    component.currentPage$.subscribe(result => results = result.content);

    const projects = [
      {acronym: '1'} as OutputProject,
      {acronym: '2'} as OutputProject
    ];

    httpTestingController.match({method: 'GET', url: `//api/project?page=0&size=25&sort=id,desc`})
      .forEach(req => req.flush({content: projects}));

    tick();
    expect(results).toEqual(projects);
  }));

  it('should sort and page the list of projects', fakeAsync(() => {
    // initial sort and page
    httpTestingController.expectOne({method: 'GET', url: `//api/project?page=0&size=25&sort=id,desc`});

    // change sorting
    component.newSort$.next({active: 'submissionDate', direction: 'asc'})
    httpTestingController.expectOne({method: 'GET', url: `//api/project?page=0&size=25&sort=submissionDate,asc`});

    // change page index
    component.newPageIndex$.next(2)
    httpTestingController.expectOne({method: 'GET', url: `//api/project?page=2&size=25&sort=submissionDate,asc`});

    // change page size
    component.newPageSize$.next(3)
    httpTestingController.expectOne({method: 'GET', url: `//api/project?page=2&size=3&sort=submissionDate,asc`});

    httpTestingController.verify();
  }));

});
