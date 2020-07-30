import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';

@Injectable()
export class ProgrammeNavigationStateManagementService {
  private activeTab$ = new ReplaySubject<number>();

  getTab(): Observable<number> {
    return this.activeTab$.asObservable()
  }

  changeTab(index: number) {
    this.activeTab$.next(index);
  }
}
