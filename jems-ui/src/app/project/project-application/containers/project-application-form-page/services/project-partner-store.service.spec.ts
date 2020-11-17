import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProjectPartnerStore} from './project-partner-store.service';
import {OutputProjectPartner} from '@cat/api';

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
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch the initial partner', fakeAsync(() => {
    let resultPartner;
    service.partner$.subscribe(result => resultPartner = result as OutputProjectPartner);
    service.init(2, 1);

    httpTestingController.match({method: 'GET', url: `//api/project/1/partner/2`})
      .forEach(req => req.flush({id: 2}));

    tick();
    expect(resultPartner).toEqual({id: 2} as any);
  }));

  it('should update a project partner contact', fakeAsync(() => {
    service.init(2, 1);
    service.updatePartnerContact([]).subscribe();

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/project/1/partner/2/contact`
    });
  }));

  it('should update a project partner contribution', fakeAsync(() => {
    service.init(2, 1);
    service.updatePartnerContribution({} as any).subscribe();

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/project/1/partner/2/contribution`
    });
  }));
});
