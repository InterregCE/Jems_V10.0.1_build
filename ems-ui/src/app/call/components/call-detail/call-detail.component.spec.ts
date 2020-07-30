import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CallDetailComponent } from './call-detail.component';
import {TestModule} from '../../../common/test-module';
import {CallModule} from '../../call.module';

describe('CallDetailComponent', () => {
  let component: CallDetailComponent;
  let fixture: ComponentFixture<CallDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        CallModule
      ],
      declarations: [ CallDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CallDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
