import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  JemsFileMetadataDTO,
  ProjectReportResultPrincipleDTO,
  ProjectReportResultPrincipleService,
  ProjectReportService,
  UpdateProjectReportResultPrincipleDTO
} from '@cat/api';
import {RoutingService} from '@common/services/routing.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {DownloadService} from '@common/services/download.service';

@Injectable({providedIn: 'root'})
export class ProjectReportResultsAndPrinciplesTabStore {
  savedResultsAndPrinciples$ = new Subject<ProjectReportResultPrincipleDTO>();
  resultsAndPrinciples$: Observable<ProjectReportResultPrincipleDTO>;

  constructor(private routingService: RoutingService,
              private projectStore: ProjectStore,
              private projectReportDetailPageStore: ProjectReportDetailPageStore,
              private projectReportService: ProjectReportService,
              private projectReportResultPrincipleService: ProjectReportResultPrincipleService,
              private readonly downloadService: DownloadService,
  ) {
    this.resultsAndPrinciples$ = this.resultsAndPrinciples();
  }

  updateResultsAndPrinciples(resultsAndPrinciples: UpdateProjectReportResultPrincipleDTO) {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ])
      .pipe(
        switchMap(([projectId, reportId]) =>
          this.projectReportResultPrincipleService.updateResultAndPrinciple(projectId, reportId, resultsAndPrinciples)),
        tap(saved => Log.info('Saved results and principles', saved)),
        tap(data => this.savedResultsAndPrinciples$.next(data))
      );
  }

  private resultsAndPrinciples(): Observable<ProjectReportResultPrincipleDTO> {
    const initialData$ = combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$
    ])
      .pipe(
        switchMap(([projectId, reportId]) => this.projectReportResultPrincipleService.getResultAndPrinciple(projectId, reportId)),
      );

    return merge(initialData$, this.savedResultsAndPrinciples$);
  }

  downloadFile(resultNumber: number): Observable<any> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ])
      .pipe(
        take(1),
        switchMap(([projectId, reportId]) =>
          this.downloadService.download(
            `api/project/report/byProjectId/${projectId}/byReportId/${reportId}/resultPrinciple/byProjectResult/${resultNumber}/attachment`,
            `project-result-$resultNumber-attachment`
          )
        ));
  }

  uploadFile(file: File, resultNumber: number): Observable<JemsFileMetadataDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ])
      .pipe(
        take(1),
        switchMap(([projectId, reportId]) => {
          return this.projectReportResultPrincipleService.uploadAttachmentToResultForm(file, projectId, reportId, resultNumber);
        }),
      );
  }

  deleteFile(resultNumber: number): Observable<any> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ])
      .pipe(
        take(1),
        switchMap(([projectId, reportId]) => {
          return this.projectReportResultPrincipleService.deleteAttachmentFromResult(projectId, reportId, resultNumber);
        })
      );
  }
}



