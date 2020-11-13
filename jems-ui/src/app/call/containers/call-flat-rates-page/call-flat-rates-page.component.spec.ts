import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CallFlatRatesPageComponent } from './call-flat-rates-page.component';
import {TestModule} from '../../../common/test-module';
import {CallModule} from '../../call.module';

describe('CallFlatRatesPageComponent', () => {
  let component: CallFlatRatesPageComponent;
  let fixture: ComponentFixture<CallFlatRatesPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        CallModule
      ],
      declarations: [ CallFlatRatesPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CallFlatRatesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
