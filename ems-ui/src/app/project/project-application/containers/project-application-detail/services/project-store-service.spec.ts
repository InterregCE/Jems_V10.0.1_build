import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {
  InputProjectEligibilityAssessment,
  InputProjectQualityAssessment,
  InputProjectStatus,
  OutputProject
} from '@cat/api';
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
    service.getProject().subscribe(res => project = res);

    service.init(1);

    httpTestingController.expectOne({method: 'GET', url: '//api/project/1'})
      .flush({id: 1, projectStatus: {status: InputProjectStatus.StatusEnum.DRAFT}});

    tick();
    expect(project.id).toEqual(1);
  }));

  it('should change status', fakeAsync(() => {
    let status: InputProjectStatus.StatusEnum = InputProjectStatus.StatusEnum.SUBMITTED;
    service.getProject().subscribe();
    service.getStatus().subscribe(res => status = res);

    service.init(1);
    service.changeStatus({status: InputProjectStatus.StatusEnum.DRAFT, note: '', date: ''})

    httpTestingController.expectOne({method: 'PUT', url: '//api/project/1/status'})
      .flush({id: 1, projectStatus: {status: InputProjectStatus.StatusEnum.DRAFT}});

    tick();
    expect(status).toEqual(InputProjectStatus.StatusEnum.DRAFT);
  }));

  it('should change status in eligibility decision', fakeAsync(() => {
    let status: InputProjectStatus.StatusEnum = InputProjectStatus.StatusEnum.SUBMITTED;
    service.getProject().subscribe();
    service.getStatus().subscribe(res => status = res);

    service.init(1);
    service.changeStatus({status: InputProjectStatus.StatusEnum.ELIGIBLE, note: 'Passed', date: '20/07/2020'})

    httpTestingController.expectOne({method: 'PUT', url: '//api/project/1/status'})
      .flush({id: 1, projectStatus: {status: InputProjectStatus.StatusEnum.ELIGIBLE, note: 'Passed', date: '20/07/2020'}});

    tick();
    expect(status).toEqual(InputProjectStatus.StatusEnum.ELIGIBLE);
  }));

  it('should change eligibility assessment', fakeAsync(() => {
    let project: OutputProject = {} as OutputProject;
    service.getProject().subscribe(res => project = res);

    service.init(1);
    service.setEligibilityAssessment({note: '', result: InputProjectEligibilityAssessment.ResultEnum.PASSED})

    httpTestingController.expectOne({method: 'POST', url: '//api/project/1/status/eligibility'})
      .flush({
        id: 1,
        eligibilityAssessment: {note: '', result: InputProjectEligibilityAssessment.ResultEnum.PASSED}
      });

    tick();
    expect(project.eligibilityAssessment.result).toEqual(InputProjectEligibilityAssessment.ResultEnum.PASSED);
  }));

  it('should change quality assessment', fakeAsync(() => {
    let project: OutputProject = {} as OutputProject;
    service.getProject().subscribe(res => project = res);

    service.init(1);
    service.setQualityAssessment({note: '', result: InputProjectQualityAssessment.ResultEnum.NOTRECOMMENDED})

    httpTestingController.expectOne({method: 'POST', url: '//api/project/1/status/quality'})
      .flush({
        id: 1,
        qualityAssessment: {note: '', result: InputProjectQualityAssessment.ResultEnum.NOTRECOMMENDED}
      });

    tick();
    expect(project.qualityAssessment.result).toEqual(InputProjectQualityAssessment.ResultEnum.NOTRECOMMENDED);
  }));
});
