import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {OutputProject, InputProjectStatus} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProjectStore} from './project-store.service';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';

describe('ProjectStoreService', () => {
  let service: ProjectStore;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        ProjectModule,
        TestModule
      ],
      providers: [
        {
          provide: ProjectStore,
          useClass: ProjectStore
        }
      ]
    });
    service = TestBed.inject(ProjectStore);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch project and status', fakeAsync(() => {
    let project: OutputProject = {} as OutputProject;
    let status: InputProjectStatus.StatusEnum = InputProjectStatus.StatusEnum.SUBMITTED;
    service.getProject().subscribe(res => project = res);
    service.getStatus().subscribe(res => status = res);

    service.init(1);

    httpTestingController.expectOne({method: 'GET', url: '//api/project/1'})
      .flush({id: 1, projectStatus: {status: InputProjectStatus.StatusEnum.DRAFT}});

    tick();

    expect(project.id).toEqual(1);
    expect(status).toEqual(InputProjectStatus.StatusEnum.DRAFT);
  }));
});
