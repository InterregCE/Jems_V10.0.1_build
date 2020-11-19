import {fakeAsync, TestBed} from '@angular/core/testing';

import {CallStore} from './call-store.service';
import {CallModule} from '../call.module';
import {InputCallCreate, InputCallUpdate} from '@cat/api';
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
    service.saveCall({} as InputCallUpdate).subscribe();

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/call`
    });
  }));

  it('should publish a call', fakeAsync(() => {
    service.publishCall(1).subscribe();

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/call/1/publish`
    });
  }));

  it('should create a call', fakeAsync(() => {
    service.createCall({} as InputCallCreate).subscribe();

    httpTestingController.expectOne({
      method: 'POST',
      url: `//api/call`
    });
  }));
});
