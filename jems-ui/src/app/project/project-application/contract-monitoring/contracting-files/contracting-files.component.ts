import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
import {I18nMessage} from '@common/models/I18nMessage';
import {ContractingFilesStoreService} from '@project/project-application/services/contracting-files-store.service';
import {ProjectReportFileDTO} from '@cat/api';
import {map, take, tap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {Alert} from '@common/components/forms/alert';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {FileListItem} from '@common/components/file-list/file-list-item';
import FileTypeEnum = ProjectReportFileDTO.TypeEnum;

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

  constructor(
    public store: ContractingFilesStoreService,
    private partnerStore: ProjectPartnerStore,
    private dialog: MatDialog,
  ) {
    this.selectedCategoryPath$ = store.selectedCategoryPath$;
    this.store.getMaximumAllowedFileSize()
      .pipe(untilDestroyed(this))
      .subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
  }

  ngOnInit(): void {
    this.store.setFileCategories(this.fileCategories());
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


  private fileCategories(): Observable<CategoryNode> {
    return this.partnerStore.partnerReportSummaries$.pipe(
      map(partners => ({
        info: { type: FileTypeEnum.Contracting },
        name: { i18nKey: 'project.application.contracting.title' },
        parent: undefined,
        children: [
          {
            info: { type: FileTypeEnum.ContractSupport },
            name: { i18nKey: 'project.application.contract.and.supporting' },
            children: [
              {
                info: { type: FileTypeEnum.Contract },
                name: { i18nKey: 'project.application.contract.and.supporting.contracts' },
              },
              {
                info: { type: FileTypeEnum.ContractDoc },
                name: { i18nKey: 'project.application.contract.and.supporting.project' },
              },
            ],
          },
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
      })),
      tap(filters => this.store.setParent(filters)),
    );
  }


}
