import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {JemsFileDTO, JemsFileMetadataDTO, PageJemsFileDTO, ProjectReportDTO, ProjectReportVerificationFileService} from '@cat/api';
import {MatSort} from '@angular/material/sort';
import {FileListTableConstants} from '@common/components/file-list/file-list-table/file-list-table-constants';
import {APIError} from '@common/models/APIError';
import {FileManagementStore} from '@project/common/components/file-management/file-management-store';
import {RoutingService} from '@common/services/routing.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {catchError, distinctUntilChanged, finalize, map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {v4 as uuid} from 'uuid';
import {DownloadService} from '@common/services/download.service';

@Injectable({providedIn: 'root'})
export class ProjectReportVerificationFileStore {

  fileList$: Observable<JemsFileDTO[]>;
  error$ = new Subject<APIError | null>();
  private projectId$: Observable<number>;
  private reportId$: Observable<number>;
  report$: Observable<ProjectReportDTO>;
  filesChanged$ = new Subject();
  newSort$ = new BehaviorSubject<Partial<MatSort>>(FileListTableConstants.DEFAULT_SORT);

  constructor(
    private projectReportVerificationFileService: ProjectReportVerificationFileService,
    private fileManagementStore: FileManagementStore,
    private routingService: RoutingService,
    private projectStore: ProjectStore,
    private projectReportDetailStore: ProjectReportDetailPageStore,
    private downloadService: DownloadService,
  ) {
    this.projectId$ = this.projectStore.projectId$;
    this.reportId$ = this.projectReportDetailStore.projectReportId$;
    this.report$ = this.projectReportDetailStore.projectReport$;
    this.fileList$ = this.fileList();
  }

  private fileList(): Observable<JemsFileDTO[]> {
    return combineLatest([
      this.projectId$,
      this.reportId$,
      this.newSort$.pipe(
        map(sort => sort?.direction ? sort : FileListTableConstants.DEFAULT_SORT),
        map(sort => `${sort.active},${sort.direction}`),
        distinctUntilChanged(),
      ),
      this.filesChanged$.pipe(startWith(null)),
    ]).pipe(
      switchMap(([projectId, reportId, sort]) =>
        this.projectReportVerificationFileService.list(projectId, reportId, 0, 40, sort)
      ),
      map((page: PageJemsFileDTO) => page.content),
      tap(data => Log.info('Fetched project report verification files', this, data)),
    );
  }


  uploadFile(file: File): Observable<JemsFileMetadataDTO> {
    const serviceId = uuid();
    this.routingService.confirmLeaveSet.add(serviceId);
    return combineLatest([
      this.projectId$,
      this.reportId$,
    ]).pipe(
      take(1),
      switchMap(([projectId, reportId]) =>
        this.projectReportVerificationFileService.uploadForm(file, projectId, reportId)
      ),
      tap(() => this.filesChanged$.next()),
      tap(() => this.error$.next(null)),
      finalize(() => this.routingService.confirmLeaveSet.delete(serviceId)),
      catchError(error => {
        this.error$.next(error.error);
        return of({} as JemsFileMetadataDTO);
      }),
    );
  }

  downloadFile(fileId: number): Observable<any> {
    return combineLatest([
      this.projectId$,
      this.reportId$,
    ]).pipe(
      take(1),
      switchMap(([projectId, reportId]) => {
        this.downloadService.download(
          `/api/project/report/byProjectId/${projectId}/byReportId/${reportId}/verification/file/byFileId/${fileId}/`,
          'project-report-verification-file'
        );
        return of(null);
      }),
    );
  }

  updateDescription(fileId: number, description: string): Observable<any> {
    return combineLatest([
      this.projectId$,
      this.reportId$
    ]).pipe(
      take(1),
      switchMap(([projectId, reportId]) =>
        this.projectReportVerificationFileService.updateDescription(fileId, projectId, reportId, description)
      ),
    );
  }

  delete(fileId: number): Observable<any> {
    return combineLatest([
      this.projectId$,
      this.reportId$
    ]).pipe(
      take(1),
      switchMap(([projectId, reportId]) =>
        this.projectReportVerificationFileService._delete(fileId, projectId, reportId)
      ),
    );
  }
}
