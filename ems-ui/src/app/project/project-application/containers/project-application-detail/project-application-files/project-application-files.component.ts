import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {ProjectStore} from '../services/project-store.service';
import {combineLatest, Observable, of, Subject} from 'rxjs';
import {catchError, flatMap, map, startWith, take, takeUntil, tap} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';
import {Permission} from '../../../../../security/permissions/permission';
import {OutputProjectFile, OutputProjectStatus, PageOutputProjectFile, ProjectFileStorageService} from '@cat/api';
import {MatSort} from '@angular/material/sort';
import {Tables} from '../../../../../common/utils/tables';
import {Log} from '../../../../../common/utils/log';
import {ProjectFileService} from '../../../services/project-file.service';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-project-application-files',
  templateUrl: './project-application-files.component.html',
  styleUrls: ['./project-application-files.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFilesComponent extends BaseComponent implements OnInit {
  Permission = Permission;

  @Input()
  projectId: number;

  editActionVisible: boolean;
  deleteActionVisible: boolean;
  downloadActionVisible: boolean;
  statusMessages: string[] = [];

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  refreshPage$ = new Subject<void>();
  newSort$ = new Subject<Partial<MatSort>>();

  project$ = this.projectStore.getProject();
  currentPage$: Observable<PageOutputProjectFile>;

  constructor(private permissionService: PermissionService,
              private projectStore: ProjectStore,
              private projectFileStorageService: ProjectFileStorageService,
              private projectFileService: ProjectFileService) {
    super();
  }

  ngOnInit(): void {
    this.assignActionsToUser();

    this.currentPage$ = combineLatest([
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
        flatMap(([pageIndex, pageSize, sort]) =>
          this.projectFileStorageService.getFilesForProject(this.projectId, pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the project files:', this, page.content)),
      );
  }

  addNewFilesForUpload(file: File): void {
    this.projectFileService.addProjectFile(this.projectId, file)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.newPageIndex$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
        map(() => `Upload of '${file.name}' successful.`),
        catchError((error: HttpErrorResponse) =>
          of(error.status === 422
            ? `File '${file.name}' already exists.`
            : `Upload of '${file.name}' not successful.`)
        ),
        tap(message => this.statusMessages = [message, ...this.statusMessages])
      ).subscribe();
  }

  downloadFile(element: OutputProjectFile): void {
    window.open(
      this.projectFileService.getDownloadLink(this.projectId, element.id),
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

  private assignActionsToUser(): void {
    combineLatest([
      this.permissionService.permissionsChanged(),
      this.project$
    ])
      .pipe(
        takeUntil(this.destroyed$)
      )
      .subscribe(([permissions, project]) => {
        const isAdmin = permissions.some(perm => perm === Permission.ADMINISTRATOR);

        this.editActionVisible = isAdmin;
        this.deleteActionVisible = isAdmin;
        this.downloadActionVisible = true;

        const isApplicant = permissions.some(perm => perm === Permission.APPLICANT_USER);
        if (!isApplicant) {
          return;
        }
        const status = project?.projectStatus?.status;
        this.editActionVisible = status === OutputProjectStatus.StatusEnum.DRAFT
          || status === OutputProjectStatus.StatusEnum.RETURNEDTOAPPLICANT
        this.deleteActionVisible = status === OutputProjectStatus.StatusEnum.DRAFT;
      })
  }
}
