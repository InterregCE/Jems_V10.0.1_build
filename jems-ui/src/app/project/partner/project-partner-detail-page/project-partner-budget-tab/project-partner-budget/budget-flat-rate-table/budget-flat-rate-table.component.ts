import {ChangeDetectionStrategy, Component, Input} from '@angular/core';

@Component({
  selector: 'jems-budget-flat-rate-table',
  templateUrl: './budget-flat-rate-table.component.html',
  styleUrls: ['./budget-flat-rate-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetFlatRateTableComponent {

  @Input()
  total: number;
  @Input()
  description: string;

  columnsToDisplay = ['description', 'total'];

}
