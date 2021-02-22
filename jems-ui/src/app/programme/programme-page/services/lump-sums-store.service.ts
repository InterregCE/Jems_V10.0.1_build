import {Injectable} from '@angular/core';
import {InputTranslation, ProgrammeLumpSumDTO} from '@cat/api';
import {Observable, ReplaySubject} from 'rxjs';

@Injectable()
export class LumpSumsStore {
  private savedLumpSum$ = new ReplaySubject<InputTranslation[] | null>(1);

  savedLumpSum(lumpSum: ProgrammeLumpSumDTO): void {
    this.savedLumpSum$.next(lumpSum?.name);
    setTimeout(() => this.savedLumpSum$.next(null), 4000);
  }

  lumpSum(): Observable<InputTranslation[] | null> {
    return this.savedLumpSum$.asObservable();
  }
}
