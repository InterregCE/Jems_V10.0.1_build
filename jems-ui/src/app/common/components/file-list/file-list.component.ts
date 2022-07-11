import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input, OnChanges,
  Output, SimpleChanges,
} from '@angular/core';
import { PageFileList } from '@common/components/file-list/page-file-list';
import { FileListItem } from '@common/components/file-list/file-list-item';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Alert } from '@common/components/forms/alert';
import { Tables } from '@common/utils/tables';

@Component({
  selector: 'jems-file-list',
  templateUrl: './file-list.component.html',
  styleUrls: ['./file-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FileListComponent implements OnChanges {
  Alert = Alert;
  Tables = Tables;

  displayedColumns: string[] = ['name', 'location', 'uploadDate', 'user', 'size', 'actions'];
  dataSource = new MatTableDataSource<FileListItem>();

  @Input()
  fileList: PageFileList;

  @Output()
  onSortChange = new EventEmitter<Partial<MatSort>>();

  @Output()
  onPageIndexChange = new EventEmitter<number>();

  @Output()
  onPageSizeChange = new EventEmitter<number>();

  @Output()
  onDownload = new EventEmitter<FileListItem>();

  @Output()
  onDelete = new EventEmitter<FileListItem>();

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.fileList && this.fileList) {
      this.dataSource.data = this.fileList.content;
    }
  }

}
