import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Component, OnInit} from '@angular/core';
import {ContractingFilesStoreService} from '@project/project-application/services/contracting-files-store.service';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, Observable, of, Subject} from 'rxjs';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {I18nMessage} from '@common/models/I18nMessage';
import {PageFileList} from '@common/components/file-list/page-file-list';
import {Alert} from '@common/components/forms/alert';
import {ContractPartnerStore} from '@project/project-application/contract-partner/contract-partner.store';
import {
  ProjectPartnerSummaryDTO,
  ProjectReportFileDTO
} from '@cat/api';
import {catchError, map, take, tap, withLatestFrom} from 'rxjs/operators';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
import FileTypeEnum = ProjectReportFileDTO.TypeEnum;
import {FileListItem} from '@common/components/file-list/file-list-item';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';


@UntilDestroy()
@Component({
  selector: 'jems-partner-files',
  templateUrl: './partner-files.component.html',
  styleUrls: ['./partner-files.component.scss']
})
export class PartnerFilesComponent implements OnInit {
  Alert = Alert;
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();

  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  selectedCategoryPath$: Observable<I18nMessage[]>;
  files$: Observable<PageFileList>;
  fileCategories$: Observable<CategoryNode>;
  data$: Observable<{
    canEdit: boolean;
    canView: boolean;
  }>;

  constructor(
    public contractingFilesStoreService: ContractingFilesStoreService,
    private partnerStore: ProjectPartnerStore,
    private permissionService: PermissionService,
    private projectStore: ProjectStore,
    private contractPartnerStore: ContractPartnerStore
  ) {
    this.selectedCategoryPath$ = contractingFilesStoreService.selectedCategoryPath$;
    this.contractingFilesStoreService.getMaximumAllowedFileSize()
      .pipe(untilDestroyed(this))
      .subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
    this.files$ = this.getFilesToList();
    this.data$ = combineLatest([
      this.contractPartnerStore.userCanEditContractPartner$,
      this.contractPartnerStore.userCanViewContractPartner$,
    ]).pipe(
      map(([canEdit, canView]) => ({canEdit, canView})),
    );
    this.fileCategories$ = combineLatest(
      [
        this.partnerStore.partnerReportSummaries$,
        this.contractPartnerStore.partnerId$,
      ]
    ).pipe(
      map(([partners, partnerId]) => (this.setFileCategory(partners, Number(partnerId)))),
      tap(filters => this.contractingFilesStoreService.setParent(filters)),
    );
  }

  ngOnInit(): void {
    this.contractingFilesStoreService.setFileCategories(this.fileCategories$);
    this.contractingFilesStoreService.setSection({type: FileTypeEnum.ContractPartnerDoc} as CategoryInfo);
  }

  private setFileCategory(partners:  ProjectPartnerSummaryDTO[], partnerId: number): CategoryNode {
    return partners.some(p => p.id === partnerId) ? this.generatePartnerNode(partners.filter(p=> p.id === partnerId)[0]) : {};
  }

  private generatePartnerNode(partner: ProjectPartnerSummaryDTO) {
    return {
        info: {type: FileTypeEnum.ContractPartnerDoc, id: partner.id},
        name: {
          i18nKey: `common.label.project.partner.role.shortcut.${partner.role}`,
          i18nArguments: {partner: `${partner.sortNumber || ''} ${partner.abbreviation}`}
        }
      };
  }

  downloadFile(file: FileListItem): void {
    this.contractingFilesStoreService.downloadFile(file.id).pipe(take(1)).subscribe();
  }

  deleteFile(file: FileListItem): void {
    this.contractingFilesStoreService.deleteFile(file.id).pipe(take(1)).subscribe();
  }

  uploadFile(target: any): void {
    if (!target) {
      return;
    }

    this.fileSizeOverLimitError$.next(false);
    this.contractingFilesStoreService.error$.next(null);

    if (target?.files[0].size > this.maximumAllowedFileSizeInMB * 1024 * 1024) {
      setTimeout(() => this.fileSizeOverLimitError$.next(true), 10);
      return;
    }

    this.contractingFilesStoreService.uploadFile(target?.files[0])
      .pipe(take(1))
      .subscribe();
  }

  updateDescription(data: FileDescriptionChange) {
    this.contractingFilesStoreService.setFileDescription(data.id, data.description).pipe(
      tap(() => this.contractingFilesStoreService.filesChanged$.next()),
      untilDestroyed(this)
    ).subscribe();
  }

  private getFilesToList(): Observable<PageFileList> {
    return this.contractingFilesStoreService.fileList$.pipe(
      withLatestFrom(this.contractPartnerStore.userCanEditContractPartner$),
      map(([pageFiles, canEdit]) => ({
        ...pageFiles,
        content: this.transform(pageFiles.content, canEdit),
      } as PageFileList)),
      catchError(error => {
        this.contractingFilesStoreService.error$.next(error.error);
        return of({} as PageFileList);
      })
    );
  }

  private transform(content: ProjectReportFileDTO[], isEditable: boolean): FileListItem[] {
    return content.map(file => ({
      id: file.id,
      name: file.name,
      type: file.type,
      uploaded: file.uploaded,
      author: file.author,
      sizeString: file.sizeString,
      description: file.description,
      editable: isEditable && file.type === FileTypeEnum.ContractPartnerDoc,
      deletable: isEditable && file.type === FileTypeEnum.ContractPartnerDoc,
      tooltipIfNotDeletable: 'file.table.action.delete.disabled.for.tab.tooltip',
      iconIfNotDeletable: 'delete_forever',
    }));
  }

}
