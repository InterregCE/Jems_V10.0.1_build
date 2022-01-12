import {TestBed} from '@angular/core/testing';

import {ProgrammePageSidenavService} from './programme-page-sidenav.service';
import {TestModule} from '@common/test-module';
import {ProgrammeModule} from '../../programme.module';

describe('ProgrammePageSidenavService', () => {
  let service: ProgrammePageSidenavService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProgrammeModule
      ]
    });
    service = TestBed.inject(ProgrammePageSidenavService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
