import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';
import {
  ReportFileManagementStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-annexes-tab/report-file-management-store';
import {
  ReportFileCategoryTypeEnum
} from '@project/project-application/report/partner-report-detail-page/partner-report-annexes-tab/report-file-category-type';

@Component({
  selector: 'jems-partner-report-annexes-tab',
  templateUrl: './partner-report-annexes-tab.component.html',
  styleUrls: ['./partner-report-annexes-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportAnnexesTabComponent implements OnInit{

  constructor(public reportFileManagementStore: ReportFileManagementStore) {
  }

  ngOnInit(): void {
    this.reportFileManagementStore.setSectionInit({type: ReportFileCategoryTypeEnum.REPORT} as CategoryInfo);
  }
}
