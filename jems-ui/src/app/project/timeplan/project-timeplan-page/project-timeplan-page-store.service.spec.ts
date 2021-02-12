import { TestBed } from '@angular/core/testing';

import { ProjectTimeplanPageStore } from './project-timeplan-page-store.service';

describe('ProjectTimeplanPageStore', () => {
  let service: ProjectTimeplanPageStore;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProjectTimeplanPageStore);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
