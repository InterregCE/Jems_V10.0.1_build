import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {ProjectReportPageStore} from '@project/project-application/report/project-report/project-report-page-store.service';
import {
  ProgrammeTypologyOfErrorsService,
  ProjectReportVerificationExpenditureLineDTO,
  ProjectReportVerificationExpenditureLineUpdateDTO,
  ProjectReportVerificationExpenditureVerificationService,
  ProjectReportVerificationRiskBasedDTO,
  TypologyErrorsDTO
} from '@cat/api';
import {map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {DownloadService} from '@common/services/download.service';

@Injectable({providedIn: 'root'})
export class ProjectVerificationReportExpenditureStore {

  projectId$: Observable<number>;
  reportId$: Observable<number>;
  isEditable$: Observable<boolean>;
  typologyOfErrors$: Observable<TypologyErrorsDTO[]>;

  riskBasedVerification$: Observable<ProjectReportVerificationRiskBasedDTO>;
  private savedRiskBasedVerification$ = new Subject<ProjectReportVerificationRiskBasedDTO>();
  aggregatedExpenditures$: Observable<ProjectReportVerificationExpenditureLineDTO[]>;

  constructor(
    private projectStore: ProjectStore,
    private projectReportStore: ProjectReportDetailPageStore,
    private projectReportPageStore: ProjectReportPageStore,
    private expenditureVerificationService: ProjectReportVerificationExpenditureVerificationService,
    private typologyOfErrorService: ProgrammeTypologyOfErrorsService,
    private downloadService: DownloadService,
  ) {
    this.projectId$ = this.projectStore.projectId$;
    this.reportId$ = this.projectReportStore.projectReportId$.pipe(map(Number));
    this.isEditable$ = this.projectReportPageStore.userCanEditVerification$;
    this.riskBasedVerification$ = this.riskBasedVerification();
    this.aggregatedExpenditures$ = this.aggregatedExpenditures();
    this.typologyOfErrors$ = this.typologyOfErrorService.getTypologyErrors();
  }

  private riskBasedVerification(): Observable<ProjectReportVerificationRiskBasedDTO> {
    const riskBasedVerification = combineLatest([
      this.projectId$,
      this.reportId$,
    ]).pipe(
      switchMap(([projectId, reportId]) =>
        this.expenditureVerificationService.getProjectReportExpenditureVerificationRiskBased(projectId, reportId))
    );

    return merge(riskBasedVerification, this.savedRiskBasedVerification$)
      .pipe(
        shareReplay(1),
      );
  }

  updateRiskBasedVerification(updateRiskBasedVerificationDTO: ProjectReportVerificationRiskBasedDTO) {
    return combineLatest([
      this.projectId$,
      this.reportId$
    ]).pipe(
      switchMap(([projectId, reportId]) =>
        this.expenditureVerificationService.updateProjectReportExpenditureVerificationRiskBased(projectId, reportId, updateRiskBasedVerificationDTO)),
      tap(data => this.savedRiskBasedVerification$.next(data))
    );
  }

  private aggregatedExpenditures(): Observable<ProjectReportVerificationExpenditureLineDTO[]> {
    return combineLatest([
      this.projectId$,
      this.reportId$,
    ]).pipe(
      switchMap(([projectId, reportId]) =>
        this.expenditureVerificationService.getProjectReportExpenditureVerification(projectId, reportId)),
    );
  }

  updateExpenditureVerification(updateExpenditureVerificationDTO: ProjectReportVerificationExpenditureLineUpdateDTO[]): Observable<ProjectReportVerificationExpenditureLineDTO[]> {
    return combineLatest([
      this.projectId$,
      this.reportId$,
    ]).pipe(
      switchMap(([projectId, reportId]) =>
        this.expenditureVerificationService.updateProjectReportExpendituresVerification(projectId, reportId, updateExpenditureVerificationDTO)),
    );
  }


  downloadFile(partnerId: number, fileId: number): Observable<any> {
    return this.downloadService.download(`/api/project/report/partner/byPartnerId/${partnerId}/${fileId}`, 'expenditure-verification');
  }
}
