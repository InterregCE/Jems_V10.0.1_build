import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {ProjectApplicationFilesComponent} from './project-application-files.component';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';
import {ProjectStore} from '../services/project-store.service';
import {HttpTestingController} from '@angular/common/http/testing';
import {InputProjectStatus, OutputProjectFile} from '@cat/api';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {Permission} from '../../../../../security/permissions/permission';

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
      providers: [
        {
          provide: ProjectStore,
          useClass: ProjectStore
        }
      ]
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
    let results: OutputProjectFile[] = [];
    component.currentPage$.subscribe(result => results = result.content);

    const users = [{name: '1'} as OutputProjectFile, {name: '2'} as OutputProjectFile];

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

  it('should set visibility actions for programme user', fakeAsync(() => {
    const projectStore = TestBed.inject(ProjectStore);
    const permissionService = TestBed.inject(PermissionService);
    projectStore.init(1);
    component.ngOnInit();
    projectStore.getProject().subscribe();
    permissionService.setPermissions([Permission.PROGRAMME_USER]);

    httpTestingController.match({method: 'GET', url: '//api/project/1'})
      .forEach(req => req.flush({projectStatus: InputProjectStatus.StatusEnum.DRAFT}))

    tick();
    expect(component.editActionVisible).toBeFalse();
    expect(component.downloadActionVisible).toBeTrue();
    expect(component.deleteActionVisible).toBeFalse();
  }));
});
