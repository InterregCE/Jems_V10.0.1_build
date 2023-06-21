import {Injectable} from '@angular/core';
import {
  ProjectReportAnnexesService,
  JemsFileMetadataDTO,
  ProjectReportWorkPackageDTO,
  ProjectReportWorkPlanService,
  UpdateProjectReportWorkPackageDTO
} from '@cat/api';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {RoutingService} from '@common/services/routing.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {DownloadService} from '@common/services/download.service';
import {switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable({providedIn: 'root'})
export class ProjectReportWorkPlanTabStore {
  workPackages$: Observable<ProjectReportWorkPackageDTO[]>;

  savedWorkPackages$ = new Subject<ProjectReportWorkPackageDTO[]>();

  constructor(
    private readonly routingService: RoutingService,
    private readonly projectStore: ProjectStore,
    private readonly projectReportDetailPageStore: ProjectReportDetailPageStore,
    private readonly projectReportWorkPlanService: ProjectReportWorkPlanService,
    private readonly projectReportAnnexesService: ProjectReportAnnexesService,
    private readonly downloadService: DownloadService,
  ) {
    this.workPackages$ = this.workPackages();
  }

  private workPackages() {
    const initialData$ = combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$
    ]).pipe(
      switchMap(([projectId, reportId]) => this.projectReportWorkPlanService.getWorkPlan(projectId, reportId)),
      tap(workPlan => Log.info('Fetched work plan', workPlan))
    );

    return merge(initialData$, this.savedWorkPackages$);
  }

  updateWorkPlan(workPackages: UpdateProjectReportWorkPackageDTO[]) {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ]).pipe(
      switchMap(([projectId, reportId]) => this.projectReportWorkPlanService.updateWorkPlan(projectId, reportId, workPackages)),
      tap(saved => Log.info('Saved work plan', saved)),
      tap(data => this.savedWorkPackages$.next(data))
    );
  }

  uploadActivityFile(file: File, workPackageId: number, activityId: number): Observable<JemsFileMetadataDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ]).pipe(
      take(1),
      switchMap(([projectId, reportId]) => this.projectReportWorkPlanService.uploadFileToActivityForm(
        file, activityId, projectId, reportId, workPackageId)
      ));
  }

  uploadDeliverableFile(file: File, workPackageId: number, activityId: number, deliverableId: number): Observable<JemsFileMetadataDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ]).pipe(
      take(1),
      switchMap(([projectId, reportId]) => this.projectReportWorkPlanService.uploadFileToDeliverableForm(
        file, activityId, deliverableId, projectId, reportId, workPackageId)
      ));
  }

  uploadOutputFile(file: File, workPackageId: number, outputId: number): Observable<JemsFileMetadataDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ]).pipe(
      take(1),
      switchMap(([projectId, reportId]) => this.projectReportWorkPlanService.uploadFileToOutputForm(
        file, outputId, projectId, reportId, workPackageId)
      ));
  }

  deleteFile(fileId: number): Observable<any> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ]).pipe(
      take(1),
      switchMap(([projectId, reportId]) =>
        this.projectReportAnnexesService.deleteProjectReportAnnexesFile(fileId, projectId, reportId)
      ),
    );
  }

  downloadFile(fileId: number): Observable<any> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ]).pipe(
      take(1),
      switchMap(([projectId, reportId]) => {
        this.downloadService.download(`/api/project/report/byProjectId/${projectId}/byReportId/${reportId}/byFileId/${fileId}/download`, 'project-report');
        return of(null);
      })
    );
  }

}
