import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import { CallPageComponent } from './call-page.component';
import {TestModule} from '../../../common/test-module';
import {CallModule} from '../../call.module';
import {OutputCall} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';

describe('CallPageComponent', () => {
  let httpTestingController: HttpTestingController;
  let component: CallPageComponent;
  let fixture: ComponentFixture<CallPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        CallModule
      ],
      declarations: [ CallPageComponent ]
    })
    .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CallPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should list calls', fakeAsync(() => {
    let results: OutputCall[] = [];
    component.currentPage$.subscribe(result => results = result.content);

    const calls = [
      {name: 'test1'} as OutputCall,
      {name: 'test2'} as OutputCall
    ];

    httpTestingController.match({method: 'GET', url: `//api/call?page=0&size=25&sort=id,desc`})
      .forEach(req => req.flush({content: calls}));

    tick();
    expect(results).toEqual(calls);
  }));
});
