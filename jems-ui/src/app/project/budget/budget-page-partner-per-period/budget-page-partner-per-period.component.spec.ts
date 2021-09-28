import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BudgetPagePartnerPerPeriodComponent } from './budget-page-partner-per-period.component';

describe('BudgetPagePartnerPerPeriodComponent', () => {
  let component: BudgetPagePartnerPerPeriodComponent;
  let fixture: ComponentFixture<BudgetPagePartnerPerPeriodComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BudgetPagePartnerPerPeriodComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BudgetPagePartnerPerPeriodComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
