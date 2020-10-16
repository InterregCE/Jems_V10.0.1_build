import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {OutputProgrammeFund} from '@cat/api';

@Component({
  selector: 'app-call-funds',
  templateUrl: './call-funds.component.html',
  styleUrls: ['./call-funds.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallFundsComponent {
  @Input()
  disabled: boolean;
  @Input()
  funds: OutputProgrammeFund[];

  @Output()
  selectionChanged = new EventEmitter<void>();
}
