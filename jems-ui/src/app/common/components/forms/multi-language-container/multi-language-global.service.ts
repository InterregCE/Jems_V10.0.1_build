import {Injectable} from '@angular/core';
import {combineLatest, Observable, ReplaySubject} from 'rxjs';
import {tap} from 'rxjs/operators';
import {LanguageStore} from '../../../services/language-store.service';


@Injectable({
  providedIn: 'root'
})
export class MultiLanguageGlobalService {

  activeInputLanguage$: Observable<string>;
  activeSystemLanguage$: Observable<string>;

  private activeInputLanguageSubject = new ReplaySubject<string>(1);
  private activeSystemLanguageSubject = new ReplaySubject<string>(1);

  constructor(private languageStore: LanguageStore) {
    this.activeInputLanguage$ = this.activeInputLanguageSubject.asObservable();
    this.activeSystemLanguage$ = this.activeSystemLanguageSubject.asObservable();

    this.languageStore.inputLanguages$.pipe(
      tap(inputLanguages => this.setActiveInputLanguage(inputLanguages && inputLanguages[0]))
    ).subscribe();

    combineLatest([this.languageStore.systemLanguages$, this.languageStore.currentSystemLanguage$]).pipe(
      tap(([systemLanguages, currentLanguage]) => this.setActiveSystemLanguage(currentLanguage || systemLanguages && systemLanguages[0]))
    ).subscribe();
  }

  setActiveInputLanguage(newLanguage: string): void {
    this.activeInputLanguageSubject.next(newLanguage);
  }

  setActiveSystemLanguage(newLanguage: string): void {
    this.activeSystemLanguageSubject.next(newLanguage);
  }
}
