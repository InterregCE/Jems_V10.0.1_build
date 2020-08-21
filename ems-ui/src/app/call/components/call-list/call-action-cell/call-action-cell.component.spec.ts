import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {CallActionCellComponent} from './call-action-cell.component';
import {OutputCall} from '@cat/api'
import * as moment from 'moment';
import {TranslateModule} from '@ngx-translate/core';

describe('CallActionCellComponent', () => {
  beforeEach(fakeAsync(() => { // 3
    TestBed.configureTestingModule({
      imports: [
        TranslateModule.forRoot()
      ],
      declarations: [
        CallActionCellComponent
      ],
    }).compileComponents();
  }));

  it('should be created', () => {
    const fixture = TestBed.createComponent(CallActionCellComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should correctly compare the dates ', fakeAsync(() => {
    const fixture = TestBed.createComponent(CallActionCellComponent);
    const actionCell = fixture.debugElement.componentInstance;

    actionCell.call = new class implements OutputCall {
      id = 1;
      name = 'Test';
      priorityPolicies = [];
      startDate = new Date('2020-08-01T14:15:00+02:00');
      endDate = new Date('2020-08-03T13:12:00+02:00');
      status = OutputCall.StatusEnum.PUBLISHED;
      description = 'test';
    } ();

    tick();
    expect(actionCell.isOpen()).toBe(false);

    actionCell.call = new class implements OutputCall {
      id = 1;
      name = 'Test';
      priorityPolicies = [];
      startDate = new Date('2020-08-11T14:15:00+05:00');
      endDate = new Date('2320-08-11T13:12:00+05:00');
      status = OutputCall.StatusEnum.PUBLISHED;
      description = 'test';
    } ();

    tick();
    expect(actionCell.isOpen()).toBe(true);

    // Testing the mechanism of the isExpired method from the component
    tick();
    expect(moment('2020-08-11T13:32:20+03:00').isAfter('2020-08-11T13:27:56+06:00')).toBe(true);

    tick();
    expect(moment('2020-08-11T13:32:20+03:00').isAfter('2020-08-11T13:27:56-02:00')).toBe(false);
  }));
});
