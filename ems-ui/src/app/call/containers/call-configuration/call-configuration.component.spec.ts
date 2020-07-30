import {async, ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';

import {CallConfigurationComponent} from './call-configuration.component';
import {TestModule} from '../../../common/test-module';
import {CallModule} from '../../call.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {InputCallCreate, InputCallUpdate} from '@cat/api';

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

  it('should publish a call', fakeAsync(() => {
    component.publishCall$.next(1);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/call/1/publish`
    })
  }));

  it('should update a call', fakeAsync(() => {
    component.saveCall$.next({} as InputCallUpdate);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/call`
    })
  }));

  it('should create a call', fakeAsync(() => {
    component.createCall({} as InputCallCreate);

    httpTestingController.expectOne({
      method: 'POST',
      url: `//api/call`
    })
  }));
});
