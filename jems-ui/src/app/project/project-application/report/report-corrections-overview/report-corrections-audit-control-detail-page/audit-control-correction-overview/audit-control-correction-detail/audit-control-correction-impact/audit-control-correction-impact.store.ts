import { UntilDestroy } from '@ngneat/until-destroy';
import { Injectable } from '@angular/core';
import {
  AuditControlCorrectionImpactDTO,
  AuditControlCorrectionImpactService,
} from '@cat/api';
import { merge, Observable, Subject } from 'rxjs';
import {
  AuditControlCorrectionDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-correction-overview/audit-control-correction-detail/audit-control-correction-detail-page.store';
import { filter, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { Log } from '@common/utils/log';

@UntilDestroy()
@Injectable({providedIn: 'root'})
export class AuditControlCorrectionImpactStore {

  canEdit$: Observable<boolean>;
  impact$: Observable<AuditControlCorrectionImpactDTO>;
  updatedImpact$ = new Subject<AuditControlCorrectionImpactDTO>();

  constructor(
    private correctionPageStore: AuditControlCorrectionDetailPageStore,
    private impactService: AuditControlCorrectionImpactService,
  ) {
    this.canEdit$ = correctionPageStore.canEdit$;
    this.impact$ = this.impact();
  }

  private impact(): Observable<AuditControlCorrectionImpactDTO> {
    const initial = this.correctionPageStore.correction$.pipe(
      map((correction: any) => correction.impact),
    );

    return merge(initial, this.updatedImpact$)
      .pipe(shareReplay(1));
  }

  updateImpact(impact: AuditControlCorrectionImpactDTO) {
    return this.correctionPageStore.correctionId$.pipe(filter(Boolean), map(Number)).pipe(
      switchMap((correctionId) =>
        this.impactService.updateImpact(0, correctionId, 0, impact)),
      tap(updatedImpact => this.updatedImpact$.next(updatedImpact)),
      tap(updatedImpact => Log.info('Update Correction Impact', this, updatedImpact)),
    );
  }
}
