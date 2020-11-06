import {fakeAsync, TestBed} from '@angular/core/testing';

import {CallStore} from './call-store.service';
import {CallModule} from '../call.module';
import {InputCallUpdate} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../common/test-module';

describe('CallStoreService', () => {
  let service: CallStore;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CallModule, TestModule]
    });
    service = TestBed.inject(CallStore);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should update a call', fakeAsync(() => {
    service.getCall().subscribe();
    service.saveCall$.next({} as InputCallUpdate);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/call`
    });
  }));
});
