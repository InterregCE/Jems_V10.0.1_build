import {Component, Input, OnInit} from '@angular/core';
import {Observable, of, Subject} from 'rxjs';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
import {ProjectReportFileDTO,} from '@cat/api';
import {ContractingFilesStoreService} from '@project/project-application/services/contracting-files-store.service';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {I18nMessage} from '@common/models/I18nMessage';
import {Alert} from '@common/components/forms/alert';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {catchError, map, take} from 'rxjs/operators';
import {PageFileList} from '@common/components/file-list/page-file-list';
import FileTypeEnum = ProjectReportFileDTO.TypeEnum;

@UntilDestroy()
@Component({
  selector: 'jems-contract-files',
  templateUrl: './contract-files.component.html',
  styleUrls: ['./contract-files.component.scss']
})
export class ContractFilesComponent implements OnInit {

  Alert = Alert;

  @Input()
  isEditable: boolean;

  files$: Observable<PageFileList>;
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();
  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;

  selectedCategoryPath$: Observable<I18nMessage[]>;

  constructor(
    public store: ContractingFilesStoreService,
  ) {
    this.selectedCategoryPath$ = store.selectedCategoryPath$;
    this.files$ = this.getFilesToList();
    this.store.getMaximumAllowedFileSize()
      .pipe(untilDestroyed(this))
      .subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
  }

  ngOnInit(): void {
    this.store.setFileCategories(this.fileCategories());
    this.store.setSection({type: FileTypeEnum.Contract} as CategoryInfo);
  }

  private getFilesToList(): Observable<PageFileList> {
   return this.store.fileList$.pipe(
      map(pageFiles => ({
        ...pageFiles,
        content: this.transform(pageFiles.content),
      } as PageFileList)),
      catchError(error => {
        this.store.error$.next(error.error);
        return of({} as PageFileList);
      })
    );
  }

  downloadFile(file: FileListItem): void {
    this.store.downloadFile(file.id)
      .pipe(take(1))
      .subscribe();
  }

  deleteFile(file: FileListItem): void {
    this.store.deleteFile(file.id).pipe(take(1)).subscribe();
  }

  uploadFile(target: any): void {
    if (!target) {
      return;
    }

    this.fileSizeOverLimitError$.next(false);
    this.store.error$.next(null);

    if (target?.files[0].size > this.maximumAllowedFileSizeInMB * 1024 * 1024) {
      setTimeout(() => this.fileSizeOverLimitError$.next(true), 10);
      return;
    }

    this.store.uploadFile(target?.files[0])
      .pipe(take(1))
      .subscribe();
  }

  private fileCategories(): Observable<CategoryNode> {
    return of({
          info: {type: FileTypeEnum.ContractSupport},
          name: {i18nKey: 'project.application.contract.and.supporting'},
          children: [
            {
              info: {type: FileTypeEnum.Contract},
              name: {i18nKey: 'project.application.contract.and.supporting.contracts'},
            },
            {
              info: {type: FileTypeEnum.ContractDoc},
              name: {i18nKey: 'project.application.contract.and.supporting.project'},
            },
          ],
    });
  }

  private transform(content: ProjectReportFileDTO[]): FileListItem[] {
    return content.map(file => ({
      ...file,
      deletable: this.isEditable,
      editable: false,
      tooltipIfNotDeletable: '',
      iconIfNotDeletable: 'delete',
    }));
  }
}
