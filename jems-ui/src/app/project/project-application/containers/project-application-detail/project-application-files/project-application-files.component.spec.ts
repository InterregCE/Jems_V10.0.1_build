import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';
import {ProjectApplicationFilesComponent} from './project-application-files.component';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {OutputProjectFile} from '@cat/api';
import {ProjectDetailPageStore} from '../../../../project-detail-page/project-detail-page-store';

describe('ProjectApplicationFilesComponent', () => {
  const URL = '//api/project/1/file?page=0&size=25&sort=id,desc';
  let component: ProjectApplicationFilesComponent;
  let fixture: ComponentFixture<ProjectApplicationFilesComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      providers: [ProjectDetailPageStore],
      declarations: [ProjectApplicationFilesComponent],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFilesComponent);
    component = fixture.componentInstance;
    component.projectId = 1;
    component.fileType = OutputProjectFile.TypeEnum.APPLICANTFILE;
    httpTestingController = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should sort and page the list of project files', fakeAsync(() => {
    httpTestingController.match({method: 'GET', url: `//api/auth/current`});
    // initial sort and page
    httpTestingController.expectOne({
      method: 'GET',
      url: URL
    });

    // change sorting
    component.newSort$.next({active: 'userRole.name', direction: 'asc'});
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/1/file?page=0&size=25&sort=userRole.name,asc'
    });

    // change page index
    component.newPageIndex$.next(2);
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/1/file?page=2&size=25&sort=userRole.name,asc'
    });

    // change page size
    component.newPageSize$.next(3);
    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/1/file?page=2&size=3&sort=userRole.name,asc'
    });

    httpTestingController.verify();
  }));

  it('should delete file', fakeAsync(() => {
    component.deleteFile({id: 1, name: 'file'} as OutputProjectFile);

    httpTestingController.match({method: 'GET', url: `//api/auth/current`});
    httpTestingController.expectOne({
      method: 'GET',
      url: URL
    });
    httpTestingController.expectOne({method: 'DELETE', url: '//api/project/1/file/applicant/1'});

    httpTestingController.verify();
  }));

  it('should save description', fakeAsync(() => {
    component.saveDescription({id: 1, name: 'file'} as OutputProjectFile);

    httpTestingController.match({method: 'GET', url: `//api/auth/current`});
    httpTestingController.expectOne({
      method: 'GET',
      url: URL
    });
    httpTestingController.expectOne({method: 'PUT', url: '//api/project/1/file/applicant/1/description'});

    httpTestingController.verify();
  }));
});
