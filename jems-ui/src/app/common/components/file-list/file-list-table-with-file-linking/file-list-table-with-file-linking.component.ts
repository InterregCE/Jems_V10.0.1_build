import {
  AfterViewInit, ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {BehaviorSubject, Observable} from 'rxjs';
import {AlertMessage} from '@common/components/file-list/file-list-table/alert-message';
import {MatSort, MatSortable} from '@angular/material/sort';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {FormBuilder, FormControl, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {SecurityService} from '../../../../security/security.service';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, filter, finalize, take, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Forms} from '@common/utils/forms';
import {v4 as uuid} from 'uuid';
import { Alert } from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-file-list-table-with-file-linking',
  templateUrl: './file-list-table-with-file-linking.component.html',
  styleUrls: ['./file-list-table-with-file-linking.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FileListTableWithFileLinkingComponent implements OnInit, OnChanges, AfterViewInit {
  Alert = Alert;

  dataSource = new MatTableDataSource<FileListItem>();

  alerts$ = new BehaviorSubject<AlertMessage[]>([]);

  @ViewChild(MatSort) sort: MatSort;

  @Input()
  displayedColumns: string[] = ['name', 'location', 'uploadDate', 'user', 'size', 'description', 'action'];
  @Input()
  fileList: FileListItem[];
  @Input()
  sortingEnabled = false;
  @Input()
  overrideUploadTranslation = 'file.table.column.name.timestamp';

  @Input()
  isUploadDone = false;

  @Input()
  isUploadAccepted = true;

  @Input()
  isFileDeletable = true;

  @Input()
  setDescriptionCallback: (data: FileDescriptionChange) => Observable<any>;
  @Input()
  deleteCallback: (file: FileListItem) => Observable<void>;

  @Input()
  uploadAttachmentCallback: (target: any, fileId: number) => Observable<any>;
  @Input()
  deleteAttachmentCallback: (fileId: number, attachmentId: number) => Observable<void>;
  @Input()
  downloadAttachmentCallback: (fileId: number) => void;

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

  attachmentForm = this.formBuilder.array([]);

  constructor(
    private formBuilder: FormBuilder,
    private dialog: MatDialog,
    public securityService: SecurityService,
    private formService: FormService,
  ) {
    this.formService.init(this.attachmentForm);
  }

  ngOnInit(): void {
    this.resetAttachments(this.fileList);
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
      this.resetAttachments(this.fileList);
      this.dataSource.data = this.fileList;
      this.resetDescription();
    }
  }

  resetDescription() {
    this.descriptionForm.reset();
  }

  resetAttachments(fileList: FileListItem[]): void {
    this.attachmentForm.reset();
    this.attachmentForm.clear();
    fileList.forEach((item, index) => {
      this.attachmentForm.push(this.formBuilder.group({
        attachment: this.formBuilder.control(item.attachment)
      }));
    });
  }

  savingDescriptionId$ = new BehaviorSubject<number | null>(null);
  saveDescription() {
    this.savingDescriptionId$.next(this.descriptionForm.value.id);
    this.setDescriptionCallback(this.descriptionForm.value)
      .pipe(
        take(1),
        tap(() => this.showAlert(FileListTableWithFileLinkingComponent.successAlert(
          'file.description.change.message.success',
          { fileName: this.descriptionForm.value.fileName },
        ))),
        catchError(error => {
          this.showAlert(FileListTableWithFileLinkingComponent.errorAlert(
            'file.description.change.message.failed',
            { fileName: this.descriptionForm.value.fileName },
          ));
          throw error;
        }),
        finalize(() => this.savingDescriptionId$.next(null)),
        tap(() => this.descriptionForm.reset()),
        tap(() => this.refresh.emit()),
      ).subscribe();
  }

  editDescription(file: FileListItem) {
    this.descriptionForm.patchValue({
      id: file.id,
      fileName: file.name,
      description: file.description,
    });
  }

  deleteFile(file: FileListItem) {
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
          FileListTableWithFileLinkingComponent.successAlert('file.delete.message.successful', { fileName: file.name })
        )),
        catchError(error => {
          this.showAlert(
            FileListTableWithFileLinkingComponent.errorAlert('file.delete.message.failed', { fileName: file.name }));
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

  private static successAlert(msg: string, i18nArgs: any = {}): AlertMessage {
    return {
      id: uuid(),
      type: Alert.SUCCESS,
      i18nMessage: msg,
      i18nArgs,
    };
  }

  private static errorAlert(msg: string, i18nArgs: any = {}): AlertMessage {
    return {
      id: uuid(),
      type: Alert.ERROR,
      i18nMessage: msg,
      i18nArgs,
    };
  }

  onUploadFile(target: any, fileId: number, index: number): void {
    this.uploadAttachmentCallback(target, fileId).subscribe(value => {
      this.attachment(index)?.patchValue(value);
    });
  }

  onDeleteFile(fileId: number, attachmentId: number, index: number): void {
    this.deleteAttachmentCallback(fileId, attachmentId)
      .pipe(take(1))
      .subscribe(_ => this.attachment(index)?.patchValue(null));
  }

  onDownloadFile(fileId: number): void {
    this.downloadAttachmentCallback(fileId);
  }

  attachment(index: number): FormControl {
    return this.attachmentForm.at(index).get('attachment') as FormControl;
  }

}
