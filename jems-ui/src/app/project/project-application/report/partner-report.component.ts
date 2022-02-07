import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {ProjectPartnerReportDTO, ProjectPartnerSummaryDTO} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {TranslateService} from '@ngx-translate/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectPartnerReportPageStore} from '@project/project-application/report/project-partner-report-page-store.service';
import {map} from 'rxjs/operators';

@Component({
  selector: 'jems-contract-monitoring',
  templateUrl: './partner-report.component.html',
  styleUrls: ['./partner-report.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportComponent implements OnInit {
  tableConfiguration: TableConfiguration;
  data$: Observable<{
    partnerReports: ProjectPartnerReportDTO[];
    partner: ProjectPartnerSummaryDTO;
  }>;

  constructor(public projectPartnerStore: ProjectPartnerStore,
              private translateService: TranslateService,
              private projectVersionStore: ProjectVersionStore,
              private activatedRoute: ActivatedRoute,
              private projectStore: ProjectStore,
              private projectPartnerReportPageStore: ProjectPartnerReportPageStore) {
    this.data$ = combineLatest([
      this.projectPartnerReportPageStore.partnerReports$,
      this.projectPartnerReportPageStore.partnerReportSummary$,
    ]).pipe(
      map(([partnerReports, partner]) => ({
        partnerReports,
        partner,
      }))
    );
  }

  ngOnInit(): void {
    this.tableConfiguration = new TableConfiguration({
      isTableClickable: false,
      sortable: false,
      columns: [
        {
          displayedColumn: 'project.application.partner.reports.table.id',
          elementProperty: 'title'
        },
        {
          displayedColumn: 'project.application.partner.reports.table.status',
          elementProperty: 'reportStatus'
        },
        {
          displayedColumn: 'project.application.partner.reports.table.version',
          elementProperty: 'version'
        }
      ]
    });
  }

  getPartnerTranslationString(partner: ProjectPartnerSummaryDTO): string {
    return `${partner.sortNumber || ''} ${partner.abbreviation}`;
  }
}
