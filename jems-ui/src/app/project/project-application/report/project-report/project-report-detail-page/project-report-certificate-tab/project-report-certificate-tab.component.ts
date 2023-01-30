import {Component} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {ProjectReportDetailPageStore} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {catchError, filter, map, switchMap, take, tap} from 'rxjs/operators';
import {PagePartnerReportCertificateDTO, PartnerReportCertificateDTO} from '@cat/api';
import {
  ProjectReportCertificateTabStore,
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-certificate-tab/project-report-certificate-tab-store.service';
import {APIError} from '@common/models/APIError';
import {Alert} from '@common/components/forms/alert';
import {MatTableDataSource} from '@angular/material/table';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectReportPageStore} from '@project/project-application/report/project-report/project-report-page-store.service';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {MatCheckbox} from '@angular/material/checkbox/checkbox';

@Component({
  selector: 'jems-project-report-certificate-tab',
  templateUrl: './project-report-certificate-tab.component.html',
  styleUrls: ['./project-report-certificate-tab.component.scss'],
  providers: [FormService],
})
export class ProjectReportCertificateTabComponent {

  Alert = Alert;
  PartnerRole = PartnerReportCertificateDTO.PartnerRoleEnum;
  dataSource: MatTableDataSource<PartnerReportCertificateDTO> = new MatTableDataSource([]);
  displayedColumns: string[] = ['checked', 'partner', 'partnerReport', 'date', 'projectReport', 'amount'];

  data$: Observable<{
    projectId: number;
    projectReportId: number;
    reportEditable: boolean;
    partnerReportCertificates: PagePartnerReportCertificateDTO;
  }>;
  error$ = new BehaviorSubject<APIError | null>(null);

  constructor(
    public pageStore: ProjectReportCertificateTabStore,
    private projectStore: ProjectStore,
    private projectReportPageStore: ProjectReportPageStore,
    private projectReportDetailPageStore: ProjectReportDetailPageStore,
    private confirmDialog: MatDialog,
  ) {
    this.data$ = combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
      this.projectReportDetailPageStore.reportEditable$,
      this.pageStore.projectReportCertificates$,
    ]).pipe(
      map(([projectId, projectReportId, reportEditable, partnerReportCertificates]) => ({
        projectId,
        projectReportId,
        reportEditable,
        partnerReportCertificates
      })),
      tap(data => this.dataSource.data = data.partnerReportCertificates.content),
    );
  }

  certificateChanged(projectId: number, projectReportId: number, partnerReportId: number, checked: boolean, event: MatCheckboxChange): void {
    event.source.checked = checked;
    if (checked) {
      this.deselectCertificate(projectId, projectReportId, partnerReportId, event.source);
    } else {
      this.selectCertificate(projectId, projectReportId, partnerReportId, event.source);
    }
  }

  private selectCertificate(projectId: number, projectReportId: number, partnerReportId: number, checkbox: MatCheckbox) {
    Forms.confirm(
      this.confirmDialog,
      {
        title: 'project.application.project.report.certificates.include.title',
        message: {
          i18nKey: 'project.application.project.report.certificates.include.description'
        },
      }).pipe(
      take(1),
      filter(Boolean),
      switchMap(() => this.pageStore.selectCertificate(projectId, projectReportId, partnerReportId)
        .pipe(
          tap(() => checkbox.checked = true),
          catchError((error) => this.showErrorMessage(error.error))
        ))
    ).subscribe();
  }

  private deselectCertificate(projectId: number, projectReportId: number, partnerReportId: number, checkbox: MatCheckbox) {
    Forms.confirm(
      this.confirmDialog,
      {
        title: 'project.application.project.report.certificates.exclude.title',
        message: {
          i18nKey: 'project.application.project.report.certificates.exclude.description'
        },
      }).pipe(
      take(1),
      filter(Boolean),
      switchMap(() => this.pageStore.deselectCertificate(projectId, projectReportId, partnerReportId)
        .pipe(
          tap(() => checkbox.checked = false),
          catchError((error) => this.showErrorMessage(error.error))
        ))
    ).subscribe();
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      if (this.error$.value?.id === error.id) {
        this.error$.next(null);
      }
    }, 10000);
    return of(null);
  }

}
