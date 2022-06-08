import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  forwardRef,
  Input,
  Output
} from '@angular/core';
import {ProjectReportFileMetadataDTO} from '@cat/api';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {Forms} from '@common/utils/forms';
import {filter, take, tap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {DatePipe} from '@angular/common';
import {CustomTranslatePipe} from '@common/pipe/custom-translate-pipe';

@Component({
  selector: 'jems-partner-actions-cell',
  templateUrl: './partner-actions-cell.component.html',
  styleUrls: ['./partner-actions-cell.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PartnerActionsCellComponent),
      multi: true
    }
  ]
})
export class PartnerActionsCellComponent implements ControlValueAccessor {
  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  fileMetadata: ProjectReportFileMetadataDTO;

  @Input()
  isReportEditable = true;
  @Output()
  upload = new EventEmitter<any>();
  @Output()
  download = new EventEmitter<number>();
  @Output()
  delete = new EventEmitter<number>();

  constructor(private changeDetectorRef: ChangeDetectorRef,
              private dialog: MatDialog,
              private datePipe: DatePipe,
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

  writeValue(obj: ProjectReportFileMetadataDTO): void {
    this.fileMetadata = obj;
    this.changeDetectorRef.detectChanges();
  }

  getTooltipText(): string {
    return `${this.fileMetadata.name}
${this.translatePipe
      .transform('use.case.update.project.partner.report.workplan.download.file.tooltip.upload.date')} ${this.datePipe
      .transform(this.fileMetadata.uploaded, 'MM/dd/yyyy')}`;
  }

  uploadFile(event: Event) {
    if(this.fileMetadata?.name) {
      Forms.confirm(this.dialog, {
        title:this.fileMetadata.name,
        message: {
          i18nKey: 'use.case.update.project.partner.report.workplan.upload.override.file',
          i18nArguments: {fileName: this.fileMetadata.name}
        }
      }).pipe(
        take(1),
        filter(yes => yes),
        tap(() => this.upload.emit(event))
      ).subscribe();
    } else {
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
