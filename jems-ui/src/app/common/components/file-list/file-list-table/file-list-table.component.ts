import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input, OnChanges,
  Output, SimpleChanges,
} from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Alert } from '@common/components/forms/alert';
import { Tables } from '@common/utils/tables';
import { FileListItem } from '@common/components/file-list/file-list-item';
import { FileDescriptionChange } from '@common/components/file-list/file-list-table/file-description-change';
import { FormBuilder, Validators } from '@angular/forms';
import { Forms } from '@common/utils/forms';
import { filter, take } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'jems-file-list-table',
  templateUrl: './file-list-table.component.html',
  styleUrls: ['./file-list-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FileListTableComponent implements OnChanges {
  Alert = Alert;
  Tables = Tables;

  displayedColumns: string[] = ['name', 'location', 'uploadDate', 'user', 'size', 'description', 'action'];
  dataSource = new MatTableDataSource<FileListItem>();

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
    description: '',
  });

  constructor(
    private formBuilder: FormBuilder,
    private dialog: MatDialog,
  ) {
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
