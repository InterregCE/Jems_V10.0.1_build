import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-budget-flat-rate-table',
  templateUrl: './budget-flat-rate-table.component.html',
  styleUrls: ['./budget-flat-rate-table.component.scss']
})
export class BudgetFlatRateTableComponent {

  @Input()
  total: number;
  @Input()
  description: string;

  columnsToDisplay = ['description', 'total'];

  constructor() {
  }

}
