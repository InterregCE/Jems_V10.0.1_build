import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import { ProgrammeSimplifiedCostOptionsComponent } from './programme-simplified-cost-options.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';
import {ProgrammeModule} from '../../../programme.module';
import {ProgrammeLumpSumDTO} from '@cat/api';

describe('ProgrammeSimplifiedCostOptionsComponent', () => {
  let httpTestingController: HttpTestingController;
  let component: ProgrammeSimplifiedCostOptionsComponent;
  let fixture: ComponentFixture<ProgrammeSimplifiedCostOptionsComponent>;

  beforeEach(async(() => {
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
    let results: ProgrammeLumpSumDTO[] = [];
    component.currentLumpSumsPage$.subscribe(result => results = result.content);

    const sums = [
      {name: 'test1'} as ProgrammeLumpSumDTO,
      {name: 'test2'} as ProgrammeLumpSumDTO
    ];

    httpTestingController.match({method: 'GET', url: `//api/costOption/lumpSum?page=0&size=25&sort=id,desc`})
      .forEach(req => req.flush({content: sums}));

    tick();
    expect(results).toEqual(sums);
  }));
});
