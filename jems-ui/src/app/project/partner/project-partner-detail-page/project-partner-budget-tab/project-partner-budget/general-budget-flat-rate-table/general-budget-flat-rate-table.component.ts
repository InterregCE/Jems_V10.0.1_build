import {ChangeDetectionStrategy, Component, Input} from '@angular/core';

@Component({
  selector: 'app-general-budget-flat-rate-table',
  templateUrl: './general-budget-flat-rate-table.component.html',
  styleUrls: ['./general-budget-flat-rate-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GeneralBudgetFlatRateTableComponent {

  @Input()
  total: number;
  @Input()
  description: string;

  columnsToDisplay = ['description', 'total'];

}
