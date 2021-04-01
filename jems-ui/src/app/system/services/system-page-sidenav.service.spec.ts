
import {SystemPageSidenavService} from './system-page-sidenav.service';
import {TestBed} from '@angular/core/testing';
import {TestModule} from '../../common/test-module';
import {SystemModule} from '../system.module';

describe('SystemPageSidenavService', () => {
  let service: SystemPageSidenavService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        SystemModule
      ]
    });
    service = TestBed.inject(SystemPageSidenavService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
