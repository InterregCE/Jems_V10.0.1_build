import {fakeAsync, TestBed, tick} from '@angular/core/testing';

import {CallListStore} from '@common/components/call-list/call-list-store.service';
import {CallDTO} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../test-module';

describe('CallListStoreService', () => {
  let service: CallListStore;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule],
      providers: [CallListStore]
    });
    service = TestBed.inject(CallListStore);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should list calls', fakeAsync(() => {
    let results: CallDTO[] = [];
    service.callPage$.subscribe(result => results = result.content);

    const calls = [
      {name: 'test1'} as CallDTO,
      {name: 'test2'} as CallDTO
    ];

    httpTestingController.match({method: 'GET', url: `//api/call?page=0&size=25&sort=id,desc`})
      .forEach(req => req.flush({content: calls}));

    tick();
    expect(results).toEqual(calls);
  }));
});
