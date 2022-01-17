import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';

import { ProgrammeStrategiesPageComponent } from './programme-strategies-page.component';
import {TestModule} from '../../../../common/test-module';
import {ProgrammeModule} from '../../../programme.module';
import {InputProgrammeStrategy, OutputProgrammeStrategy} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';

describe('ProgrammeStrategiesPageComponent', () => {
  let component: ProgrammeStrategiesPageComponent;
  let fixture: ComponentFixture<ProgrammeStrategiesPageComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProgrammeModule
      ],
      declarations: [ ProgrammeStrategiesPageComponent ]
    })
    .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammeStrategiesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch initial strategies', fakeAsync(() => {
    let result: OutputProgrammeStrategy[] = [];
    component.strategies$.subscribe(res => result = res);

    httpTestingController.match({method: 'GET', url: `//api/programmestrategy`}).forEach(req => req.flush([{
      strategy: InputProgrammeStrategy.StrategyEnum.EUStrategyAdriaticIonianRegion,
      active: false
    } as OutputProgrammeStrategy]));

    tick();
    console.log(result);
    expect(result.length).toBe(1);
    expect(result[0].strategy).toBe(InputProgrammeStrategy.StrategyEnum.EUStrategyAdriaticIonianRegion);
    expect(result[0].active).toBe(false);
  }));

  it('should update funds', fakeAsync(() => {
    let result: OutputProgrammeStrategy[] = [];
    component.details$.subscribe(res => result = res);
    component.saveStrategies$.next([{strategy: InputProgrammeStrategy.StrategyEnum.EUStrategyAdriaticIonianRegion, active: true} as InputProgrammeStrategy]);

    httpTestingController.match({method: 'PUT', url: `//api/programmestrategy`})
      .forEach(req => req.flush([{
        strategy: InputProgrammeStrategy.StrategyEnum.EUStrategyAdriaticIonianRegion,
        active: true
      } as OutputProgrammeStrategy]));

    tick();
    expect(result.length).toBe(1);
    expect(result[0].strategy).toBe(InputProgrammeStrategy.StrategyEnum.EUStrategyAdriaticIonianRegion);
    expect(result[0].active).toBe(true);
  }));
});
