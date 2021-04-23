import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {ProjectStore} from '../services/project-store.service';
import {combineLatest, ReplaySubject, Subject} from 'rxjs';
import {catchError, map, mergeMap, startWith, take, takeUntil, tap} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';
import {Permission} from '../../../../../security/permissions/permission';
import {OutputProjectFile, ProjectDetailDTO, ProjectFileStorageService, ProjectStatusDTO} from '@cat/api';
import {MatSort} from '@angular/material/sort';
import {Tables} from '../../../../../common/utils/tables';
import {Log} from '../../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {APIError} from '../../../../../common/models/APIError';

@Component({
  selector: 'app-project-application-files',
  templateUrl: './project-application-files.component.html',
  styleUrls: ['./project-application-files.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFilesComponent extends BaseComponent {
  Permission = Permission;

  @Input()
  projectId: number;
  @Input()
  fileType: OutputProjectFile.TypeEnum;

  uploadSuccess$ = new Subject<boolean>();
  uploadError$ = new ReplaySubject<APIError | null>(1);

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  refreshPage$ = new Subject<void>();
  newSort$ = new Subject<Partial<MatSort>>();

  private currentPage$ = combineLatest([
    this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
    this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
    this.newSort$.pipe(
      startWith(Tables.DEFAULT_INITIAL_SORT),
      map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
      map(sort => `${sort.active},${sort.direction}`)
    ),
    this.refreshPage$.pipe(startWith(null))
  ])
    .pipe(
      mergeMap(([pageIndex, pageSize, sort]) =>
        this.projectFileStorageService.getFilesForProject(this.fileType, this.projectId, pageIndex, pageSize, sort)),
      tap(page => Log.info('Fetched the project files:', this, page.content)),
    );

  details$ = combineLatest([
    this.currentPage$,
    this.projectStore.project$,
    this.projectStore.projectCurrentDecisions$,
    this.permissionService.permissionsChanged()
  ])
    .pipe(
      map(([page, project, decisions, permissions]) => ({
        page,
        project,
        fundingDecisionDefined: !!decisions?.fundingDecision,
        permission: permissions[0],
        uploadPossible: this.canUploadFiles(project, permissions[0])
      }))
    );

  constructor(private permissionService: PermissionService,
              private projectStore: ProjectStore,
              private projectFileStorageService: ProjectFileStorageService) {
    super();
  }

  addNewFilesForUpload(file: File): void {
    this.projectFileStorageService.uploadProjectFileForm(file, this.fileType, this.projectId)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.newPageIndex$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
        tap(() => this.uploadSuccess$.next(true)),
        tap(() => this.uploadError$.next(null)),
        catchError((error: HttpErrorResponse) => {
          this.uploadError$.next(error.error);
          throw error;
        })
      ).subscribe();
  }

  downloadFile(file: OutputProjectFile): void {
    window.open(
      `/api/project/${this.projectId}/file/${file.id}`,
      '_blank',
    );
  }

  deleteFile(file: OutputProjectFile): void {
    this.projectFileStorageService.deleteFile(file.id, this.projectId)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.newPageIndex$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
        tap(() => Log.info('Deleted file', this, file.name)),
      ).subscribe();
  }

  saveDescription(file: OutputProjectFile): void {
    this.projectFileStorageService.setDescriptionToFile(file.id, this.projectId, {description: file.description})
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(saved => Log.info('Changed file description', this, saved)),
        tap(() => this.refreshPage$.next()),
      ).subscribe();
  }

  private canUploadFiles(project: ProjectDetailDTO, permission: Permission): boolean {
    if (permission === Permission.ADMINISTRATOR) {
      return true;
    }
    if (this.fileType === OutputProjectFile.TypeEnum.ASSESSMENTFILE) {
      return permission === Permission.PROGRAMME_USER;
    }
    if (permission === Permission.PROGRAMME_USER) {
      // programme user can only upload assessment files
      return false;
    }
    return project.projectStatus.status === ProjectStatusDTO.StatusEnum.DRAFT
      || project.projectStatus.status === ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT;
  }
}
