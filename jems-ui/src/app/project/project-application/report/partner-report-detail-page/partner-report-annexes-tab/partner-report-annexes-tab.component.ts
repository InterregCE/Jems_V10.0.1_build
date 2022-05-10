import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';
import {Observable} from 'rxjs';
import {I18nMessage} from '@common/models/I18nMessage';
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

  canReadFiles$: Observable<boolean>;
  selectedCategoryPath$: Observable<I18nMessage[]>;

  constructor(public reportFileManagementStore: ReportFileManagementStore) {
    this.canReadFiles$ = reportFileManagementStore.canReadFiles$;
    this.selectedCategoryPath$ = reportFileManagementStore.selectedCategoryPath$;
  }

  ngOnInit(): void {
    this.reportFileManagementStore.setSection({type: ReportFileCategoryTypeEnum.REPORT} as CategoryInfo);
  }
}
