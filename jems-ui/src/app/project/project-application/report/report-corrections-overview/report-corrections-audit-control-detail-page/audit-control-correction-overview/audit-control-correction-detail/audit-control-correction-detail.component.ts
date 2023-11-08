import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  AuditControlCorrectionDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-correction-overview/audit-control-correction-detail/audit-control-correction-detail-page.store';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {CorrectionAvailablePartnerDTO, ProjectAuditControlCorrectionExtendedDTO} from '@cat/api';
import {catchError, finalize, map, take, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {APIError} from '@common/models/APIError';
import {Alert} from '@common/components/forms/alert';

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
    correction: ProjectAuditControlCorrectionExtendedDTO;
    canEdit: boolean;
    canClose: boolean;
    correctionPartnerData: CorrectionAvailablePartnerDTO[];
  }>;

  constructor(
    private router: RoutingService,
    private auditControlCorrectionDetailPageStore: AuditControlCorrectionDetailPageStore
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

  closeCorrection(projectId: number, auditControlId: number, correctionId: number) {
    this.pendingAction$.next(true);
    this.auditControlCorrectionDetailPageStore.closeCorrection(projectId, auditControlId, correctionId)
      .pipe(
        take(1),
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
