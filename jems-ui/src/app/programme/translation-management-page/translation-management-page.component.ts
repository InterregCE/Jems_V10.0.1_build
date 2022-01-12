import {ChangeDetectionStrategy, Component, QueryList, ViewChildren} from '@angular/core';
import {TranslationManagementStore} from './translation-management-store.service';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {catchError, finalize, map} from 'rxjs/operators';
import {ProgrammeLanguageDTO, TranslationFileMetaDataDTO} from '@cat/api';
import {ProgrammePageSidenavService} from '../programme-page/services/programme-page-sidenav.service';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {APIError} from '@common/models/APIError';
import {Alert} from '@common/components/forms/alert';
import FileTypeEnum = TranslationFileMetaDataDTO.FileTypeEnum;
import {ProgrammeEditableStateStore} from '../programme-page/services/programme-editable-state-store.service';
import {DownloadService} from '@common/services/download.service';

@UntilDestroy()
@Component({
  selector: 'app-translation-management-page',
  templateUrl: './translation-management-page.component.html',
  styleUrls: ['./translation-management-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TranslationManagementPageComponent {

  Alert = Alert;
  FileTypeEnum = FileTypeEnum;
  displayedColumns: string[] = ['language', 'fileName', 'lastModified', 'action'];
  applicationTranslationFileDataSource = new MatTableDataSource<TranslationFileMetaDataDTO>();
  systemTranslationFileDataSource = new MatTableDataSource<TranslationFileMetaDataDTO>();
  isApplicationTranslationDownloadInProgress$ = new BehaviorSubject(false);
  isSystemTranslationDownloadInProgress$ = new BehaviorSubject(false);
  data$: Observable<{
    error: APIError | null;
    fileNameWarning: string | null;
    inProgressUploads: string[];
  }>;
  private inProgressUploads$ = new BehaviorSubject<string[]>([]);
  private error$ = new BehaviorSubject<APIError | null>(null);
  private fileNameWarning$ = new BehaviorSubject<string | null>(null);
  private toUploadFileMetaData: TranslationFileMetaDataDTO;

  @ViewChildren(MatSort) set sortList(content: QueryList<MatSort>) {
    if (content.first) {
      this.systemTranslationFileDataSource.sort = content.first;
    }
    if (content.last) {
      this.applicationTranslationFileDataSource.sort = content.last;
    }
  }

  constructor(public translationManagementStore: TranslationManagementStore,
              private programmePageSidenavService: ProgrammePageSidenavService,
              public programmeEditableStateStore: ProgrammeEditableStateStore,
              private downloadService: DownloadService
              ) {

    this.data$ = combineLatest([this.inProgressUploads$, this.error$, this.fileNameWarning$, this.translationManagementStore.systemLanguages$, this.translationManagementStore.applicationTranslationFileList$, this.translationManagementStore.systemTranslationFileList$]).pipe(
      map(([inProgressUploads, error, fileNameWarning, systemLanguages, applicationTranslationFiles, systemTranslationFiles]) => {
        this.systemTranslationFileDataSource.data = this.getDataSourceData(systemLanguages, systemTranslationFiles, FileTypeEnum.System);
        this.applicationTranslationFileDataSource.data = this.getDataSourceData(systemLanguages, applicationTranslationFiles, FileTypeEnum.Application);
        return {
          error,
          fileNameWarning,
          inProgressUploads
        };
      }),
    );
  }

  setFileMetaDataToUpload(fileType: FileTypeEnum, language: string): void {
    this.toUploadFileMetaData = {fileType, language} as TranslationFileMetaDataDTO;
  }

  uploadFile($event: any): void {
    this.error$.next(null);
    this.fileNameWarning$.next(null);
    const file = $event.target.files[0];
    const expectedFileName = this.getFileName(this.toUploadFileMetaData.fileType, this.toUploadFileMetaData.language);
    if (file.name.toLowerCase() !== expectedFileName.toLowerCase()) {
      this.fileNameWarning$.next(expectedFileName);
    } else {
      this.inProgressUploads$.next([...this.inProgressUploads$.value, expectedFileName]);
      this.translationManagementStore.uploadTranslationFile(file, this.toUploadFileMetaData.fileType, this.toUploadFileMetaData.language)
        .pipe(
          catchError(error => {
            this.error$.next(error.error);
            throw error;
          }),
          finalize(() => this.inProgressUploads$.value.splice(this.inProgressUploads$.value.indexOf(expectedFileName), 1)),
          untilDestroyed(this)
        ).subscribe();
    }
  }

  getDataSourceData(languages: ProgrammeLanguageDTO[], translationFileList: TranslationFileMetaDataDTO[], fileType: FileTypeEnum): TranslationFileMetaDataDTO[] {
    return languages.map(language => {
      return translationFileList.find(it => it.language === language.code) ||
        {
          language: language.code,
          fileType,
          lastModified: undefined
        } as unknown as TranslationFileMetaDataDTO;
    });
  }

  downloadDefaultTranslationFile(fileType: FileTypeEnum): void {
    if (fileType) {
      this.getInProgressSubjectByFileType(fileType).next(true);
      this.downloadService.download(`/api/translationFile/${fileType}`, 'default-translation.properties').pipe(
        finalize(() => this.getInProgressSubjectByFileType(fileType).next(false))
      ).subscribe();
    }
  }

  downloadTranslationFile(fileType: FileTypeEnum, language: string): void {
    if (fileType && language) {
      this.downloadService.download(`/api/translationFile/${fileType}/${language}/`, 'translation.properties');
    }
  }

  isLoading(inProgressUploads: string[], fileType: FileTypeEnum, language: string): boolean {
    return inProgressUploads.indexOf(this.getFileName(fileType, language)) >= 0;
  }

  getFileName(fileType: FileTypeEnum, language: string): string {
    return `${fileType}_${language.toLowerCase()}.properties`;
  }

  getInProgressSubjectByFileType(fileType: FileTypeEnum): BehaviorSubject<Boolean> {
    return fileType === FileTypeEnum.Application ? this.isApplicationTranslationDownloadInProgress$ : this.isSystemTranslationDownloadInProgress$;
  }
}
