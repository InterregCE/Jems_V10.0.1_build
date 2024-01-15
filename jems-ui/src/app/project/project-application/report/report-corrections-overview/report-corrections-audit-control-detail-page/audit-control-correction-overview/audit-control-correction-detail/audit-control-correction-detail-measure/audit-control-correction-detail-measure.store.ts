import {UntilDestroy} from '@ngneat/until-destroy';
import {Injectable} from '@angular/core';
import {
  CorrectionAvailablePartnerDTO, ProjectAuditControlCorrectionDTO,
  ProjectCorrectionProgrammeMeasureDTO,
  ProjectCorrectionProgrammeMeasureService,
  ProjectCorrectionProgrammeMeasureUpdateDTO
} from '@cat/api';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  AuditControlCorrectionDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-correction-overview/audit-control-correction-detail/audit-control-correction-detail-page.store';
import {filter, map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';


@UntilDestroy()
@Injectable({providedIn: 'root'})
export class AuditControlCorrectionDetailMeasureStore {

  canEdit$: Observable<boolean>;
  correctionPartnerData$: Observable<CorrectionAvailablePartnerDTO[]>;
  correctionIdentity$: Observable<ProjectAuditControlCorrectionDTO>;
  programmeMeasure$: Observable<ProjectCorrectionProgrammeMeasureDTO>;
  updatedProgrammeMeasure$ = new Subject<ProjectCorrectionProgrammeMeasureDTO>();

  constructor(
    private programmeMeasureService: ProjectCorrectionProgrammeMeasureService,
    private correctionPageStore: AuditControlCorrectionDetailPageStore,
  ) {
    this.canEdit$ = correctionPageStore.canEdit$;
    this.correctionPartnerData$ = correctionPageStore.correctionPartnerData$;
    this.correctionIdentity$ = correctionPageStore.correction$;
    this.programmeMeasure$ = this.programmeMeasure();
  }

  private programmeMeasure(): Observable<ProjectCorrectionProgrammeMeasureDTO> {
    const initial = combineLatest([
      this.correctionPageStore.projectId$,
      this.correctionPageStore.auditControlId$.pipe(filter(Boolean), map(Number)),
      this.correctionPageStore.correctionId$.pipe(filter(Boolean), map(Number)),
    ]).pipe(
      switchMap(([projectId, auditControlId, correctionId]) =>
        this.programmeMeasureService.getProgrammeMeasure(auditControlId, correctionId, projectId)),
      tap(programmeMeasure => Log.info('Fetched Correction Programme Measure', this, programmeMeasure))
    );

    return merge(initial, this.updatedProgrammeMeasure$)
      .pipe(shareReplay(1));
  }

  updateProgrammeMeasure(programmeMeasure: ProjectCorrectionProgrammeMeasureUpdateDTO) {
    return combineLatest([
      this.correctionPageStore.projectId$,
      this.correctionPageStore.auditControlId$.pipe(filter(Boolean), map(Number)),
      this.correctionPageStore.correctionId$.pipe(filter(Boolean), map(Number)),
    ]).pipe(
      switchMap(([projectId, auditControlId, correctionId]) =>
        this.programmeMeasureService.updateProgrammeMeasure(auditControlId, correctionId, projectId, programmeMeasure)),
      tap(updatedProgrammeMeasure => this.updatedProgrammeMeasure$.next(updatedProgrammeMeasure)),
      tap(updateProgrammeMeasure => Log.info('Update Correction Programme Measure', this, updateProgrammeMeasure)),
    );
  }
}
