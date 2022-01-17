import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';

import { ProgrammeIndicatorsOverviewPageComponent } from './programme-indicators-overview-page.component';
import {TestModule} from '../../../../common/test-module';
import {ProgrammeModule} from '../../../programme.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {InputTranslation, OutputIndicatorDetailDTO, ResultIndicatorDetailDTO} from '@cat/api';

describe('ProgrammeIndicatorsOverviewPageComponent', () => {
  let httpTestingController: HttpTestingController;
  let component: ProgrammeIndicatorsOverviewPageComponent;
  let fixture: ComponentFixture<ProgrammeIndicatorsOverviewPageComponent>;

  beforeEach(waitForAsync(() => {
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
    let results: OutputIndicatorDetailDTO[] = [];
    component.currentOutputIndicatorPage$.subscribe(result => results = result.content);

    const indicators = [
      {name: [{language: InputTranslation.LanguageEnum.EN, translation: 'test1'} as InputTranslation]} as OutputIndicatorDetailDTO,
      {name: [{language: InputTranslation.LanguageEnum.EN, translation: 'test2'} as InputTranslation]} as OutputIndicatorDetailDTO
    ];

    httpTestingController.match({method: 'GET', url: `//api/programmeindicator/output?page=0&size=25&sort=id,desc`})
      .forEach(req => req.flush({content: indicators}));

    tick();
    expect(results).toEqual(indicators);
  }));

  it('should list resultIndicators', fakeAsync(() => {
    let results: ResultIndicatorDetailDTO[] = [];
    component.currentResultIndicatorPage$.subscribe(result => results = result.content);

    const indicators = [
      {name: [{language: InputTranslation.LanguageEnum.EN, translation: 'test1'} as InputTranslation]} as ResultIndicatorDetailDTO,
      {name: [{language: InputTranslation.LanguageEnum.EN, translation: 'test2'} as InputTranslation]} as ResultIndicatorDetailDTO
    ];

    httpTestingController.match({method: 'GET', url: `//api/programmeindicator/result?page=0&size=25&sort=id,desc`})
      .forEach(req => req.flush({content: indicators}));

    tick();
    expect(results).toEqual(indicators);
  }));
});
