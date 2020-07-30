import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CallConfigurationComponent } from './call-configuration.component';
import {TestModule} from '../../../common/test-module';
import {CallModule} from '../../call.module';

describe('CallConfigurationComponent', () => {
  let component: CallConfigurationComponent;
  let fixture: ComponentFixture<CallConfigurationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        CallModule
      ],
      declarations: [ CallConfigurationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CallConfigurationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
