import {Injectable} from '@angular/core';
import {ProgrammeLumpSumDTO} from '@cat/api';
import {Observable, ReplaySubject} from 'rxjs';

@Injectable()
export class LumpSumsStore {
  private savedLumpSum$ = new ReplaySubject<string | null>(1);

  savedLumpSum(lumpSum: ProgrammeLumpSumDTO): void {
    this.savedLumpSum$.next(lumpSum?.name);
    setTimeout(() => this.savedLumpSum$.next(null), 4000);
  }

  lumpSum(): Observable<string | null> {
    return this.savedLumpSum$.asObservable();
  }
}
