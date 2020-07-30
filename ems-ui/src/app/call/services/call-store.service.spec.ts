import {fakeAsync, TestBed, tick} from '@angular/core/testing';

import {CallStore} from './call-store.service';
import {CallModule} from '../call.module';
import {OutputCall} from '@cat/api';

describe('CallStoreService', () => {
  let service: CallStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CallModule]
    });
    service = TestBed.inject(CallStore);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should provide published call name', fakeAsync(() => {
    const providedValues: (string | null)[] = [];
    service.publishedCall().subscribe(val => providedValues.push(val));

    service.callPublished({name: 'callName'} as OutputCall)

    tick();
    expect(providedValues).toEqual(['callName']);
    tick(5000);
    expect(providedValues).toEqual(['callName', null]);
  }));
});
