import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { PageFileList } from '@common/components/file-list/page-file-list';
import { MatSort } from '@angular/material/sort';
import { Tables } from '@common/utils/tables';
import { FileListItem } from '@common/components/file-list/file-list-item';
import {Observable, Subject, Subscription} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {take} from 'rxjs/operators';
import {JemsFileMetadataDTO} from '@cat/api';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';

@Component({
  selector: 'jems-file-list',
  templateUrl: './file-list.component.html',
  styleUrls: ['./file-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FileListComponent {
  Tables = Tables;

  @Input()
  fileList: PageFileList;

  @Input()
  setDescriptionCallback: (data: FileDescriptionChange) => Observable<any>;
  @Input()
  deleteCallback: (file: FileListItem) => Observable<void>;

  @Output()
  onSortChange = new EventEmitter<Partial<MatSort>>();

  @Output()
  onPageIndexChange = new EventEmitter<number>();

  @Output()
  onPageSizeChange = new EventEmitter<number>();

  @Output()
  onDownload = new EventEmitter<FileListItem>();

  @Output()
  refresh = new EventEmitter<any>();

  static doFileUploadWithValidation(
    target: any,
    fileSizeOverLimitError$: Subject<boolean>,
    error$: Subject<APIError | null>,
    maximumAllowedFileSizeInMB: number,
    callback: (file: File) => Observable<JemsFileMetadataDTO>,
  ): Subscription {
    if (!target) {
      return Subscription.EMPTY;
    }

    fileSizeOverLimitError$.next(false);
    error$.next(null);

    if (target?.files[0].size > maximumAllowedFileSizeInMB * 1024 * 1024) {
      setTimeout(() => fileSizeOverLimitError$.next(true), 10);
      return Subscription.EMPTY;
    }

    return callback(target?.files[0])
      .pipe(take(1))
      .subscribe();
  }
}
