import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {ProjectPartnerReportSummaryDTO, ProjectPartnerSummaryDTO, UserRoleDTO} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {catchError, distinctUntilChanged, filter, finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {
  ProjectApplicationFormSidenavService
} from '../containers/project-application-form-page/services/project-application-form-sidenav.service';
import {RoutingService} from '@common/services/routing.service';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {APIError} from '@common/models/APIError';
import {Alert} from '@common/components/forms/alert';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {TranslateService} from '@ngx-translate/core';
import {
  MultiLanguageGlobalService
} from '@common/components/forms/multi-language-container/multi-language-global.service';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {
  PartnerControlReportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {ColumnWidth} from '@common/components/table/model/column-width';
import StatusEnum = ProjectPartnerReportSummaryDTO.StatusEnum;

@Component({
  selector: 'jems-contract-monitoring',
  templateUrl: './partner-report.component.html',
  styleUrls: ['./partner-report.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
@UntilDestroy()
export class PartnerReportComponent {
  PermissionsEnum = PermissionsEnum;
  ProjectPartnerReportSummaryDTO = ProjectPartnerReportSummaryDTO;
  successfulDeletionMessage: boolean;

  @ViewChild('numberingCell', {static: true})
  numberingCell: TemplateRef<any>;

  @ViewChild('statusCell', {static: true})
  statusCell: TemplateRef<any>;

  @ViewChild('periodCell', {static: true})
  periodCell: TemplateRef<any>;

  @ViewChild('actionCell', {static: true})
  actionCell: TemplateRef<any>;

  @ViewChild('deleteCell', {static: true})
  deleteCell: TemplateRef<any>;

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  partnerId = this.activatedRoute?.snapshot?.params?.partnerId;
  tableConfiguration: TableConfiguration;
  actionPending = false;
  controlActionMap = new Map<number, BehaviorSubject<boolean>>();
  error$ = new BehaviorSubject<APIError | null>(null);
  Alert = Alert;
  isStartControlButtonDisabled = false;
  deletableReportId: number | null = null;

  data$: Observable<{
    totalElements: number;
    partnerReports: ProjectPartnerReportSummaryDTO[];
    partner: ProjectPartnerSummaryDTO;
    canUserViewControlReports: boolean;
    canUserEditControlReports: boolean;
    canEditReport: boolean;
  }>;

  readonly isControlButtonVisible$: Observable<boolean> = combineLatest([
    this.pageStore.institutionUserCanViewControlReports$,
    this.pageStore.institutionUserCanEditControlReports$,
    this.pageStore.userCanViewReport$,
    this.pageStore.userCanEditReport$
  ]).pipe(
    map(([
           canViewFromInstitution,
           canEditFromInstitution,
           canViewFromProjectPrivileges,
           canEditFromProjectPrivileges]) => (canViewFromInstitution || canEditFromInstitution) || canViewFromProjectPrivileges || canEditFromProjectPrivileges
    )
  );

  constructor(
      public pageStore: PartnerReportPageStore,
      public store: PartnerControlReportStore,
      private activatedRoute: ActivatedRoute,
      private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
      private router: RoutingService,
      private partnerReportDetail: PartnerReportDetailPageStore,
      private translateService: TranslateService,
      private multiLanguageGlobalService: MultiLanguageGlobalService,
      private dialog: MatDialog,
      private changeDetectorRef: ChangeDetectorRef
  ) {
    this.data$ = combineLatest([
      this.pageStore.partnerReports$,
      this.pageStore.partnerSummary$,
      this.multiLanguageGlobalService.activeSystemLanguage$,
      this.pageStore.institutionUserCanViewControlReports$,
      this.pageStore.institutionUserCanEditControlReports$,
      this.pageStore.userCanEditReport$,
    ]).pipe(
      map(([partnerReports, partner, systemLanguage, canUserViewControlReports, canUserEditControlReports, canEditReport]) => {
        return {
            totalElements: partnerReports.totalElements,
            partnerReports: partnerReports.content,
            partner,
            canUserViewControlReports,
            canUserEditControlReports,
            canEditReport,
          };
        }
      ),
      tap(data => {
        data.partnerReports.forEach((report) => {
          this.controlActionMap.set(report.id, new BehaviorSubject<boolean>(false));
          if (report.status === StatusEnum.Draft && report.reportNumber === data.totalElements) {
            this.deletableReportId = report.reportNumber;
          }
          const someDraft = data.partnerReports
            .find((x) => x.status === ProjectPartnerReportSummaryDTO.StatusEnum.Draft);
          this.refreshColumns(data.canEditReport, !!someDraft);
        });
      })
    );

    combineLatest([
      this.pageStore.institutionUserCanViewControlReports$,
      this.pageStore.institutionUserCanEditControlReports$,
    ]).pipe(
      map(([canViewFromInstitution, canEditFromInstitution]) => {
          if (canViewFromInstitution && !canEditFromInstitution) {
            this.isStartControlButtonDisabled = true;
          }
          this.isStartControlButtonDisabled = !canEditFromInstitution;
        }
      )
    ).subscribe();
  }

  private refreshColumns(canEditReport: boolean, thereIsDraft: boolean) {
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
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.statusCell,
        },
        {
          displayedColumn: 'project.application.partner.reports.table.version',
          elementProperty: 'linkedFormVersion'
        },
        {
          displayedColumn: 'project.application.partner.report.reporting.period',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.periodCell,
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
          displayedColumn: 'project.application.partner.reports.table.control',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.actionCell,
          clickable: false
        },
        ...((canEditReport && thereIsDraft) ? [{
          displayedColumn: 'common.delete.entry',
          customCellTemplate: this.deleteCell,
          columnWidth: ColumnWidth.IdColumn,
          clickable: false
        }] : []),
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

  createControlReportForPartnerReport(partnerReport: ProjectPartnerReportSummaryDTO): void {
    if (this.isStartControlButtonDisabled) {
      return;
    }

    this.getPendingActionStatus(partnerReport.id).next(true);
    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.partner.report.confirm.control.start.header',
        message: {
          i18nKey: 'project.application.partner.report.confirm.control.start.message'
        },
      }).pipe(
      take(1),
      tap((answer) => {
        if (answer) {
          this.changeStatusOfReport(partnerReport);
        } else {
          this.getPendingActionStatus(partnerReport.id).next(false);
        }
      })).subscribe();
  }

  getPendingActionStatus(reportId: number): any {
    return this.controlActionMap.get(reportId);
  }

  private initializeTableConfiguration(partnerId: number): void {
    this.tableConfiguration.routerLink = `/app/project/detail/${this.projectId}/reporting/${partnerId}/reports/`;
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    },4000);
    return of(null);
  }

  private showSuccessMessageAfterDeletion(): void {
    this.successfulDeletionMessage = true;
    setTimeout(() => {
      this.successfulDeletionMessage = false;
      this.changeDetectorRef.markForCheck();
    }, 4000);
  }

  private changeStatusOfReport(partnerReport: ProjectPartnerReportSummaryDTO): void {
    this.partnerReportDetail.startControlOnPartnerReport(this.partnerId, partnerReport.id)
      .pipe(
        take(1),
        tap((report) => this.router.navigate([`../${partnerReport.id}/controlReport/identificationTab`], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'})),
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.getPendingActionStatus(partnerReport.id).next(false))
      ).subscribe();
  }

  delete(partnerReport: ProjectPartnerReportSummaryDTO): void {
    Forms.confirm(
      this.dialog, {
        title: 'project.application.partner.report.confirm.deletion.header',
        message: {i18nKey: 'project.application.partner.report.confirm.deletion.message', i18nArguments: {id: partnerReport.reportNumber.toString()}}
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.pageStore.deletePartnerReport(partnerReport.id)),
        tap(() => this.showSuccessMessageAfterDeletion()),
        catchError((error) => this.showErrorMessage(error.error)),
      ).subscribe();
  }

}
