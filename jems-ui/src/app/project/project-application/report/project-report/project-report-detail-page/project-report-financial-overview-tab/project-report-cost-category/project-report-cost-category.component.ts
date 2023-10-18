import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {
  CertificateCostCategoryBreakdownDTO, CertificateCostCategoryBreakdownLineDTO,
  ProjectPartnerReportUnitCostDTO
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {FormService} from '@common/components/section/form/form.service';
import CategoryEnum = ProjectPartnerReportUnitCostDTO.CategoryEnum;

@Component({
  selector: 'jems-project-report-cost-category',
  templateUrl: './project-report-cost-category.component.html',
  styleUrls: ['./project-report-cost-category.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectReportCostCategoryComponent implements OnChanges {

  columnsAvailable = ['type', 'totalEligibleBudget', 'previouslyReported', 'currentReport', 'totalReportedSoFar', 'totalReportedSoFarPercentage', 'remainingBudget', 'previouslyVerified', 'currentVerified'];
  verifiedColumns = ['currentVerified'];
  displayedColumns = this.columnsAvailable;

  @Input()
  breakdown: CertificateCostCategoryBreakdownDTO;
  @Input()
  allowedCostCategories: Map<CategoryEnum | 'LumpSum' | 'UnitCost', boolean>;
  @Input()
  isVerified = false;

  dataSource: MatTableDataSource<CertificateLine> = new MatTableDataSource([]);

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
      ...(this.allowedCostCategories.get('SpfCosts') ?
        [{ ...this.breakdown.spfCost, translation: 'project.partner.spf.cost.type.spf'}] : []),
    ];
    this.displayedColumns = [...this.columnsAvailable]
        .filter(column => this.isVerified || !this.verifiedColumns.includes(column));
  }

}

interface CertificateLine extends CertificateCostCategoryBreakdownLineDTO {
  translation: string;
}
