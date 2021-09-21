import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {ProgrammeFundDTO} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProgrammeFundsComponent} from './programme-funds.component';
import {ProgrammeModule} from '../programme.module';
import {TestModule} from '@common/test-module';

describe('ProgrammeFundsComponent', () => {
  let httpTestingController: HttpTestingController;
  let component: ProgrammeFundsComponent;
  let fixture: ComponentFixture<ProgrammeFundsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ProgrammeFundsComponent],
      imports: [
        ProgrammeModule,
        TestModule
      ],
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammeFundsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch initial funds', fakeAsync(() => {
    let result: ProgrammeFundDTO[] = [];
    component.funds$.subscribe(res => result = res);

    httpTestingController.expectOne({method: 'GET', url: `//api/programmeFund`})
      .flush([{id: 1}]);

    tick();
    expect(result.length).toBe(1);
    expect(result[0].id).toBe(1);
  }));

  it('should update funds', fakeAsync(() => {
    let result: ProgrammeFundDTO[] = [];
    component.funds$.subscribe(res => result = res);
    component.saveFunds$.next([]);

    httpTestingController.expectOne({method: 'PUT', url: `//api/programmeFund`})
      .flush([{id: 1}]);

    tick(4100);
    expect(result.length).toBe(1);
    expect(result[0].id).toBe(1);
  }));
});
