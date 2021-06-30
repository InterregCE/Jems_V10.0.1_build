import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {combineLatest, Observable, ReplaySubject, Subject} from 'rxjs';
import {catchError, map, startWith, switchMap, take, takeUntil, tap} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';
import {
  OutputProjectFile,
  PageOutputProjectFile, ProjectDecisionDTO,
  ProjectDetailDTO,
  ProjectFileStorageService,
  ProjectStatusDTO,
  UserRoleCreateDTO
} from '@cat/api';
import {MatSort} from '@angular/material/sort';
import {Tables} from '../../../../../common/utils/tables';
import {Log} from '../../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {APIError} from '../../../../../common/models/APIError';
import {ProjectUtil} from '../../../../project-util';
import {ProjectDetailPageStore} from '../../../../project-detail-page/project-detail-page-store';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Component({
  selector: 'app-project-application-files',
  templateUrl: './project-application-files.component.html',
  styleUrls: ['./project-application-files.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFilesComponent extends BaseComponent {

  @Input()
  projectId: number;
  @Input()
  fileType: OutputProjectFile.TypeEnum;

  uploadSuccess$ = new Subject<boolean>();
  uploadError$ = new ReplaySubject<APIError | null>(1);

  deleteSuccess$ = new Subject<boolean>();
  deleteError$ = new ReplaySubject<APIError | null>(1);

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
      switchMap(([pageIndex, pageSize, sort]) => {
        return this.fileType === OutputProjectFile.TypeEnum.APPLICANTFILE ?
          this.projectFileStorageService.getApplicationFilesForProject(this.projectId, pageIndex, pageSize, sort) :
          this.projectFileStorageService.getAssessmentFilesForProject(this.projectId, pageIndex, pageSize, sort);
      }),
    );

  details$ = combineLatest([
    this.currentPage$,
    this.projectDetailPageStore.project$,
    this.projectDetailPageStore.projectCurrentDecisions$,
    this.projectDetailPageStore.isProjectLatestVersion$,
    this.permissionService.hasPermission(PermissionsEnum.ProjectFileAssessmentUpdate),
    this.permissionService.hasPermission(PermissionsEnum.ProjectFileApplicationUpdate),
    this.permissionService.hasPermission(PermissionsEnum.ProjectFileApplicationRetrieve),
    this.projectDetailPageStore.isThisUserOwner$,
  ])
    .pipe(
      map(([page, project, decisions, isProjectLatestVersion, canUploadAssessmentFile, canUploadApplicationFile, canRetrieveApplicationFile, isThisUserOwner]: [PageOutputProjectFile, ProjectDetailDTO, ProjectDecisionDTO, boolean, boolean, boolean, boolean, boolean]) => ({
        page,
        project,
        fundingDecisionDefined: !!decisions?.finalFundingDecision || !!decisions?.preFundingDecision,
        uploadPossible: this.canUploadFiles(project, isProjectLatestVersion, canUploadAssessmentFile, canUploadApplicationFile, canRetrieveApplicationFile),
        canChangeApplicationFile: canUploadApplicationFile,
        canRetrieveApplicationFile,
        canChangeAssessmentFile: canUploadAssessmentFile,
        isThisUserOwner,
      }))
    );

  constructor(private permissionService: PermissionService,
              private projectDetailPageStore: ProjectDetailPageStore,
              private projectFileStorageService: ProjectFileStorageService) {
    super();
  }

  private getUploadRequest(file: File): Observable<any> {
    if (this.fileType === OutputProjectFile.TypeEnum.APPLICANTFILE) {
      return this.projectFileStorageService.uploadProjectApplicationFileForm(file, this.projectId);
    }
    return this.projectFileStorageService.uploadProjectAssessmentFileForm(file, this.projectId);
  }

  addNewFilesForUpload(file: File): void {
    this.getUploadRequest(file)
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
    const type = this.fileType === OutputProjectFile.TypeEnum.APPLICANTFILE ? 'applicant' : 'assessment';
    window.open(
      `/api/project/${this.projectId}/file/${type}/${file.id}`,
      '_blank',
    );
  }

  private getDeleteRequest(file: OutputProjectFile): Observable<any> {
    if (this.fileType === OutputProjectFile.TypeEnum.APPLICANTFILE) {
      return this.projectFileStorageService.deleteProjectApplicationFile(file.id, this.projectId);
    }
    return this.projectFileStorageService.deleteProjectAssessmentFile(file.id, this.projectId);
  }

  deleteFile(file: OutputProjectFile): void {
    this.getDeleteRequest(file)
        .pipe(
          take(1),
          takeUntil(this.destroyed$),
          tap(() => this.newPageIndex$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
          tap(() => Log.info('Deleted file', this, file.name)),
          tap(() => this.deleteSuccess$.next(true)),
          tap(() => this.deleteError$.next(null)),
          catchError((error: HttpErrorResponse) => {
            this.deleteError$.next(error.error);
            throw error;
          })
        ).subscribe();
  }

  private getSaveDescriptionRequest(file: OutputProjectFile): Observable<OutputProjectFile> {
    if (this.fileType === OutputProjectFile.TypeEnum.APPLICANTFILE) {
      return this.projectFileStorageService.setDescriptionToProjectApplicationFile(file.id, this.projectId, {description: file.description});
    }
    return this.projectFileStorageService.setDescriptionToProjectAssessmentFile(file.id, this.projectId, {description: file.description});
  }

  saveDescription(file: OutputProjectFile): void {
    this.getSaveDescriptionRequest(file)
        .pipe(
          take(1),
          takeUntil(this.destroyed$),
          tap(saved => Log.info('Changed file description', this, saved)),
          tap(() => this.refreshPage$.next()),
        ).subscribe();
  }

  private canUploadFiles(project: ProjectDetailDTO, isProjectLatestVersion: boolean, canUploadAssessmentFile: boolean, canUploadApplicationFile: boolean, canRetrieveApplicationFile: boolean): boolean {
    if (!isProjectLatestVersion) {
      return false;
    }

    if (this.fileType === OutputProjectFile.TypeEnum.ASSESSMENTFILE) {
      return canUploadAssessmentFile;
    }

    // make a difference between users with only View permission and applicants
    if (this.fileType === OutputProjectFile.TypeEnum.APPLICANTFILE) {
      if (canUploadApplicationFile && canRetrieveApplicationFile) {
        return canUploadApplicationFile;
      }
      if (canRetrieveApplicationFile) {
        return false;
      }
    }

    return ProjectUtil.isDraft(project) || project.projectStatus.status === ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT;
  }
}
