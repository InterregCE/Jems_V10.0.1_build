
import {CallPageSidenavService} from './call-page-sidenav.service';
import {TestBed} from '@angular/core/testing';
import {CallModule} from '../call.module';
import {TestModule} from '../../common/test-module';

describe('CallPageSidenavService', () => {
  let service: CallPageSidenavService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        CallModule
      ]
    });
    service = TestBed.inject(CallPageSidenavService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
