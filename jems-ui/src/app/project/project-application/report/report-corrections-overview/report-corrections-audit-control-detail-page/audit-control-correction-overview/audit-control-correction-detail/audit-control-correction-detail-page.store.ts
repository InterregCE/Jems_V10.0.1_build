import {Injectable} from '@angular/core';
import {combineLatest, Observable, of} from 'rxjs';
import {RoutingService} from '@common/services/routing.service';
import {UntilDestroy} from '@ngneat/until-destroy';
import {
  ProjectAuditControlCorrectionExtendedDTO,
  ProjectCorrectionService
} from '@cat/api';
import {catchError, shareReplay, switchMap, tap} from 'rxjs/operators';
import {ProjectPaths} from '@project/common/project-util';
import {Log} from '@common/utils/log';
import {
  ReportCorrectionsAuditControlDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/report-corrections-audit-control-detail-page.store';

@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class AuditControlCorrectionDetailPageStore {

  AUDIT_CONTROL_CORRECTION_PATH = 'correction/';

  projectId$: Observable<number>;
  auditControlId$: Observable<string | number | null>;
  correctionId$: Observable<string | number | null>;
  correction$: Observable<ProjectAuditControlCorrectionExtendedDTO>;

  constructor(
    private routingService: RoutingService,
    private reportCorrectionsAuditControlDetailPageStore: ReportCorrectionsAuditControlDetailPageStore,
    private projectAuditControlCorrectionService: ProjectCorrectionService,
  ) {
    this.projectId$ = this.reportCorrectionsAuditControlDetailPageStore.projectId$;
    this.auditControlId$ = this.reportCorrectionsAuditControlDetailPageStore.auditControlId$;
    this.correctionId$ = this.correctionId();
    this.correction$ = this.correction();
  }

  private correctionId(): Observable<string | number | null> {
    return this.routingService.routeParameterChanges(this.AUDIT_CONTROL_CORRECTION_PATH, 'correctionId');
  }

  private correction(): Observable<ProjectAuditControlCorrectionExtendedDTO> {
    const initialCorrection = combineLatest([
      this.reportCorrectionsAuditControlDetailPageStore.auditControlId$,
      this.reportCorrectionsAuditControlDetailPageStore.projectId$,
      this.correctionId$
    ]).pipe(
      switchMap(([auditControlId, projectId, correctionId]) =>
        correctionId
          ? this.projectAuditControlCorrectionService.getProjectAuditCorrection(Number(auditControlId), Number(correctionId), projectId).pipe(
            catchError(() => {
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId]);
              return of({} as ProjectAuditControlCorrectionExtendedDTO);
            })
          )
          : of({} as ProjectAuditControlCorrectionExtendedDTO)
      ),
      tap((correction) => Log.info('Fetched correction', this, correction)),
      shareReplay(1),
    );

    return initialCorrection;
  }
}
