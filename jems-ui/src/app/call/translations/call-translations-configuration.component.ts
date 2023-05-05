import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Alert } from '@common/components/forms/alert';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { CallTranslationFileDTO, JemsFileMetadataDTO } from '@cat/api';
import { catchError, filter, map, take, tap } from 'rxjs/operators';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { MatTableDataSource } from '@angular/material/table';
import { CallTranslationsConfigurationStore } from './call-translations-configuration.store';
import { CallPageSidenavService } from '../services/call-page-sidenav.service';
import { DownloadService } from '@common/services/download.service';
import { AlertMessage } from '@common/components/file-list/file-list-table/alert-message';
import { FileListTableComponent } from '@common/components/file-list/file-list-table/file-list-table.component';
import { Forms } from '@common/utils/forms';
import { MatDialog } from '@angular/material/dialog';

@UntilDestroy()
@Component({
  selector: 'jems-call-translations-configuration',
  templateUrl: './call-translations-configuration.component.html',
  styleUrls: ['./call-translations-configuration.component.scss'],
  providers: [CallTranslationsConfigurationStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallTranslationsConfigurationComponent {

  displayedColumns = ['language', 'fileName', 'uploaded', 'actions'];
  dataSource: MatTableDataSource<CallTranslationFileDTO> = new MatTableDataSource([]);

  Alert = Alert;
  alerts$ = new BehaviorSubject<AlertMessage[]>([]);

  data$: Observable<{
    callId: number;
  }>;
  languageToUpload: CallTranslationFileDTO.LanguageEnum | null = null;

  constructor(
    private store: CallTranslationsConfigurationStore,
    private downloadService: DownloadService,
    private dialog: MatDialog,
    private callSidenavService: CallPageSidenavService,
  ) {
    this.data$ = combineLatest([
      store.callId$,
      store.translationsConfiguration$,
    ]).pipe(
      tap(([_, translationsConfiguration]) => this.dataSource.data = translationsConfiguration),
      map(([callId, _]) => ({ callId })),
      untilDestroyed(this),
    );
  }

  download(callId: number, file: JemsFileMetadataDTO) {
    this.downloadService.download(`/api/call/translation/${callId}/download/${file.id}`, file.name);
  }

  downloadFromProgramme(callId: number, language: CallTranslationFileDTO.LanguageEnum) {
    this.downloadService.download(`/api/translationFile/Application/${language}/`, `Application_${language.toLowerCase()}.properties`);
  }

  upload($event: any, callId: number): void {
    const file = $event.target.files[0];
    if (this.languageToUpload) {
      this.store.upload(file, callId, this.languageToUpload)
        .pipe(
          tap((fileUploaded) => this.showAlert(
            FileListTableComponent.successAlert('call.detail.file.uploaded.success', { file: fileUploaded.file.name })
          )),
          catchError(error => {
            const errorMsg = error?.error?.details?.[0]?.i18nMessage?.i18nKey;
            this.showAlert(
              FileListTableComponent.errorAlert(errorMsg ? errorMsg : 'file.delete.message.failed'));
            throw error;
          }),
          untilDestroyed(this),
        )
        .subscribe();
    }
  }

  delete(callId: number, translation: CallTranslationFileDTO): void {
    Forms.confirm(
      this.dialog, {
        title: translation.file.name,
        message: {i18nKey: 'file.dialog.message', i18nArguments: {name: translation.file.name}}
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        tap(() => this.performDeletion(callId, translation)),
      ).subscribe();
  }

  private performDeletion(callId: number, translation: CallTranslationFileDTO) {
    this.store.delete(callId, translation.language)
      .pipe(
        tap(() => this.showAlert(FileListTableComponent
          .successAlert('file.delete.message.successful', { fileName: translation.file.name }))),
        catchError(error => {
          this.showAlert(FileListTableComponent.errorAlert('file.delete.message.failed', {fileName: translation.file.name}));
          throw error;
        }),
        untilDestroyed(this),
      )
      .subscribe();
  }

  private showAlert(alert: AlertMessage) {
    this.alerts$.next([...this.alerts$.value, alert]);
    setTimeout(
      () => this.dismissAlert(alert.id),
      alert.type === Alert.SUCCESS ? 5000 : 30000);
  }

  dismissAlert(id: string) {
    const alerts = this.alerts$.value.filter(that => that.id !== id);
    this.alerts$.next(alerts);
  }

}
