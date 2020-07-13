import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {ProjectFileStorageService, ProjectService} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProjectApplicationDetailComponent} from './project-application-detail.component';
import {ProjectFileService} from '../../services/project-file.service';
import {ActivatedRoute} from '@angular/router';
import {TestModule} from '../../../../common/test-module';
import {MatDialogModule} from '@angular/material/dialog';

describe('ProjectApplicationDetailComponent', () => {

  let fixture: ComponentFixture<ProjectApplicationDetailComponent>;
  let projectApplicationDetailComponent: ProjectApplicationDetailComponent;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        MatDialogModule
      ],
      declarations: [
        ProjectApplicationDetailComponent,
      ],
      providers: [
        {
          provide: ProjectApplicationDetailComponent,
          useClass: ProjectApplicationDetailComponent
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
        },
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    });
    TestBed.compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationDetailComponent);
    projectApplicationDetailComponent = (
      fixture.componentInstance
    ) as ProjectApplicationDetailComponent;
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should create the project application', () => {
    expect(projectApplicationDetailComponent).toBeTruthy();
  });
});
