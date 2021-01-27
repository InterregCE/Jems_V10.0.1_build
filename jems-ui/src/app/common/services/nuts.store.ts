import {OutputNuts, ProgrammeDataService} from '@cat/api';
import {Injectable} from '@angular/core';
import {shareReplay, take, tap} from 'rxjs/operators';
import {Log} from '../utils/log';
import {Observable} from 'rxjs';

@Injectable({providedIn: 'root'})
export class NutsStore {

  private nuts$ = this.programmeDataService.getNuts()
    .pipe(
      take(1),
      tap(nuts => Log.info('Fetched programme nuts', this, nuts)),
      shareReplay(1)
    );

  constructor(private programmeDataService: ProgrammeDataService) {
  }

  getNuts(): Observable<OutputNuts[]> {
    return this.nuts$;
  }

}
