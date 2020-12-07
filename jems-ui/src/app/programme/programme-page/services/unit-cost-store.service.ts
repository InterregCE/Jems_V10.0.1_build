import {Injectable} from '@angular/core';
import {ProgrammeUnitCostDTO} from '@cat/api';
import {Observable, ReplaySubject} from 'rxjs';

@Injectable()
export class UnitCostStore {
  private savedUnitCost$ = new ReplaySubject<string | null>(1);

  savedUnitCost(unitCost: ProgrammeUnitCostDTO): void {
    this.savedUnitCost$.next(unitCost?.name);
    setTimeout(() => this.savedUnitCost$.next(null), 4000);
  }

  unitCost(): Observable<string | null> {
    return this.savedUnitCost$.asObservable();
  }
}
