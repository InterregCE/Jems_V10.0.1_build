import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {combineLatest, Observable} from 'rxjs';
import {CallDetailDTO, ProjectFundBudgetPerPeriodDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {NumberService} from '@common/services/number.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {ProjectBudgetPeriodPageStore} from '@project/budget/budget-page-per-period/budget-period-page.store';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';

@Component({
  selector: 'jems-budget-page-funds-per-period',
  templateUrl: './budget-page-fund-per-period.component.html',
  styleUrls: ['./budget-page-fund-per-period.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetPageFundPerPeriodComponent {

  readonly PERIOD_PREPARATION: number = 0;
  readonly PERIOD_CLOSURE: number = 255;

  APPLICATION_FORM = APPLICATION_FORM;

  @Input() tableConfig: TableConfig[];
  @Input() projectPeriodNumbers: number[];

  data$: Observable<{
    managementEuFundsDetails:  ProjectFundBudgetPerPeriodDTO[];
    spfEuFundsDetails:  ProjectFundBudgetPerPeriodDTO[];
    managementNonEuFundsDetails:  ProjectFundBudgetPerPeriodDTO[];
    spfNonEuFundsDetails: ProjectFundBudgetPerPeriodDTO[];
    totalManagementEuFundsPerPeriod: number[];
    totalSpfEuFundsPerPeriod: number[];
    totalManagementFundsPerPeriod: number[];
    totalSpfFundsPerPeriod: number[];
    totalManagementEuFunds: number;
    totalSpfEuFunds: number;
    totalManagementFunds: number;
    totalSpfFunds: number;
    isAFPeriodsEnabled: boolean;
    isCallTypeSpf: boolean;
  }>;

  constructor(private budgetPeriodStore: ProjectBudgetPeriodPageStore,
              private projectStore: ProjectStore) {
    this.data$ = combineLatest([
      this.budgetPeriodStore.projectBudgetFundsPerPeriod$,
      this.projectStore.projectCallType$
    ]).pipe(
        map(([budgetFundsDetails, callType]): any => {
            const managementEuFundsDetails = budgetFundsDetails.managementFundsPerPeriod.filter(budgetFundsDetail => budgetFundsDetail.fund.type !== 'OTHER');
            const managementNonEuFundsDetails = budgetFundsDetails.managementFundsPerPeriod.filter(budgetFundsDetail => budgetFundsDetail.fund.type === 'OTHER');
            const totalManagementEuFundsPerPeriod = this.calculateTotalFundsPerPeriods(managementEuFundsDetails, this.projectPeriodNumbers);
            const totalManagementFundsPerPeriod = this.calculateTotalFundsPerPeriods(managementNonEuFundsDetails.concat(managementEuFundsDetails), this.projectPeriodNumbers);

            const spfEuFundsDetails = budgetFundsDetails.spfFundsPerPeriod.filter(budgetFundsDetail => budgetFundsDetail.fund.type !== 'OTHER');
            const spfNonEuFundsDetails =  budgetFundsDetails.spfFundsPerPeriod.filter(budgetFundsDetail => budgetFundsDetail.fund.type === 'OTHER');
            const totalSpfEuFundsPerPeriod = this.calculateTotalFundsPerPeriods(spfEuFundsDetails, this.projectPeriodNumbers);
            const totalSpfFundsPerPeriod = this.calculateTotalFundsPerPeriods(spfNonEuFundsDetails.concat(spfEuFundsDetails), this.projectPeriodNumbers);
            return {
              managementEuFundsDetails,
              spfEuFundsDetails,
              managementNonEuFundsDetails,
              spfNonEuFundsDetails,
              totalManagementEuFundsPerPeriod,
              totalSpfEuFundsPerPeriod,
              totalManagementFundsPerPeriod,
              totalSpfFundsPerPeriod,
              totalManagementEuFunds: NumberService.sum(totalManagementEuFundsPerPeriod),
              totalSpfEuFunds: NumberService.sum(totalSpfEuFundsPerPeriod),
              totalManagementFunds: NumberService.sum(totalManagementFundsPerPeriod),
              totalSpfFunds: NumberService.sum(totalSpfFundsPerPeriod),
              isAFPeriodsEnabled: this.projectPeriodNumbers.length > 0,
              isCallTypeSpf: callType === CallDetailDTO.TypeEnum.SPF
            };
          }
        ));
  }

  getPeriodTranslation(periodNumber: number): string {
    if (periodNumber === this.PERIOD_PREPARATION) {
      return 'project.application.form.section.part.e.period.preparation';
    }

    if (periodNumber === this.PERIOD_CLOSURE) {
      return 'project.application.form.section.part.e.period.closure';
    }

    return 'project.partner.budget.table.period';
  }

  private calculateTotalFundsPerPeriods(budgetFundsDetails: ProjectFundBudgetPerPeriodDTO[], projectPeriodNumbers: number[]): number[] {
    return projectPeriodNumbers.map(projectPeriod => NumberService.sum(budgetFundsDetails
      .flatMap(budgetFundsDetail => budgetFundsDetail.periodFunds
        .filter(periodFund => periodFund.periodNumber === projectPeriod)
        .map(periodFund => periodFund.totalFundsPerPeriod))));
  }
}
