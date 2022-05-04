import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Observable} from 'rxjs';
import {ProjectPartnerBudgetPerPeriodDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {ProjectBudgetPeriodPageStore} from '@project/budget/budget-page-per-period/budget-period-page.store';


@Component({
  selector: 'jems-budget-page-partner-per-period',
  templateUrl: './budget-page-partner-per-period.component.html',
  styleUrls: ['./budget-page-partner-per-period.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetPagePartnerPerPeriodComponent {

  readonly PERIOD_PREPARATION: number = 0;
  readonly PERIOD_CLOSURE: number = 255;

  @Input() tableConfig: TableConfig[];
  @Input() projectPeriodNumbers: number[];

  APPLICATION_FORM = APPLICATION_FORM;

  data$: Observable<{
    partnersBudgetPerPeriod: ProjectPartnerBudgetPerPeriodDTO[];
    totals: number[];
    totalsPercentage: number[];
    periodsAvailable: boolean;
  }>;

  private static sortByNumber(a: ProjectPartnerBudgetPerPeriodDTO, b: ProjectPartnerBudgetPerPeriodDTO): number {
    return a.partner.sortNumber - b.partner.sortNumber;
  }


  constructor(
    private budgetPeriodStore: ProjectBudgetPeriodPageStore,
  ) {
    this.data$ = this.budgetPeriodStore.projectBudgetOverviewPerPartnerPerPeriods$
      .pipe(
        map((projectBudgetOverviewPerPartnerPerPeriod) => {
          const perPeriodSorted = [...projectBudgetOverviewPerPartnerPerPeriod.partnersBudgetPerPeriod]
            .sort(BudgetPagePartnerPerPeriodComponent.sortByNumber);
          return {
            partnersBudgetPerPeriod: perPeriodSorted,
            totals: projectBudgetOverviewPerPartnerPerPeriod.totals,
            totalsPercentage: projectBudgetOverviewPerPartnerPerPeriod.totalsPercentage,
            periodsAvailable: this.projectPeriodNumbers.length > 0
          };
        }),
      );
  }
}
