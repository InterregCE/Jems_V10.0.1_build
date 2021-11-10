import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';

import { ProgrammeSimplifiedCostOptionsComponent } from './programme-simplified-cost-options.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';
import {ProgrammeModule} from '../../../programme.module';
import {ProgrammeLumpSumListDTO, ProgrammeUnitCostListDTO} from '@cat/api';

describe('ProgrammeSimplifiedCostOptionsComponent', () => {
  let httpTestingController: HttpTestingController;
  let component: ProgrammeSimplifiedCostOptionsComponent;
  let fixture: ComponentFixture<ProgrammeSimplifiedCostOptionsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProgrammeModule
      ],
      declarations: [ ProgrammeSimplifiedCostOptionsComponent ]
    })
    .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammeSimplifiedCostOptionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should list lumpSums', fakeAsync(() => {
    let results: ProgrammeLumpSumListDTO[] = [];
    component.lumpSumsDataSource$.subscribe(result => results = result.data);

    const sums = [
      {name: [ { language: 'EN', translation: 'test1'} ] } as ProgrammeLumpSumListDTO,
      {name: [ { language: 'EN', translation: 'test2'} ] } as ProgrammeLumpSumListDTO
    ];

    httpTestingController.match({method: 'GET', url: `//api/costOption/lumpSum`})
      .forEach(req => req.flush(sums));

    tick();
    expect(results).toEqual(sums);
  }));

  it('should list unitCosts', fakeAsync(() => {
    let results: ProgrammeUnitCostListDTO[] = [];
    component.unitCostDataSource$.subscribe(result => results = result.data);

    const sums = [
      {name: [ { language: 'EN', translation: 'test1'} ] } as ProgrammeUnitCostListDTO,
      {name: [ { language: 'EN', translation: 'test2'} ] } as ProgrammeUnitCostListDTO
    ];

    httpTestingController.match({method: 'GET', url: `//api/costOption/unitCost`})
      .forEach(req => req.flush(sums));

    tick();
    expect(results).toEqual(sums);
  }));
});
