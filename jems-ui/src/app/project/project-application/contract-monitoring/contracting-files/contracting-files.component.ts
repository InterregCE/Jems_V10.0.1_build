import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';
import {I18nMessage} from '@common/models/I18nMessage';
import {ContractingFilesStore} from "@project/project-application/contract-monitoring/contracting-files/contracting-files.store";
import {ProjectReportFileDTO} from "@cat/api";
import {filter, switchMap, take} from "rxjs/operators";
import {FileListItem} from "@common/components/file-list/file-list-item";
import {MatDialog} from "@angular/material/dialog";
import {Forms} from "@common/utils/forms";
import {AcceptedFileTypesConstants} from "@project/common/components/file-management/accepted-file-types.constants";
import {Alert} from '@common/components/forms/alert';
import FileTypeEnum = ProjectReportFileDTO.TypeEnum;
import {UntilDestroy, untilDestroyed} from "@ngneat/until-destroy";

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
    public store: ContractingFilesStore,
    private dialog: MatDialog,
  ) {
    this.selectedCategoryPath$ = store.selectedCategoryPath$;
    this.store.getMaximumAllowedFileSize()
      .pipe(untilDestroyed(this))
      .subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
  }

  ngOnInit(): void {
    this.store.setSection({type: FileTypeEnum.Contracting} as CategoryInfo);
  }

  downloadFile(file: FileListItem): void {
    this.store.downloadFile(file.id)
      .pipe(take(1))
      .subscribe();
  }

  deleteFile(file: FileListItem): void {
    Forms.confirm(this.dialog, {
      title: file.name,
      message: { i18nKey: 'file.dialog.message', i18nArguments: { name: file.name } },
    }).pipe(
      take(1),
      filter(answer => !!answer),
      switchMap(() => this.store.deleteFile(file.id)),
    ).subscribe();
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

}
