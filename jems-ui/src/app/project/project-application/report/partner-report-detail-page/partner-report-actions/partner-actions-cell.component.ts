import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  forwardRef,
  Input,
  Output
} from '@angular/core';
import {JemsFileMetadataDTO} from '@cat/api';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {Forms} from '@common/utils/forms';
import {filter, take, tap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {DatePipe} from '@angular/common';
import {CustomTranslatePipe} from '@common/pipe/custom-translate-pipe';
import {FileListTableConstants} from '@common/components/file-list/file-list-table/file-list-table-constants';
import {FormService} from '@common/components/section/form/form.service';
import {ReportFileManagementStore} from '@project/project-application/report/partner-report-detail-page/partner-report-annexes-tab/report-file-management-store';

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
  @Input()
  fileMetadata: JemsFileMetadataDTO;
  isUploadInProgress = false;
  anonymizedName = FileListTableConstants.SENSITIVE_FILE_NAME_MASK;

  @Input()
  isReportEditable = true;

  @Input()
  set isUploadDone(value: boolean){
    if (value) {
      this.isUploadInProgress = false;
      this.formService.setSuccess(null);
    }
  }
  @Output()
  upload = new EventEmitter<any>();
  @Output()
  download = new EventEmitter<number>();
  @Output()
  delete = new EventEmitter<number>();

  maximumAllowedFileSizeInMB: number;

  constructor(private changeDetectorRef: ChangeDetectorRef,
              private dialog: MatDialog,
              private datePipe: DatePipe,
              private translatePipe: CustomTranslatePipe,
              private formService: FormService,
              private fileManagementStore: ReportFileManagementStore
              ) {
    this.fileManagementStore.getMaximumAllowedFileSize().pipe(tap(value => this.maximumAllowedFileSizeInMB = value)).subscribe();
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
      .transform('use.case.update.project.partner.report.workplan.download.file.tooltip.upload.date')} ${this.datePipe
      .transform(this.fileMetadata.uploaded, 'MM/dd/yyyy')}`;
  }

  uploadFile(event: any) {
    if (this.formService.checkFileSizeError(event?.target?.files[0].size, this.maximumAllowedFileSizeInMB)) {
      return;
    }

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
            this.upload.emit(event?.target);
          }
        )
      ).subscribe();
    } else {
      this.isUploadInProgress = true;
      this.upload.emit(event?.target);
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

  downloadFile(file: JemsFileMetadataDTO) {
    if (file.name !== this.anonymizedName) {
      this.download.emit(file.id);
    }
  }

}
