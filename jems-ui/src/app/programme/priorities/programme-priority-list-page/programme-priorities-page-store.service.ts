import {Injectable} from '@angular/core';
import {ProgrammePriorityDTO, ProgrammePriorityService} from '@cat/api';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';

@Injectable()
export class ProgrammePrioritiesPageStore {

  priorities$: Observable<ProgrammePriorityDTO[]>;

  constructor(private programmePriorityService: ProgrammePriorityService) {
    this.priorities$ = this.priorities();
  }

  private priorities(): Observable<ProgrammePriorityDTO[]> {
    return this.programmePriorityService.get()
      .pipe(
        tap(priorities => Log.info('Fetched the priorities:', this, priorities)),
      );
  }
}
