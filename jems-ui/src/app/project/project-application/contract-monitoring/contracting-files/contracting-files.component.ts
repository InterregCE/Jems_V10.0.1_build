import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {combineLatest, Observable, of, Subject} from 'rxjs';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
import {I18nMessage} from '@common/models/I18nMessage';
import {ContractingFilesStoreService} from '@project/project-application/services/contracting-files-store.service';
import {ProjectPartnerSummaryDTO, ProjectReportFileDTO, UserRoleCreateDTO} from '@cat/api';
import {catchError, map, take, tap} from 'rxjs/operators';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {Alert} from '@common/components/forms/alert';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {PageFileList} from '@common/components/file-list/page-file-list';
import FileTypeEnum = ProjectReportFileDTO.TypeEnum;
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';

@UntilDestroy()
@Component({
  selector: 'jems-contracting-files',
  templateUrl: './contracting-files.component.html',
  styleUrls: ['./contracting-files.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ContractingFilesComponent implements OnInit{
  Alert = Alert;
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();

  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  selectedCategoryPath$: Observable<I18nMessage[]>;
  files$: Observable<PageFileList>;
  canEdit: boolean;

  private contractsFileCategories: CategoryNode[] = [];
  fileCategories$: Observable<CategoryNode>;

  constructor(
    public store: ContractingFilesStoreService,
    private partnerStore: ProjectPartnerStore,
    private permissionService: PermissionService
  ) {
    this.selectedCategoryPath$ = store.selectedCategoryPath$;
    this.store.getMaximumAllowedFileSize()
      .pipe(untilDestroyed(this))
      .subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
    this.files$ = this.getFilesToList();
    this.permissionService.hasPermission(PermissionsEnum.ProjectSetToContracted).pipe(
      tap(hasPermission => this.canEdit = hasPermission),
      untilDestroyed(this)
    ).subscribe();

    this.fileCategories$ = combineLatest(
      [
        this.partnerStore.partnerReportSummaries$,
        this.store.canSeeProjectContracts$
      ]
    ).pipe(
      tap(([partners, canSeeProjectContracts]) => {
        if (canSeeProjectContracts) {
          this.setContractCategories();
        }
      }),
      map(([partners, canSeeProjectContracts]) => (this.setFileCategories(partners, this.contractsFileCategories))),
      tap(filters => this.store.setParent(filters)),
    );

    this.store.canSeeProjectContracts$.pipe(
      tap(canSeeContracts => { if (canSeeContracts) { this.setContractCategories(); }}),
      untilDestroyed(this)
    ).subscribe();

  }

  ngOnInit(): void {
    this.store.setFileCategories(this.fileCategories$);
    this.store.setSection({type: FileTypeEnum.Contracting} as CategoryInfo);
  }

  downloadFile(file: FileListItem): void {
    this.store.downloadFile(file.id).pipe(take(1)).subscribe();
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

  updateDescription(data: FileDescriptionChange) {
    this.store.setFileDescription(data.id, data.description).pipe(
      tap(() => this.store.filesChanged$.next()),
      untilDestroyed(this)
    ).subscribe();
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

  private setFileCategories(partners:  ProjectPartnerSummaryDTO[], contractsFileCategories:  CategoryNode[]): CategoryNode {
    return {
      info: { type: FileTypeEnum.Contracting },
      name: { i18nKey: 'project.application.contracting.title' },
      parent: undefined,
      children: [
        ...contractsFileCategories,
        {
          info: { type: FileTypeEnum.ContractPartner },
          name: { i18nKey: 'project.application.contract.partner' },
          children: partners.map(partner => ({
            info: { type: FileTypeEnum.ContractPartnerDoc, id: partner.id },
            name: {
              i18nKey: `common.label.project.partner.role.shortcut.${partner.role}`,
              i18nArguments: {partner: `${partner.sortNumber || ''} ${partner.abbreviation}`}
            },
          })),
        },
        {
          info: { type: FileTypeEnum.ContractInternal },
          name: { i18nKey: 'project.application.contract.internal' },
        },
      ],
    };
  }

  private transform(content: ProjectReportFileDTO[]): FileListItem[] {
    return content.map(file => ({
      id: file.id,
      name: file.name,
      type: file.type,
      uploaded: file.uploaded,
      author: file.author,
      sizeString: file.sizeString,
      description: file.description,
      editableDescription: file.type === FileTypeEnum.ContractInternal,
      editable: this.canEdit,
      deletable: this.canEdit && file.type === FileTypeEnum.ContractInternal,
      tooltipIfNotDeletable: 'file.table.action.delete.disabled.for.tab.tooltip',
      iconIfNotDeletable: 'delete_forever',
    }));
  }


  private setContractCategories(){
    this.contractsFileCategories = [{
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
    }];
  }

}
