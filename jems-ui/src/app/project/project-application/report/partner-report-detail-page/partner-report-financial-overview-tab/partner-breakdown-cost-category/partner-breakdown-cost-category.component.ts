import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {
  ExpenditureCostCategoryBreakdownDTO,
  ExpenditureCostCategoryBreakdownLineDTO,
  ProjectPartnerReportUnitCostDTO,
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import CategoryEnum = ProjectPartnerReportUnitCostDTO.CategoryEnum;

@UntilDestroy()
@Component({
  selector: 'jems-partner-breakdown-cost-category',
  templateUrl: './partner-breakdown-cost-category.component.html',
  styleUrls: ['./partner-breakdown-cost-category.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerBreakdownCostCategoryComponent implements OnChanges {

  certifiedColumns = ['totalEligibleAfterControl'];
  columnsAvailable = ['type', 'flatRate', 'totalEligibleBudget', 'previouslyReported', 'currentReport', 'totalEligibleAfterControl', 'totalReportedSoFar', 'totalReportedSoFarPercentage', 'remainingBudget'];
  displayedColumns = this.columnsAvailable;

  @Input()
  breakdown: ExpenditureCostCategoryBreakdownDTO;
  @Input()
  allowedCostCategories: Map<CategoryEnum | 'LumpSum' | 'UnitCost', boolean>;
  @Input()
  isCertified = false;

  dataSource: MatTableDataSource<ExpenditureLine> = new MatTableDataSource([]);

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = [
      ...(this.allowedCostCategories.get(CategoryEnum.StaffCosts) ?
        [{ ...this.breakdown.staff, translation: 'project.partner.budget.staff'}] : []),
      ...(this.allowedCostCategories.get(CategoryEnum.OfficeAndAdministrationCosts) ?
        [{ ...this.breakdown.office, translation: 'unit.cost.cost.category.OfficeAndAdministrationCosts'}] : []),
      ...(this.allowedCostCategories.get(CategoryEnum.TravelAndAccommodationCosts) ?
        [{ ...this.breakdown.travel, translation: 'project.partner.budget.travel'}] : []),
      ...(this.allowedCostCategories.get(CategoryEnum.ExternalCosts) ?
        [{ ...this.breakdown.external, translation: 'project.partner.budget.external'}] : []),
      ...(this.allowedCostCategories.get(CategoryEnum.EquipmentCosts) ?
        [{ ...this.breakdown.equipment, translation: 'project.partner.budget.equipment'}] : []),
      ...(this.allowedCostCategories.get(CategoryEnum.InfrastructureCosts) ?
        [{ ...this.breakdown.infrastructure, translation: 'project.partner.budget.infrastructure'}] : []),
      ...(this.allowedCostCategories.get(CategoryEnum.Multiple) ?
        [{ ...this.breakdown.other, translation: 'project.partner.budget.other'}] : []),
      ...(this.allowedCostCategories.get('LumpSum') ?
        [{ ...this.breakdown.lumpSum, translation: 'project.partner.budget.lumpSum'}] : []),
      ...(this.allowedCostCategories.get('UnitCost') ?
        [{ ...this.breakdown.unitCost, translation: 'project.partner.budget.unitCosts'}] : []),
    ];
    this.displayedColumns = [...this.columnsAvailable]
      .filter(column => this.isCertified || !this.certifiedColumns.includes(column));
  }

}

interface ExpenditureLine extends ExpenditureCostCategoryBreakdownLineDTO {
  translation: string;
}
