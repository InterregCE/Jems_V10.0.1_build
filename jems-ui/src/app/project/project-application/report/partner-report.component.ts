import {ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {ProjectPartnerReportSummaryDTO, ProjectPartnerSummaryDTO} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {TranslateService} from '@ngx-translate/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {
  ProjectPartnerReportPageStore
} from '@project/project-application/report/project-partner-report-page-store.service';
import {map, take, tap} from 'rxjs/operators';
import {
  ProjectApplicationFormSidenavService
} from '../containers/project-application-form-page/services/project-application-form-sidenav.service';
import {RoutingService} from '@common/services/routing.service';
import {ColumnType} from '@common/components/table/model/column-type.enum';

@Component({
  selector: 'jems-contract-monitoring',
  templateUrl: './partner-report.component.html',
  styleUrls: ['./partner-report.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportComponent implements OnInit {

  @ViewChild('numberingCell', {static: true})
  numberingCell: TemplateRef<any>;

  @Input()
  pageIndex: number;

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  partnerId = this.activatedRoute?.snapshot?.params?.partnerId;

  tableConfiguration: TableConfiguration;
  data$: Observable<{
    partnerReports: ProjectPartnerReportSummaryDTO[];
    partner: ProjectPartnerSummaryDTO;
  }>;

  constructor(public projectPartnerStore: ProjectPartnerStore,
              public projectPartnerReportPageStore: ProjectPartnerReportPageStore,
              private translateService: TranslateService,
              private projectVersionStore: ProjectVersionStore,
              private activatedRoute: ActivatedRoute,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private router: RoutingService) {
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
      routerLink: `/app/project/detail/${this.projectId}/reporting/${this.partnerId}/reports/`,
      isTableClickable: true,
      sortable: true,
      columns: [
        {
          displayedColumn: 'project.application.partner.reports.table.id',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.numberingCell,
        },
        {
          displayedColumn: 'project.application.partner.reports.table.status',
          elementProperty: 'status'
        },
        {
          displayedColumn: 'project.application.partner.reports.table.version',
          elementProperty: 'linkedFormVersion'
        },
        {
          displayedColumn: 'project.application.partner.reports.table.created.at',
          elementProperty: 'createdAt',
          columnType: ColumnType.DateColumn
        },
        {
          displayedColumn: 'project.application.partner.reports.table.first.submission',
          elementProperty: 'firstSubmission',
          columnType: ColumnType.DateColumn
        },
        {
          displayedColumn: 'project.application.partner.reports.table.latest.resubmission',
          elementProperty: 'firstSubmission',
          columnType: ColumnType.DateColumn
        }
      ]
    });
  }

  getPartnerTranslationString(partner: ProjectPartnerSummaryDTO): string {
    return `${partner.sortNumber || ''} ${partner.abbreviation}`;
  }

  createPartnerReport(): void {
    this.projectPartnerReportPageStore.createPartnerReport(this.partnerId)
      .pipe(
        take(1),
        tap((report) => this.router.navigate([`../${report.id}/identification`], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'})),
      ).subscribe();
  }
}
