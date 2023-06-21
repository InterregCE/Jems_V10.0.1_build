import {ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, forwardRef, Input, Output} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {JemsFileMetadataDTO} from '@cat/api';
import {MatDialog} from '@angular/material/dialog';
import {CustomTranslatePipe} from '@common/pipe/custom-translate-pipe';
import {Forms} from '@common/utils/forms';
import {filter, take, tap} from 'rxjs/operators';
import {LocaleDatePipe} from '@common/pipe/locale-date.pipe';

@Component({
  selector: 'jems-file-operations-action-cell',
  templateUrl: './file-operations-action-cell.component.html',
  styleUrls: ['./file-operations-action-cell.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => FileOperationsActionCellComponent),
      multi: true
    }
  ]
})
export class FileOperationsActionCellComponent implements ControlValueAccessor {
  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  fileMetadata: JemsFileMetadataDTO;
  isUploadInProgress = false;

  @Input()
  isUploadAccepted = true;

  @Input()
  isFileDeletable = true;
  @Input()
  set isUploadDone(value: boolean){
    if (value) {
      this.isUploadInProgress = false;
    }
  }
  @Output()
  upload = new EventEmitter<any>();
  @Output()
  download = new EventEmitter<number>();
  @Output()
  delete = new EventEmitter<number>();

  constructor(private changeDetectorRef: ChangeDetectorRef,
              private dialog: MatDialog,
              private localeDatePipe: LocaleDatePipe,
              private translatePipe: CustomTranslatePipe) {
  }

  onChange = (value: any) => {
    // Intentionally left blank
  };

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    // Intentionally left blank
  }

  writeValue(obj: JemsFileMetadataDTO): void {
    this.fileMetadata = obj;
    this.changeDetectorRef.detectChanges();
  }

  getTooltipText(): string {
    return `${this.fileMetadata.name}
${this.translatePipe
      .transform('use.case.update.project.partner.report.workplan.download.file.tooltip.upload.date')} ${this.localeDatePipe.transform(this.fileMetadata.uploaded)}`;
  }

  uploadFile(event: Event) {
    this.isUploadInProgress = false;
    if (this.fileMetadata?.name) {
      Forms.confirm(this.dialog, {
        title: this.fileMetadata.name,
        message: {
          i18nKey: 'use.case.update.project.partner.report.workplan.upload.override.file',
          i18nArguments: {fileName: this.fileMetadata.name}
        }
      }).pipe(
        take(1),
        filter(yes => yes),
        tap(() => {
            this.isUploadInProgress = true;
            this.upload.emit(event);
          }
        )
      ).subscribe();
    } else {
      this.isUploadInProgress = true;
      this.upload.emit(event);
    }
  }

  deleteFile(id: number) {
    if(this.fileMetadata?.name) {
      Forms.confirm(this.dialog, {
        title: this.fileMetadata.name,
        message: {i18nKey: 'file.dialog.message', i18nArguments: {name: this.fileMetadata.name}}
      }).pipe(
        take(1),
        filter(yes => yes),
        tap(() => this.delete.emit(id))
      ).subscribe();
    }
  }
}
