import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';

import {CallConfigurationComponent} from './call-configuration.component';
import {TestModule} from '../../../common/test-module';
import {CallModule} from '../../call.module';
import {HttpTestingController} from '@angular/common/http/testing';

describe('CallConfigurationComponent', () => {
  let component: CallConfigurationComponent;
  let fixture: ComponentFixture<CallConfigurationComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        CallModule
      ],
      declarations: [CallConfigurationComponent]
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
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
