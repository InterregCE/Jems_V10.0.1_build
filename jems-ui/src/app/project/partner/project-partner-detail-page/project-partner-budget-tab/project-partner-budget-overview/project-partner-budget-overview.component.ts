import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy} from '@ngneat/until-destroy';
import {combineLatest, Observable} from 'rxjs';
import {ProjectPartnerBudgetDTO, ProjectPartnerDetailDTO, ProjectService} from '@cat/api';
import {map} from 'rxjs/operators';
import {ActivatedRoute} from '@angular/router';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectPartnerDetailPageStore} from '@project/partner/project-partner-detail-page/project-partner-detail-page.store';
import {PartnerBudgetTables} from '@project/model/budget/partner-budget-tables';
import {BudgetOptions} from '@project/model/budget/budget-options';

@UntilDestroy()
@Component({
  selector: 'jems-project-partner-budget-overview',
  templateUrl: './project-partner-budget-overview.component.html',
  styleUrls: ['./project-partner-budget-overview.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerBudgetOverviewComponent {

  dataSource$: Observable<ProjectPartnerBudgetDTO[]> = combineLatest(
    [
      this.budgetStore.budgets$,
      this.budgetStore.totalBudget$,
      this.budgetStore.budgetOptions$,
      this.budgetStore.partnerTotalLumpSum$,
      this.budgetStore.partner$
    ]).pipe(
      map(([budgets, total, options, lumpSumTotal, partner]) =>
        this.calculateDataForTable(budgets, total, options, lumpSumTotal, partner)
      ),
    );

  constructor(
    private activatedRoute: ActivatedRoute,
    private projectService: ProjectService,
    public projectStore: ProjectStore,
    private projectVersionStore: ProjectVersionStore,
    private budgetStore: ProjectPartnerDetailPageStore,
  ) {
  }

  private calculateDataForTable(
    budgets: PartnerBudgetTables,
    total: number,
    options: BudgetOptions,
    lumpSumTotal: number,
    partner: ProjectPartnerDetailDTO,
  ): ProjectPartnerBudgetDTO[] {
    const staffCostsTotal = ProjectPartnerDetailPageStore.calculateStaffCostsTotal(
      options,
      budgets.staffCosts.total || 0,
      budgets.travelCosts.total || 0,
      budgets.externalCosts.total || 0,
      budgets.equipmentCosts.total || 0,
      budgets.infrastructureCosts.total || 0,
    );
    const travelCostsTotal = ProjectPartnerDetailPageStore.calculateTravelAndAccommodationCostsTotal(
      options.travelAndAccommodationOnStaffCostsFlatRate,
      staffCostsTotal,
      budgets.travelCosts.total || 0,
    );

    return [
      {
        partner: {
          id: partner.id,
          abbreviation: partner.abbreviation,
          role: partner.role,
          sortNumber: partner.sortNumber,
        },
        staffCosts: staffCostsTotal,
        travelCosts: travelCostsTotal,
        externalCosts: budgets.externalCosts.total,
        equipmentCosts: budgets.equipmentCosts.total,
        infrastructureCosts: budgets.infrastructureCosts.total,
        officeAndAdministrationCosts: ProjectPartnerDetailPageStore.calculateOfficeAndAdministrationFlatRateTotal(
          options.officeAndAdministrationOnStaffCostsFlatRate,
          options.officeAndAdministrationOnDirectCostsFlatRate,
          staffCostsTotal,
          travelCostsTotal,
          budgets.externalCosts.total || 0,
          budgets.equipmentCosts.total || 0,
          budgets.infrastructureCosts.total || 0,
        ),
        otherCosts: ProjectPartnerDetailPageStore.calculateOtherCostsFlatRateTotal(
          options.staffCostsFlatRate,
          options.otherCostsOnStaffCostsFlatRate || 0,
          budgets.staffCosts.total || 0,
        ),
        totalSum: total,
        lumpSumContribution: lumpSumTotal,
        unitCosts: budgets.unitCosts.total,
      } as ProjectPartnerBudgetDTO
    ];
  }

}
