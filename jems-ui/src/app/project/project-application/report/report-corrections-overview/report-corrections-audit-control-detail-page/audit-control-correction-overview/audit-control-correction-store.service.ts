import {Injectable} from '@angular/core';
import {combineLatest, Observable, of, Subject} from 'rxjs';
import {
  PageProjectAuditControlCorrectionLineDTO,
  ProjectAuditControlCorrectionDTO,
  ProjectCorrectionService,
} from '@cat/api';
import {map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {Tables} from '@common/utils/tables';
import {MatSort} from '@angular/material/sort';
import {
  ReportCorrectionsAuditControlDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/report-corrections-audit-control-detail-page.store';

@Injectable({
  providedIn: 'root'
})
export class AuditControlCorrectionStore {
  refreshCorrections$ = new Subject<void>();
  corrections$: Observable<PageProjectAuditControlCorrectionLineDTO>;
  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  correctionsUnpaged$: Observable<PageProjectAuditControlCorrectionLineDTO>;

  constructor(
    private projectAuditControlCorrectionService: ProjectCorrectionService,
    private reportCorrectionsAuditControlDetailPageStore: ReportCorrectionsAuditControlDetailPageStore,
    ) {
    this.corrections$ = this.corrections();
    this.correctionsUnpaged$ = this.correctionsUnpaged();
  }

  createEmptyCorrection(projectId: number, auditControlId: number, linking: boolean): Observable<ProjectAuditControlCorrectionDTO> {
    return this.projectAuditControlCorrectionService.createProjectAuditCorrection(Number(auditControlId), linking, projectId)
      .pipe(
        tap(() => this.refreshCorrections$.next()),
        tap(created => Log.info('Created correction:', this, created)),
      );
  }


  private corrections(): Observable<PageProjectAuditControlCorrectionLineDTO> {
    return combineLatest([
      this.reportCorrectionsAuditControlDetailPageStore.projectId$,
      this.reportCorrectionsAuditControlDetailPageStore.auditControlId$,
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith({active: 'id', direction: 'desc'}),
        map(sort => sort?.direction ? sort : {active: 'id', direction: 'desc'}),
        map(sort => `${sort.active},${sort.direction}`)
      ),
      this.refreshCorrections$.pipe(startWith(null))
    ]).pipe(
      switchMap(([projectId, auditControlId, pageIndex, pageSize, sort]) =>
        this.projectAuditControlCorrectionService.listProjectAuditCorrections(Number(auditControlId), Number(projectId), pageIndex, pageSize, sort)),
      tap(corrections => Log.info('Fetched the corrections:', this, corrections)),
    );
  }

  deleteCorrection(correctionId: number): Observable<void> {
    return combineLatest([
      this.reportCorrectionsAuditControlDetailPageStore.projectId$,
      this.reportCorrectionsAuditControlDetailPageStore.auditControlId$,
    ]).pipe(
      take(1),
      switchMap(([projectId, auditControlId]) => this.projectAuditControlCorrectionService.deleteProjectAuditCorrection(Number(auditControlId), correctionId, projectId)),
      tap(() => this.refreshCorrections$.next()),
      tap(() => Log.info('Deleted correction: ', this, correctionId))
    );
  }

  private correctionsUnpaged(): Observable<PageProjectAuditControlCorrectionLineDTO> {
    return combineLatest([
      this.reportCorrectionsAuditControlDetailPageStore.projectId$,
      this.reportCorrectionsAuditControlDetailPageStore.auditControlId$,
      this.refreshCorrections$.pipe(startWith(null))
    ]).pipe(
      switchMap(([projectId, auditControlId]) =>
          (!!projectId && !!auditControlId) ? this.projectAuditControlCorrectionService.listProjectAuditCorrections(Number(auditControlId), Number(projectId)) : of({} as PageProjectAuditControlCorrectionLineDTO)),
      tap(corrections => Log.info('Fetched the corrections unpaged:', this, corrections)),
    );
  }
}
