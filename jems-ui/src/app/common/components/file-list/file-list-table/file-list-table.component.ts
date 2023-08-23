import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import {MatSort, MatSortable} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {Alert} from '@common/components/forms/alert';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {FormBuilder, Validators} from '@angular/forms';
import {Forms} from '@common/utils/forms';
import {AlertMessage} from '@common/components/file-list/file-list-table/alert-message';
import {MatDialog} from '@angular/material/dialog';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {catchError, filter, finalize, take, tap} from 'rxjs/operators';
import {BehaviorSubject, Observable} from 'rxjs';
import {SecurityService} from '../../../../security/security.service';
import {v4 as uuid} from 'uuid';
import {FileListTableConstants} from './file-list-table-constants';
import {RoutingService} from "@common/services/routing.service";

@UntilDestroy()
@Component({
  selector: 'jems-file-list-table',
  templateUrl: './file-list-table.component.html',
  styleUrls: ['./file-list-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FileListTableComponent implements OnChanges, AfterViewInit {
  private serviceId = uuid();

  Alert = Alert;
  SENSITIVE_FILE_NAME_MASK = FileListTableConstants.SENSITIVE_FILE_NAME_MASK;

  displayedColumns: string[] = ['name', 'location', 'uploadDate', 'user', 'size', 'description', 'action'];
  dataSource = new MatTableDataSource<FileListItem>();

  alerts$ = new BehaviorSubject<AlertMessage[]>([]);

  @ViewChild(MatSort) sort: MatSort;

  @Input()
  fileList: FileListItem[];
  @Input()
  sortingEnabled = false;
  @Input()
  overrideUploadTranslation = 'file.table.column.name.timestamp';

  @Input()
  setDescriptionCallback: (data: FileDescriptionChange) => Observable<any>;
  @Input()
  deleteCallback: (file: FileListItem) => Observable<void>;

  @Output()
  onSortChange = new EventEmitter<Partial<MatSort>>();
  @Output()
  onDownload = new EventEmitter<FileListItem>();
  @Output()
  refresh = new EventEmitter<any>();

  descriptionForm = this.formBuilder.group({
    id: [null, Validators.required],
    fileName: '',
    description: ['', Validators.maxLength(250)],
  });

  constructor(
    private formBuilder: FormBuilder,
    private dialog: MatDialog,
    public securityService: SecurityService,
    private routingService: RoutingService,
  ) {
  }

  ngAfterViewInit(): void {
    this.sort?.sortChange
      .pipe(
        tap(() => this.onSortChange.emit(this.sort)),
        untilDestroyed(this),
      ).subscribe();
    this.sort?.sort(({ id: 'uploaded', start: 'desc'}) as MatSortable);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.fileList && this.fileList) {
      this.dataSource.data = this.fileList;
      this.resetDescription();
    }
  }

  resetDescription() {
    this.descriptionForm.reset();
  }

  savingDescriptionId$ = new BehaviorSubject<number | null>(null);
  saveDescription() {
    this.savingDescriptionId$.next(this.descriptionForm.value.id);
    this.setDescriptionCallback(this.descriptionForm.value)
      .pipe(
        take(1),
        tap(() => this.showAlert(FileListTableComponent.successAlert(
          'file.description.change.message.success',
          { fileName: this.descriptionForm.value.fileName },
        ))),
        catchError(error => {
          this.showAlert(FileListTableComponent.errorAlert(
            'file.description.change.message.failed',
            { fileName: this.descriptionForm.value.fileName },
          ));
          throw error;
        }),
        finalize(() => this.routingService.confirmLeaveMap.delete(this.serviceId)),
        finalize(() => this.savingDescriptionId$.next(null)),
        tap(() => this.descriptionForm.reset()),
        tap(() => this.refresh.emit()),
      ).subscribe();
  }

  editDescription(file: FileListItem) {
    this.routingService.confirmLeaveMap.set(this.serviceId, true)
    this.descriptionForm.patchValue({
      id: file.id,
      fileName: file.name,
      description: file.description,
    });
  }

  deleteFile(file: FileListItem) {
    this.routingService.confirmLeaveMap.delete(this.serviceId)
    Forms.confirm(
      this.dialog, {
        title: file.name,
        message: {i18nKey: 'file.dialog.message', i18nArguments: {name: file.name}}
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        tap(() => this.performDeletion(file)),
      ).subscribe();
  }

  deletingId$ = new BehaviorSubject<number | null>(null);
  private performDeletion(file: FileListItem) {
    this.deletingId$.next(file.id);
    this.deleteCallback(file)
      .pipe(
        take(1),
        tap(() => this.showAlert(
          FileListTableComponent.successAlert('file.delete.message.successful', { fileName: file.name })
        )),
        catchError(error => {
          this.showAlert(
            FileListTableComponent.errorAlert('file.delete.message.failed', { fileName: file.name }));
          throw error;
        }),
        finalize(() => this.deletingId$.next(null)),
        tap(() => this.refresh.emit()),
      ).subscribe();
  }

  private showAlert(alert: AlertMessage) {
    this.alerts$.next([...this.alerts$.value, alert]);
    setTimeout(
      () => this.dismissAlert(alert.id),
      alert.type === Alert.SUCCESS ? 5000 : 30000);
  }

  dismissAlert(id: string) {
    const alerts = this.alerts$.value.filter(that => that.id !== id);
    this.alerts$.next(alerts);
  }

  static successAlert(msg: string, i18nArgs: any = {}): AlertMessage {
    return {
      id: uuid(),
      type: Alert.SUCCESS,
      i18nMessage: msg,
      i18nArgs,
    };
  }

  static errorAlert(msg: string, i18nArgs: any = {}): AlertMessage {
    return {
      id: uuid(),
      type: Alert.ERROR,
      i18nMessage: msg,
      i18nArgs,
    };
  }

}
