import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {SelectionModel} from '@angular/cdk/collections';
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
  @Input()
  selection: SelectionModel<OutputProgrammeFund>;
}
