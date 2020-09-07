import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProgrammeStrategiesPageComponent } from './programme-strategies-page.component';
import {TestModule} from '../../../../common/test-module';
import {ProgrammeModule} from '../../../programme.module';

describe('ProgrammeStrategiesPageComponent', () => {
  let component: ProgrammeStrategiesPageComponent;
  let fixture: ComponentFixture<ProgrammeStrategiesPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProgrammeModule
      ],
      declarations: [ ProgrammeStrategiesPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammeStrategiesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
