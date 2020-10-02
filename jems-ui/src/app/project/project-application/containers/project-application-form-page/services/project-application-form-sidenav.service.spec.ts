import {TestBed} from '@angular/core/testing';

import {ProjectApplicationFormSidenavService} from './project-application-form-sidenav.service';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';

describe('ProjectApplicationFormSIdenavService', () => {
  let service: ProjectApplicationFormSidenavService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ]
    });
    service = TestBed.inject(ProjectApplicationFormSidenavService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
