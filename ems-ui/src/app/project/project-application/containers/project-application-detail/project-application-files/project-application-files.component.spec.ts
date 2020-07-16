import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {ProjectApplicationFilesComponent} from './project-application-files.component';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {OutputProjectFile} from '@cat/api';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {Permission} from '../../../../../security/permissions/permission';
import {ProjectStore} from '../services/project-store.service';

describe('ProjectApplicationFilesComponent', () => {
  let component: ProjectApplicationFilesComponent;
  let fixture: ComponentFixture<ProjectApplicationFilesComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      declarations: [ProjectApplicationFilesComponent],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFilesComponent);
    component = fixture.componentInstance;
    component.projectId = 1;
    httpTestingController = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should list project files', fakeAsync(() => {
    const permissionService = TestBed.inject(PermissionService);
    const projectStore = TestBed.inject(ProjectStore);
    permissionService.setPermissions([Permission.ADMINISTRATOR]);
    let results: OutputProjectFile[] = [];
    component.details$.subscribe(result => results = result.page.content);

    const users = [{name: '1'} as OutputProjectFile, {name: '2'} as OutputProjectFile];

    projectStore.init(1);
    httpTestingController.expectOne({method: 'GET', url: '//api/project/1'}).flush({id: 1});
    httpTestingController.match({method: 'GET', url: '//api/project/1/file?page=0&size=25&sort=id,desc'})
      .forEach(req => req.flush({content: users}));

    tick();
    expect(results).toEqual(users);
  }));

  it('should sort and page the list of project files', fakeAsync(() => {
    // initial sort and page
    httpTestingController.expectOne({method: 'GET', url: '//api/project/1/file?page=0&size=25&sort=id,desc'});

    // change sorting
    component.newSort$.next({active: 'userRole.name', direction: 'asc'})
    httpTestingController.expectOne({method: 'GET', url: '//api/project/1/file?page=0&size=25&sort=userRole.name,asc'});

    // change page index
    component.newPageIndex$.next(2)
    httpTestingController.expectOne({method: 'GET', url: '//api/project/1/file?page=2&size=25&sort=userRole.name,asc'});

    // change page size
    component.newPageSize$.next(3)
    httpTestingController.expectOne({method: 'GET', url: '//api/project/1/file?page=2&size=3&sort=userRole.name,asc'});

    httpTestingController.verify();
  }));

  it('should delete file', fakeAsync(() => {
    component.deleteFile({id: 1, name: 'file'} as OutputProjectFile);

    httpTestingController.expectOne({method: 'GET', url: '//api/project/1/file?page=0&size=25&sort=id,desc'});
    httpTestingController.expectOne({method: 'DELETE', url: '//api/project/1/file/1'});

    httpTestingController.verify();
  }));

  it('should save description', fakeAsync(() => {
    component.saveDescription({id: 1, name: 'file'} as OutputProjectFile);

    httpTestingController.expectOne({method: 'GET', url: '//api/project/1/file?page=0&size=25&sort=id,desc'});
    httpTestingController.expectOne({method: 'PUT', url: '//api/project/1/file/1/description'});

    httpTestingController.verify();
  }));
});
