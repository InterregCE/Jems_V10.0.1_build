import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {
  CertificateVerificationDeductionOverviewDTO, ControlDeductionOverviewRowDTO, VerificationDeductionOverviewDTO,
  VerificationDeductionOverviewRowDTO,
  VerificationWorkOverviewLineDTO
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'jems-verification-deduction-per-certificate',
  templateUrl: './verification-deduction-per-certificate.component.html',
  styleUrls: ['./verification-deduction-per-certificate.component.scss']
})
export class VerificationDeductionPerCertificateComponent implements OnChanges {

  overviewTableData: OverviewTableData[] = [];

  @Input()
  certificatesDeductionOverviews: CertificateVerificationDeductionOverviewDTO[] = [];

  displayedColumns = ['typeOfError', 'staff', 'officeAndAdministration', 'travelAndAccommodation', 'external', 'equipment', 'infrastructure', 'lumpSum', 'unitCost', 'other', 'total'];
  displayedSubColumns = this.displayedColumns.map(col => `${col}-sub`);


  ngOnChanges(changes: SimpleChanges): void {
    this.certificatesDeductionOverviews.map( certificateOverview => {
      const deductionOverviewData = certificateOverview.deductionOverview;
      const tableDataSource = new MatTableDataSource<ControlDeductionOverviewRowDTO>(deductionOverviewData.deductionRows);

      const overviewTableData: OverviewTableData = {
        partnerReportNumber: certificateOverview.partnerReportNumber,
        partnerName: this.getPartnerName(certificateOverview.partnerRole, certificateOverview.partnerNumber),
        staffCostsFlatRate: deductionOverviewData.staffCostsFlatRate,
        officeAndAdministrationFlatRate: deductionOverviewData.officeAndAdministrationFlatRate,
        travelAndAccommodationFlatRate: deductionOverviewData.travelAndAccommodationFlatRate,
        otherCostsOnStaffCostsFlatRate: deductionOverviewData.otherCostsOnStaffCostsFlatRate,
        showFlatRates: this.showFlatRates(deductionOverviewData),
        deductionRows: tableDataSource,
        total: deductionOverviewData.total
      };

      this.overviewTableData.push(overviewTableData);
    });
  }

  private showFlatRates(deductionOverviewData: VerificationDeductionOverviewDTO): boolean {
    return !!deductionOverviewData.staffCostsFlatRate
        || !!deductionOverviewData.officeAndAdministrationFlatRate
        || !!deductionOverviewData.travelAndAccommodationFlatRate
        || !!deductionOverviewData.otherCostsOnStaffCostsFlatRate;
  }
  private getPartnerName(partnerRole: CertificateVerificationDeductionOverviewDTO.PartnerRoleEnum, partnerNumber: number): string {
    if (partnerRole === 'LEAD_PARTNER') {
      return `LP${partnerNumber}`;
    } else {
      return `PP${partnerNumber}`;
    }
  }

}
export interface OverviewTableData {
  partnerReportNumber: number;
  partnerName: string;
  staffCostsFlatRate: number;
  officeAndAdministrationFlatRate: number;
  travelAndAccommodationFlatRate: number;
  otherCostsOnStaffCostsFlatRate: number;
  showFlatRates: boolean;
  deductionRows: MatTableDataSource<VerificationDeductionOverviewRowDTO>;
  total: VerificationDeductionOverviewRowDTO;
}
