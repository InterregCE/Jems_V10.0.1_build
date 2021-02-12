import { TestBed } from '@angular/core/testing';

import { ProjectTimeplanPageStoreService } from './project-timeplan-page-store.service';

describe('ProjectTimeplanPageStoreService', () => {
  let service: ProjectTimeplanPageStoreService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProjectTimeplanPageStoreService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
