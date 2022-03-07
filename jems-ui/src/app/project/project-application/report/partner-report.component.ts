import {AfterViewInit, ChangeDetectionStrategy, Component, TemplateRef, ViewChild} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {ProjectPartnerReportSummaryDTO, ProjectPartnerSummaryDTO, UserRoleDTO} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {catchError, distinctUntilChanged, filter, finalize, map, take, tap} from 'rxjs/operators';
import {ProjectApplicationFormSidenavService} from '../containers/project-application-form-page/services/project-application-form-sidenav.service';
import {RoutingService} from '@common/services/routing.service';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {APIError} from '@common/models/APIError';
import { Alert } from '@common/components/forms/alert';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'jems-contract-monitoring',
  templateUrl: './partner-report.component.html',
  styleUrls: ['./partner-report.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
@UntilDestroy()
export class PartnerReportComponent implements AfterViewInit {
  PermissionsEnum = PermissionsEnum;

  @ViewChild('numberingCell', {static: true})
  numberingCell: TemplateRef<any>;

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  tableConfiguration: TableConfiguration;
  actionPending = false;
  error$ = new BehaviorSubject<APIError | null>(null);
  Alert = Alert;

  data$: Observable<{
    partnerReports: ProjectPartnerReportSummaryDTO[];
    partner: ProjectPartnerSummaryDTO;
  }>;

  constructor(public pageStore: PartnerReportPageStore,
              private activatedRoute: ActivatedRoute,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private router: RoutingService) {
    this.data$ = combineLatest([
      this.pageStore.partnerReports$,
      this.pageStore.partnerSummary$,
    ]).pipe(
      map(([partnerReports, partner]) => ({
        partnerReports,
        partner,
      }))
    );
  }

  ngAfterViewInit(): void {
    this.tableConfiguration = new TableConfiguration({
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

    this.pageStore.partnerId$
      .pipe(
        distinctUntilChanged(),
        filter(partnerId => !!partnerId),
        tap(partnerId => this.initializeTableConfiguration(partnerId as any)),
        untilDestroyed(this)
      ).subscribe();
  }

  getPartnerTranslationString(partner: ProjectPartnerSummaryDTO): string {
    return `${partner.sortNumber || ''} ${partner.abbreviation}`;
  }

  createPartnerReport(): void {
    this.actionPending = true;
    this.pageStore.createPartnerReport()
      .pipe(
        take(1),
        tap((report) => this.router.navigate([`../${report.id}/identification`], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'})),
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.actionPending = false)
      ).subscribe();
  }

  private initializeTableConfiguration(partnerId: number): void {
    this.tableConfiguration.routerLink = `/app/project/detail/${this.projectId}/reporting/${partnerId}/reports/`;
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    },         4000);
    return of(null);
  }
}
