import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {ProgrammeFundDTO} from '@cat/api';

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
  funds: ProgrammeFundDTO[];
  @Input()
  isApplicant: boolean;
  @Input()
  initialFunds: ProgrammeFundDTO[];


  @Output()
  selectionChanged = new EventEmitter<void>();

  isFundAlreadySelected(fund: ProgrammeFundDTO): boolean {
    const foundFund = this.initialFunds.find(initialFunds => initialFunds.id === fund.id);
    return !!(foundFund && foundFund.selected);
  }
}
