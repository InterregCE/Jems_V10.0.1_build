import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  AuditControlCorrectionDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-correction-overview/audit-control-correction-detail/audit-control-correction-detail-page.store';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {CorrectionAvailablePartnerDTO, ProjectAuditControlCorrectionDTO} from '@cat/api';
import {catchError, filter, finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {APIError} from '@common/models/APIError';
import {Alert} from '@common/components/forms/alert';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';

@Component({
  selector: 'jems-audit-control-correction-detail',
  templateUrl: './audit-control-correction-detail.component.html',
  styleUrls: ['./audit-control-correction-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AuditControlCorrectionDetailComponent {

  Alert = Alert;
  pendingAction$ = new BehaviorSubject(false);
  error$ = new BehaviorSubject<APIError | null>(null);

  data$: Observable<{
    projectId: number;
    auditControlId: number;
    correction: ProjectAuditControlCorrectionDTO;
    canEdit: boolean;
    canClose: boolean;
    correctionPartnerData: CorrectionAvailablePartnerDTO[];
  }>;

  constructor(
    private router: RoutingService,
    private auditControlCorrectionDetailPageStore: AuditControlCorrectionDetailPageStore,
    private dialog: MatDialog,
  ) {
    this.data$ = combineLatest([
      this.auditControlCorrectionDetailPageStore.projectId$,
      this.auditControlCorrectionDetailPageStore.auditControlId$,
      this.auditControlCorrectionDetailPageStore.correction$,
      this.auditControlCorrectionDetailPageStore.canEdit$,
      this.auditControlCorrectionDetailPageStore.canClose$,
      this.auditControlCorrectionDetailPageStore.correctionPartnerData$,
    ]).pipe(
      map(([projectId, auditControlId, correction, canEdit, canClose, correctionPartnerData]) =>
        ({
          projectId,
          auditControlId,
          correction,
          canEdit,
          canClose,
          correctionPartnerData
        })
      )
    );
  }

  redirectToCorrectionsOverview(projectId: number, auditControlId: number): void {
    this.router.navigate([`/app/project/detail/${projectId}/corrections/auditControl/${auditControlId}`]);
  }

  closeCorrection(projectId: number, auditControlId: number, correction: ProjectAuditControlCorrectionDTO) {
    this.pendingAction$.next(true);

    Forms.confirm(
      this.dialog, {
        title: `AC${correction.auditControlNumber}.${correction.orderNr}`,
        message: {i18nKey: 'project.application.reporting.corrections.close.correction.dialog'}
      }
    ).pipe(
      take(1),
      filter(answer => !!answer),
      switchMap(() => this.auditControlCorrectionDetailPageStore.closeCorrection(projectId, auditControlId, correction.id)),
      tap(() => this.redirectToCorrectionsOverview(projectId, auditControlId)),
      catchError(error => this.showErrorMessage(error.error$)),
      finalize(() => this.pendingAction$.next(false)),
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
