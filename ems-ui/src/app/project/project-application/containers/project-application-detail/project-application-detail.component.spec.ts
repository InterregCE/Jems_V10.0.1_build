import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {OutputProjectFile, ProjectFileStorageService, ProjectService} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProjectApplicationDetailComponent} from './project-application-detail.component';
import {ProjectFileService} from '../../services/project-file.service';
import {ActivatedRoute} from '@angular/router';
import {DeleteDialogComponent} from '../../components/project-application-detail/delete-dialog/delete-dialog.component';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {TestModule} from '../../../../common/test-module';
import {MatDialogModule} from '@angular/material/dialog';
import {of} from 'rxjs';

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
        DeleteDialogComponent
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
    TestBed.overrideModule(BrowserDynamicTestingModule, {
      set: {
        entryComponents: [DeleteDialogComponent],
      },
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

  xit('should get 100 paged project files from server', async () => {
    // projectApplicationDetailComponent.getFilesForProject(1);
    httpTestingController.expectOne({
      method: 'GET',
      url: `/api/project/${1}/file?size=${100}&sort=updated,desc`
    }).flush({});
    httpTestingController.verify();
  });

  xit('should upload a project application file', fakeAsync(() => {
    const event = {target: {files: [{name: 'name'}]}};
    // projectApplicationDetailComponent.addNewFilesForUpload(event);
    httpTestingController.expectOne({
      method: 'POST',
      url: `/api/project/${1}/file/`
    }).flush(event);
    httpTestingController.verify();
    tick();
    // check for correct status
    expect(projectApplicationDetailComponent.statusMessages).toBeTruthy();
  }));

  it('should delete an uploaded project application file', fakeAsync(() => {
    (projectApplicationDetailComponent as any).projectFileStorageService = TestBed.inject(ProjectFileStorageService);
    spyOn((projectApplicationDetailComponent as any).dialog, 'open')
      .and
      .returnValue({afterClosed: () => of(true)});
    spyOn((projectApplicationDetailComponent as any).projectFileStorageService, 'deleteFile')
      .and.returnValue(of());
    projectApplicationDetailComponent.deleteFile({id: 1} as OutputProjectFile);
    expect((projectApplicationDetailComponent as any).projectFileStorageService.deleteFile).toHaveBeenCalled();
  }));
});
