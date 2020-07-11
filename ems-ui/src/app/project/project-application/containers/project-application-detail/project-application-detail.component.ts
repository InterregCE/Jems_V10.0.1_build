import {ChangeDetectionStrategy, Component} from '@angular/core';
import {OutputProjectFile, ProjectFileStorageService} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {ProjectFileService} from '../../services/project-file.service';
import {MatDialog} from '@angular/material/dialog';
import {combineLatest, Subject} from 'rxjs';
import {catchError, filter, flatMap, map, startWith, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {BaseComponent} from '@common/components/base-component';
import {HttpErrorResponse} from '@angular/common/http';
import {MatSort} from '@angular/material/sort';
import {Tables} from '../../../../common/utils/tables';
import {Forms} from '../../../../common/utils/forms';
import {ProjectStore} from './services/project-store.service';

@Component({
  selector: 'app-project-application-detail',
  templateUrl: './project-application-detail.component.html',
  styleUrls: ['./project-application-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    ProjectStore
  ]
})
export class ProjectApplicationDetailComponent extends BaseComponent {
  fileNumber = 0;
  projectId = this.activatedRoute.snapshot.params.projectId;
  statusMessages: string[];

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  refreshPage$ = new Subject<void>();
  newSort$ = new Subject<Partial<MatSort>>();

  currentPage$ =
    combineLatest([
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
        tap(page => this.fileNumber = page.totalElements),
      );

  STATUS_MESSAGE_SUCCESS = (filename: string) => `Upload of '${filename}' successful.`;
  ERROR_MESSAGE_UPLOAD = (filename: string) => `Upload of '${filename}' not successful.`;
  ERROR_MESSAGE_EXISTS = (filename: string) => `File '${filename}' already exists.`;

  constructor(private projectFileStorageService: ProjectFileStorageService,
              private projectFileService: ProjectFileService,
              public projectStore: ProjectStore,
              private dialog: MatDialog,
              private activatedRoute: ActivatedRoute) {
    super();
    this.projectStore.init(this.projectId);
  }

  addNewFilesForUpload($event: File) {
    this.projectFileService.addProjectFile(this.projectId, $event).pipe(
      takeUntil(this.destroyed$),
      tap(() => this.newPageIndex$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      catchError((error: HttpErrorResponse) => {
        this.addErrorFromResponse(error, $event.name);
        throw error;
      })
    ).subscribe(() => {
      this.addMessageFromResponse(this.STATUS_MESSAGE_SUCCESS($event.name));
    });
  }

  downloadFile(element: OutputProjectFile) {
    window.open(
      this.projectFileService.getDownloadLink(this.projectId, element.id),
      '_blank',
    );
  }

  deleteFile(file: OutputProjectFile) {
    Forms.confirmDialog(
      this.dialog,
      file.name,
      'Are you sure you want to delete' + ' ' + file.name + ' ?',)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        filter(answer => !!answer),
        flatMap(() => this.projectFileStorageService.deleteFile(file.id, this.projectId)),
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

  private addMessageFromResponse(status: string) {
    if (!this.statusMessages) {
      this.statusMessages = [];
    }
    this.statusMessages.unshift(status);
  }

  private addErrorFromResponse(status: any, filename: string) {
    if (status.error && status.status === 422) {
      this.addMessageFromResponse(this.ERROR_MESSAGE_EXISTS(filename));
    } else {
      this.addMessageFromResponse(this.ERROR_MESSAGE_UPLOAD(filename));
    }
  }
}
