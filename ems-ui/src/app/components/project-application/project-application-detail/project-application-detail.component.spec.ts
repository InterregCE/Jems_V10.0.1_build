import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {ProjectApplicationService} from '../../../services/project-application.service';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {ProjectFileStorageService, ProjectService} from '@cat/api';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {ProjectApplicationDetailComponent} from './project-application-detail.component';
import {ProjectFileService} from '../../../services/project-file.service';
import {RouterTestingModule} from '@angular/router/testing';
import {ActivatedRoute} from '@angular/router';

describe('ProjectApplicationDetailComponent', () => {

  let fixture: ComponentFixture<ProjectApplicationDetailComponent>;
  let projectApplicationDetailComponent: ProjectApplicationDetailComponent;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule
      ],
      declarations: [
        ProjectApplicationDetailComponent
      ],
      providers: [
        {
          provide: ProjectApplicationDetailComponent,
          useClass: ProjectApplicationDetailComponent
        },
        {
          provide: ProjectApplicationService,
          useClass: ProjectApplicationService
        },
        {
          provide: ProjectService,
          useClass: ProjectService
        },
        {
          provide: ProjectFileService,
          useClass: ProjectFileService
        },
        {
          provide: ProjectFileStorageService,
          useClass: ProjectFileStorageService
        },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              params: {
                projectId: 1
              }
            }
          }
        }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationDetailComponent);
    projectApplicationDetailComponent = (
      fixture.componentInstance
    ) as ProjectApplicationDetailComponent;
    httpTestingController = TestBed.get(HttpTestingController);
  });

  it('should create the project application', () => {
    expect(projectApplicationDetailComponent).toBeTruthy();
  });

  it('should get 100 paged project files from server', async () => {
    projectApplicationDetailComponent.getFilesForProject(1);
    httpTestingController.expectOne({
      method: 'GET',
      url: `/api/project/${1}/file?size=${100}&sort=updated,desc`
    }).flush({});
    httpTestingController.verify();
  });

  it('should upload a project application file', fakeAsync(() => {
    const event = { target: { files: [ {name: 'name'} ] }};
    projectApplicationDetailComponent.addNewFilesForUpload(event);
    httpTestingController.expectOne({
      method: 'POST',
      url: `/api/project/1/file`
    }).flush(event);
    httpTestingController.verify();
    tick();
    // check for correct status
    expect(projectApplicationDetailComponent.statusMessages).toBeTruthy();
  }));

});
