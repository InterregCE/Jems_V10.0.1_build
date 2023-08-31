import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {
  ControlDeductionOverviewDTO,
  ControlDeductionOverviewRowDTO,
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'jems-control-report-deduction-overview',
  templateUrl: './control-report-deduction-overview.component.html',
  styleUrls: ['./control-report-deduction-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ControlReportDeductionOverviewComponent implements OnChanges {

  displayedColumns = ['typeOfError', 'staff', 'officeAndAdministration', 'travelAndAccommodation', 'external', 'equipment', 'infrastructure', 'lumpSum', 'unitCost', 'other', 'total'];
  displayedSubColumns = this.displayedColumns.map(col => `${col}-sub`);

  dataSource = new MatTableDataSource<ControlDeductionOverviewRowDTO>([]);
  total: ControlDeductionOverviewRowDTO;
  staffCostsFlatRate: number;
  officeAndAdministrationFlatRate: number;
  travelAndAccommodationFlatRate: number;
  otherCostsOnStaffCostsFlatRate: number;
  showFlatRates: boolean;

  @Input()
  deductionData: ControlDeductionOverviewDTO;

  ngOnChanges(changes: SimpleChanges): void {
    this.showFlatRates = !!this.deductionData.staffCostsFlatRate
      || !!this.deductionData.officeAndAdministrationFlatRate
      || !!this.deductionData.travelAndAccommodationFlatRate
      || !!this.deductionData.otherCostsOnStaffCostsFlatRate;

    this.dataSource.data = this.deductionData.deductionRows.filter(row => row.total)
    this.total = this.deductionData.total;

    this.staffCostsFlatRate = this.deductionData.staffCostsFlatRate;
    this.officeAndAdministrationFlatRate = this.deductionData.officeAndAdministrationFlatRate;
    this.travelAndAccommodationFlatRate = this.deductionData.travelAndAccommodationFlatRate;
    this.otherCostsOnStaffCostsFlatRate = this.deductionData.otherCostsOnStaffCostsFlatRate;
  }

}

