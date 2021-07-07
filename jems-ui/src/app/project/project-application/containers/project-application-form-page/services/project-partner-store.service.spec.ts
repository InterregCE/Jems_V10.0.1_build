import {fakeAsync, TestBed} from '@angular/core/testing';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProjectPartnerStore} from './project-partner-store.service';

describe('ProjectPartnerStoreService', () => {
  let service: ProjectPartnerStore;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule,
      ],
    });
    service = TestBed.inject(ProjectPartnerStore);
    httpTestingController = TestBed.inject(HttpTestingController);
    (service as any).projectId = 1;
    (service as any).partnerId = 2;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should update a project partner contact', fakeAsync(() => {
    service.updatePartnerContact([]).subscribe();

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/project/partner/2/contact`
    });
  }));

  it('should update a project partner motivation', fakeAsync(() => {
    service.updatePartnerMotivation({} as any).subscribe();

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/project/partner/2/motivation`
    });
  }));
});
