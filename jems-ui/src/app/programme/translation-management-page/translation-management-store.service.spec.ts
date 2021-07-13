import { TestBed } from '@angular/core/testing';

import { TranslationManagementStore } from './translation-management-store.service';

describe('TranslationManagementStoreService', () => {
  let service: TranslationManagementStore;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TranslationManagementStore);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
