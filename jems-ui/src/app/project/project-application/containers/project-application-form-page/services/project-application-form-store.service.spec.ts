import {fakeAsync, TestBed} from '@angular/core/testing';

import {ProjectApplicationFormStore} from './project-application-form-store.service';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';
import {HttpTestingController} from '@angular/common/http/testing';

describe('ProjectApplicationFormStoreService', () => {
  let service: ProjectApplicationFormStore;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ]
    });
    service = TestBed.inject(ProjectApplicationFormStore);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch the description', fakeAsync(() => {
    service.getProjectDescription().subscribe();
    service.init(1);

    httpTestingController.expectOne({
      method: 'GET',
      url: '//api/project/1/description'
    });
  }));
});
