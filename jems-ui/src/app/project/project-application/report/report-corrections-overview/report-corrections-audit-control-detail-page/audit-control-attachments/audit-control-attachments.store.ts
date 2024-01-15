import {Injectable} from '@angular/core';
import {APIError} from '@common/models/APIError';
import {JemsFileDTO, JemsFileMetadataDTO, PageJemsFileDTO, ProjectAuditAndControlFileService} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {FileListTableConstants} from '@common/components/file-list/file-list-table/file-list-table-constants';
import {MatSort} from '@angular/material/sort';
import {RoutingService} from '@common/services/routing.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ReportCorrectionsAuditControlDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/report-corrections-audit-control-detail-page.store';
import {catchError, distinctUntilChanged, filter, finalize, map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {v4 as uuid} from 'uuid';
import {DownloadService} from '@common/services/download.service';


@Injectable({providedIn: 'root'})
export class AuditControlAttachmentsStore {

  fileList$: Observable<JemsFileDTO[]>;
  filesChanged$ = new Subject();
  newSort$ = new BehaviorSubject<Partial<MatSort>>(FileListTableConstants.DEFAULT_SORT);
  error$ = new Subject<APIError | null>();

  private projectId$: Observable<number>;
  private auditControlId$: Observable<number>;
  canEdit$: Observable<boolean>;

  constructor(
    private projectAuditControlFileService: ProjectAuditAndControlFileService,
    private routingService: RoutingService,
    private projectStore: ProjectStore,
    private auditControlDetailPageStore: ReportCorrectionsAuditControlDetailPageStore,
    private downloadService: DownloadService,
  ) {
    this.projectId$ = projectStore.projectId$.pipe(filter(Boolean), map(Number));
    this.auditControlId$ = auditControlDetailPageStore.auditControlId$.pipe(filter(Boolean), map(Number));
    this.canEdit$ = auditControlDetailPageStore.canEdit$;

    this.fileList$ = this.fileList();
  }

  private fileList(): Observable<JemsFileDTO[]> {
    return combineLatest([
      this.projectId$,
      this.auditControlId$,
      this.newSort$.pipe(
        map(sort => sort?.direction ? sort : FileListTableConstants.DEFAULT_SORT),
        map(sort => `${sort.active},${sort.direction}`),
        distinctUntilChanged(),
      ),
      this.filesChanged$.pipe(startWith(null)),
    ]).pipe(
      switchMap(([projectId, auditControlId, sort]) =>
        this.projectAuditControlFileService.list(auditControlId, projectId, 0, 40, sort)
      ),
      map((page: PageJemsFileDTO) => page.content),
      tap(data => Log.info('Fetched project audi control files', this, data)),
    );
  }

  uploadFile(file: File): Observable<JemsFileMetadataDTO> {
    const serviceId = uuid();
    this.routingService.confirmLeaveMap.set(serviceId, true);
    return combineLatest([
      this.projectId$,
      this.auditControlId$,
    ]).pipe(
      take(1),
      switchMap(([projectId, auditControlId]) =>
        this.projectAuditControlFileService.uploadForm(file, auditControlId, projectId)
      ),
      tap(() => this.filesChanged$.next()),
      tap(() => this.error$.next(null)),
      finalize(() => this.routingService.confirmLeaveMap.delete(serviceId)),
      catchError(error => {
        this.error$.next(error.error);
        return of({} as JemsFileMetadataDTO);
      }),
    );
  }

  downloadFile(fileId: number): Observable<any> {
    return combineLatest([
      this.projectId$,
      this.auditControlId$,
    ]).pipe(
      take(1),
      switchMap(([projectId, auditControlId]) => {
        this.downloadService.download(
          `/api/project/${projectId}/audit/${auditControlId}/file/byFileId/${fileId}/`,
          'project-audit-control-file'
        );
        return of(null);
      }),
    );
  }

  updateDescription(fileId: number, description: string): Observable<any> {
    return combineLatest([
      this.projectId$,
      this.auditControlId$
    ]).pipe(
      take(1),
      switchMap(([projectId, auditControlId]) =>
        this.projectAuditControlFileService.updateDescription(auditControlId, fileId, projectId, description)
      ),
    );
  }

  delete(fileId: number): Observable<any> {
    return combineLatest([
      this.projectId$,
      this.auditControlId$
    ]).pipe(
      take(1),
      switchMap(([projectId, auditControlId]) =>
        this.projectAuditControlFileService._delete(auditControlId, fileId, projectId)
      ),
    );
  }

}
