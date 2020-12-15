import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CallBudgetSettingsPageComponent } from './call-budget-settings-page.component';
import {TestModule} from '../../../common/test-module';
import {CallModule} from '../../call.module';

describe('CallFlatRatesPageComponent', () => {
  let component: CallBudgetSettingsPageComponent;
  let fixture: ComponentFixture<CallBudgetSettingsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        CallModule
      ],
      declarations: [ CallBudgetSettingsPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CallBudgetSettingsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
