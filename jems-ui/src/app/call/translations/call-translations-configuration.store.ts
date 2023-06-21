import { Injectable } from '@angular/core';
import { CallTranslationConfigurationService, CallTranslationFileDTO } from '@cat/api';
import {switchMap, tap} from 'rxjs/operators';
import { UntilDestroy } from '@ngneat/until-destroy';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { CallStore } from '../services/call-store.service';

@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class CallTranslationsConfigurationStore {

  callId$: Observable<number>;
  translationsConfiguration$: Observable<CallTranslationFileDTO[]>;
  refresh$ = new BehaviorSubject(true);

  constructor(
    private readonly callStore: CallStore,
    private readonly callTranslationService: CallTranslationConfigurationService,
  ) {
    this.callId$ = callStore.callId$;
    this.translationsConfiguration$ = this.translationsConfiguration();
  }

  private translationsConfiguration(): Observable<CallTranslationFileDTO[]> {
    return combineLatest([
      this.callStore.callId$,
      this.refresh$,
    ]).pipe(
      switchMap(([callId, _]) => this.callTranslationService.getTranslation(callId)),
    );
  }

  upload(file: Blob, callId: number, language: string): Observable<CallTranslationFileDTO> {
    return this.callTranslationService.uploadTranslationFileForm(file, callId, language)
      .pipe(tap(() => this.refresh$.next(true)));
  }

  delete(callId: number, language: string): Observable<any> {
    return this.callTranslationService.deleteTranslationFile(callId, language)
      .pipe(tap(() => this.refresh$.next(true)));
  }

}
