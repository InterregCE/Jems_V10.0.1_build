import {AfterViewInit, ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {
  ControlDeductionOverviewDTO,
  ControlDeductionOverviewRowDTO,
  ControlOverviewDTO,
  ControlWorkOverviewDTO,
  ProjectPartnerReportControlOverviewService,
  ProjectPartnerReportDTO, ProjectPartnerReportUnitCostDTO
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {catchError, finalize, map, take, tap} from 'rxjs/operators';
import {
  PartnerControlReportOverviewAndFinalizeStore
} from '@project/project-application/report/partner-control-report/partner-control-report-overview-and-finalize-tab/partner-control-report-overview-and-finalize.store';
import {
  PartnerControlReportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import {FormService} from '@common/components/section/form/form.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {APIError} from '@common/models/APIError';
import {ActivatedRoute, Router} from '@angular/router';
import {Alert} from '@common/components/forms/alert';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {LocaleDatePipe} from '@common/pipe/locale-date.pipe';
import CategoryEnum = ProjectPartnerReportUnitCostDTO.CategoryEnum;
import {
  PartnerReportFinancialOverviewStoreService
} from '@project/project-application/report/partner-report-detail-page/partner-report-financial-overview-tab/partner-report-financial-overview-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'jems-control-report-deduction-overview',
  templateUrl: './control-report-deduction-overview.component.html',
  styleUrls: ['./control-report-deduction-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ControlReportDeductionOverviewComponent implements OnChanges {

  displayedColumns = ['typeOfError', 'staff', 'officeAndAdministration', 'travelAndAccommodation', 'external', 'equipment', 'infrastructure', 'lumpSum', 'other', 'total'];
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

    this.dataSource.data = this.deductionData.deductionRows;
    this.total = this.deductionData.total;

    this.staffCostsFlatRate = this.deductionData.staffCostsFlatRate;
    this.officeAndAdministrationFlatRate = this.deductionData.officeAndAdministrationFlatRate;
    this.travelAndAccommodationFlatRate = this.deductionData.travelAndAccommodationFlatRate;
    this.otherCostsOnStaffCostsFlatRate = this.deductionData.otherCostsOnStaffCostsFlatRate;
  }

}

