import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {OutputCall} from '@cat/api';
import * as moment from 'moment';

@Component({
  selector: 'app-call-action-cell',
  templateUrl: './call-action-cell.component.html',
  styleUrls: ['./call-action-cell.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallActionCellComponent {
  @Input()
  call: OutputCall;

  @Output()
  apply = new EventEmitter<number>();

  isOpen(): boolean {
    const currentDate = moment(new Date());
    return currentDate.isBefore(this.call.endDate) && currentDate.isAfter(this.call.startDate);
  }
}
