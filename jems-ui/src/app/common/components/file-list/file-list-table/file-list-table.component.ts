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
import { MatTableDataSource } from '@angular/material/table';
import { Alert } from '@common/components/forms/alert';
import { Tables } from '@common/utils/tables';
import { FileListItem } from '@common/components/file-list/file-list-item';
import { FileDescriptionChange } from '@common/components/file-list/file-list-table/file-description-change';
import { FormBuilder, Validators } from '@angular/forms';
import { Forms } from '@common/utils/forms';
import { MatDialog } from '@angular/material/dialog';
import {UntilDestroy, untilDestroyed} from "@ngneat/until-destroy";
import { filter, take, tap } from 'rxjs/operators';
import { SecurityService } from '../../../../security/security.service';

@UntilDestroy()
@Component({
  selector: 'jems-file-list-table',
  templateUrl: './file-list-table.component.html',
  styleUrls: ['./file-list-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FileListTableComponent implements OnChanges, AfterViewInit {
  Alert = Alert;
  Tables = Tables;

  displayedColumns: string[] = ['name', 'location', 'uploadDate', 'user', 'size', 'description', 'action'];
  dataSource = new MatTableDataSource<FileListItem>();

  @ViewChild(MatSort) sort: MatSort

  @Input()
  fileList: FileListItem[];
  @Input()
  sortingEnabled = false;

  @Output()
  onSortChange = new EventEmitter<Partial<MatSort>>();
  @Output()
  onDownload = new EventEmitter<FileListItem>();
  @Output()
  onDelete = new EventEmitter<FileListItem>();

  @Output()
  onDescriptionChange = new EventEmitter<FileDescriptionChange>();
  @Input()
  savingInProgressForId: number | null = null;

  descriptionForm = this.formBuilder.group({
    id: [null, Validators.required],
    description: ['', Validators.maxLength(250)],
  });

  constructor(
    private formBuilder: FormBuilder,
    private dialog: MatDialog,
    public securityService: SecurityService,
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

  saveDescription() {
    this.onDescriptionChange.emit(this.descriptionForm.value);
  }

  editDescription(file: FileListItem) {
    this.descriptionForm.patchValue({
      id: file.id,
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
      ).subscribe(() => this.onDelete.emit(file));
  }

}
