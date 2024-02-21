import {UntilDestroy} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {MatTableDataSource} from '@angular/material/table';
import {
  PerPartnerCostCategoryBreakdownDTO,
  PerPartnerCostCategoryBreakdownLineDTO, ProjectPartnerReportUnitCostDTO
} from '@cat/api';
import CategoryEnum = ProjectPartnerReportUnitCostDTO.CategoryEnum;

@UntilDestroy()
@Component({
  selector: 'jems-project-breakdown-per-partner',
  templateUrl: './project-breakdown-per-partner.component.html',
  styleUrls: ['./project-breakdown-per-partner.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectBreakdownPerPartnerComponent implements OnChanges {

  dataSource: MatTableDataSource<PerPartnerCostCategoryBreakdownLineDTO> = new MatTableDataSource([]);
  displayedColumns: string[] = [];
  CategoryEnum = CategoryEnum;

  @Input()
  breakdown: PerPartnerCostCategoryBreakdownDTO;

  @Input()
  allowedCostCategories: Map<CategoryEnum | 'LumpSum' | 'UnitCost', boolean>;

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = this.breakdown.partners;
    this.displayedColumns = [
      'partner',
      'organizationAbbreviation',
      'country',
      ... this.allowedCostCategories.get(CategoryEnum.StaffCosts) ? ['staffCosts'] : [],
      ... this.allowedCostCategories.get(CategoryEnum.OfficeAndAdministrationCosts) ? ['officeAndAdministration'] : [],
      ... this.allowedCostCategories.get(CategoryEnum.TravelAndAccommodationCosts) ? ['travelAndAccommodation'] : [],
      ... this.allowedCostCategories.get(CategoryEnum.ExternalCosts) ? ['externalServices'] : [],
      ... this.allowedCostCategories.get(CategoryEnum.EquipmentCosts) ? ['equipment'] : [],
      ... this.allowedCostCategories.get(CategoryEnum.InfrastructureCosts) ? ['infraAndWorks'] : [],
      ... this.allowedCostCategories.get(CategoryEnum.Multiple) ? ['otherCosts'] : [],
      ... this.allowedCostCategories.get('LumpSum') ? ['lumpSum'] : [],
      ... this.allowedCostCategories.get('UnitCost') ? ['unitCosts'] : [],
      ... this.allowedCostCategories.get(CategoryEnum.SpfCosts) ? ['spfCosts'] : [],
      'totalBudget'
    ];
  }

}
