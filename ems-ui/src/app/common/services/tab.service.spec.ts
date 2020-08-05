import {fakeAsync, TestBed, tick} from '@angular/core/testing';

import {TabService} from './tab.service';
import {TestModule} from '../test-module';

describe('TabService', () => {
  let service: TabService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule]
    });
    service = TestBed.inject(TabService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should persist and clean the tab index', fakeAsync(() => {
    let currentTab = -1;
    service.currentTab('test').subscribe(tab => currentTab = tab);

    service.changeTab('test', 0);
    tick();
    expect(currentTab).toEqual(0);
    expect(localStorage.getItem('test')).toEqual('0');

    service.cleanupTab('test');
    expect(localStorage.getItem('test')).toBeFalsy();
  }));
});
