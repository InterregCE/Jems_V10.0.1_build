import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  forwardRef,
  Output
} from '@angular/core';
import {ProjectReportFileMetadataDTO} from '@cat/api';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';

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

  acceptedFilesTypes = ['.csv', '.dat', '.db', '.dbf', '.log', '.mdb', '.xml', '.email', '.eml', '.emlx', '.msg', '.oft', '.ost', '.pst', '.vcf', '.bmp', '.gif', '.jpeg', '.jpg', '.png', '.psd', '.svg', '.tif', '.tiff', '.htm', '.html', '.key', '.odp', '.pps', '.ppt', '.ppt', '.pptx', '.ods', '.xls', '.xlsm', '.xlsx', '.doc', '.docx', '.odt', '.pdf', '.rtf', '.tex', '.txt', '.wpd', '.mov', '.avi', '.mp4', '.zip', '.rar', '.ace', '.7z', '.url'];

  fileMetadata: ProjectReportFileMetadataDTO;

  @Output()
  upload = new EventEmitter<any>();
  @Output()
  download = new EventEmitter<number>();
  @Output()
  delete = new EventEmitter<number>();

  constructor(private changeDetectorRef: ChangeDetectorRef) {
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
}
