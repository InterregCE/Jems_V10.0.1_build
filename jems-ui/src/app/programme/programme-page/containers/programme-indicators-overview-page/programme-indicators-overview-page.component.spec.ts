import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import { ProgrammeIndicatorsOverviewPageComponent } from './programme-indicators-overview-page.component';
import {TestModule} from '../../../../common/test-module';
import {ProgrammeModule} from '../../../programme.module';
import {OutputIndicatorOutput, OutputIndicatorResult} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';

describe('ProgrammeIndicatorsOverviewPageComponent', () => {
  let httpTestingController: HttpTestingController;
  let component: ProgrammeIndicatorsOverviewPageComponent;
  let fixture: ComponentFixture<ProgrammeIndicatorsOverviewPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProgrammeModule
      ],
      declarations: [ ProgrammeIndicatorsOverviewPageComponent ],
    })
    .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammeIndicatorsOverviewPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should list outputIndicators', fakeAsync(() => {
    let results: OutputIndicatorOutput[] = [];
    component.currentOutputIndicatorPage$.subscribe(result => results = result.content);

    const indicators = [
      {name: 'test1'} as OutputIndicatorOutput,
      {name: 'test2'} as OutputIndicatorOutput
    ];

    httpTestingController.match({method: 'GET', url: `//api/programmeindicator/output?page=0&size=25&sort=id,desc`})
      .forEach(req => req.flush({content: indicators}));

    tick();
    expect(results).toEqual(indicators);
  }));

  it('should list resultIndicators', fakeAsync(() => {
    let results: OutputIndicatorResult[] = [];
    component.currentResultIndicatorPage$.subscribe(result => results = result.content);

    const indicators = [
      {name: 'test1'} as OutputIndicatorResult,
      {name: 'test2'} as OutputIndicatorResult
    ];

    httpTestingController.match({method: 'GET', url: `//api/programmeindicator/result?page=0&size=25&sort=id,desc`})
      .forEach(req => req.flush({content: indicators}));

    tick();
    expect(results).toEqual(indicators);
  }));
});
