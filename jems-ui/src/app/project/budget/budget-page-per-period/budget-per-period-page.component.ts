import {ChangeDetectionStrategy, Component} from '@angular/core';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {map} from 'rxjs/operators';
import {combineLatest, Observable} from 'rxjs';
import {ProjectBudgetPeriodPageStore} from '@project/budget/budget-page-per-period/budget-period-page.store';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'jems-budget-per-period-page',
  templateUrl: './budget-per-period-page.component.html',
  styleUrls: ['./budget-per-period-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetPerPeriodPageComponent {

  private readonly PERIOD_PREPARATION = 0;
  private readonly PERIOD_CLOSURE: number = 255;

  Alert = Alert;

  tableConfig: TableConfig[];
  projectPeriodNumbers: number[];
  projectPeriods$ = this.budgetPeriodStore.projectPeriods$;
  projectTitle$ = this.budgetPeriodStore.projectTitle$;

  data$: Observable<{
    projectTitle: string;
    projectPeriodNumbers: number[];
    tableConfig: TableConfig[];
    isAFPeriodsEnabled: boolean;
  }>;

  constructor(private budgetPeriodStore: ProjectBudgetPeriodPageStore) {
    this.data$ = combineLatest([this.projectTitle$, this.projectPeriods$]).pipe(
      map(([projectTitle, projectPeriods]) => {
        const projectPeriodNumbers = [this.PERIOD_PREPARATION, ...projectPeriods.map(projectPeriod => projectPeriod.number), this.PERIOD_CLOSURE];
        const tableConfig = this.getTableConfig(projectPeriodNumbers);
        return {
          projectTitle,
          projectPeriodNumbers,
          tableConfig,
          isAFPeriodsEnabled: projectPeriods.length > 0
        };
      })
    );
  }


  private getTableConfig(projectPeriodNumbers: number[]): TableConfig[] {
    return [{minInRem: 7},
            {minInRem: 7},
            ...projectPeriodNumbers.map(() => ({minInRem: 6})),
            {minInRem: 7}];
  }

}
