import {Injectable} from '@angular/core';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {
  ProgrammeLanguageDTO,
  ProgrammeLanguageService,
  TranslationFileMetaDataDTO,
  TranslationFileService
} from '@cat/api';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {map, share, shareReplay, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {LanguageStore} from '@common/services/language-store.service';
import {TranslateService} from '@ngx-translate/core';
import FileTypeEnum = TranslationFileMetaDataDTO.FileTypeEnum;

@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class TranslationManagementStore {

  private uploadTranslationFileEvent$ = new BehaviorSubject<boolean>(true);
  private translationFileList$ = this.getTranslationFileList();

  systemTranslationFileList$ = this.getSystemTranslationFileList();
  applicationTranslationFileList$ = this.getApplicationTranslationFileList();
  systemLanguages$ = this.getLanguagesList();


  constructor(private translationFileService: TranslationFileService, private programmeLanguageService: ProgrammeLanguageService, private languageStore: LanguageStore, private translate: TranslateService) {
  }

  refreshTranslations(language: string): Observable<object> {
    return this.translate.reloadLang(language).pipe(
      tap(translations => this.translate.setTranslation(language, translations)),
      share()
    );
  }

  uploadTranslationFile(file: File, fileType: FileTypeEnum, language: string): Observable<TranslationFileMetaDataDTO> {
    return this.translationFileService.uploadForm(file, fileType, language).pipe(
      withLatestFrom(this.languageStore.currentSystemLanguage$),
      tap(() => this.uploadTranslationFileEvent$.next(true)),
      switchMap(([fileMetadata, currentSystemLanguage]) => currentSystemLanguage === language ? this.refreshTranslations(language).pipe(map(() => fileMetadata)) : of(fileMetadata)),
      untilDestroyed(this),
      share()
    );
  }

  private getLanguagesList(): Observable<ProgrammeLanguageDTO[]> {
    return this.programmeLanguageService.get().pipe(
      shareReplay(1)
    );
  }

  private getTranslationFileList(): Observable<TranslationFileMetaDataDTO[]> {
    return this.uploadTranslationFileEvent$.pipe(
      switchMap(() => this.translationFileService.get()),
      shareReplay(1)
    );
  }

  private getSystemTranslationFileList(): Observable<TranslationFileMetaDataDTO[]> {
    return this.translationFileList$.pipe(
      map(it => it.filter(item => item.fileType === FileTypeEnum.System)),
      shareReplay(1)
    );
  }

  private getApplicationTranslationFileList(): Observable<TranslationFileMetaDataDTO[]> {
    return this.translationFileList$.pipe(
      map(it => it.filter(item => item.fileType === FileTypeEnum.Application)),
      shareReplay(1)
    );
  }

}
