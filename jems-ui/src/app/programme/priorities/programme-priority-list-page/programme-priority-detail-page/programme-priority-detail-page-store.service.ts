import {Injectable} from '@angular/core';
import {merge, Observable, of, Subject} from 'rxjs';
import {ProgrammePriorityAvailableSetupDTO, ProgrammePriorityDTO, ProgrammePriorityService} from '@cat/api';
import {startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {RoutingService} from '../../../../common/services/routing.service';

@Injectable()
export class ProgrammePriorityDetailPageStore {
  public static PRIORITIES_DETAIL_PATH = '/app/programme/priorities/';

  private savedPriority = new Subject<ProgrammePriorityDTO>();

  policies$: Observable<ProgrammePriorityAvailableSetupDTO>;
  priority$: Observable<ProgrammePriorityDTO | {}>;

  constructor(private priorityService: ProgrammePriorityService,
              private router: RoutingService) {
    this.policies$ = this.policies();
    this.priority$ = this.priority();
  }

  updatePriority(priorityId: number, priority: ProgrammePriorityDTO): Observable<ProgrammePriorityDTO> {
    return this.priorityService.update(priorityId, priority)
      .pipe(
        tap(saved => this.savedPriority.next(saved)),
        tap(saved => Log.info('Saved priority', saved)),
      );
  }

  createPriority(priority: ProgrammePriorityDTO): Observable<ProgrammePriorityDTO> {
    return this.priorityService.create(priority)
      .pipe(
        tap(created => Log.info('Created priority', created)),
      );
  }

  private priority(): Observable<ProgrammePriorityDTO | {}> {
    const initialPriority = this.router.routeParameterChanges(ProgrammePriorityDetailPageStore.PRIORITIES_DETAIL_PATH, 'priorityId')
      .pipe(
        switchMap(priorityId => priorityId ? this.priorityService.getById(Number(priorityId)) : of({})),
        tap(priority => Log.info('Fetched the programme priority:', this, priority)),
      );

    return merge(initialPriority, this.savedPriority);
  }

  private policies(): Observable<ProgrammePriorityAvailableSetupDTO> {
    return this.savedPriority
      .pipe(
        startWith(null),
        switchMap(() => this.priorityService.getAvailableSetup()),
        tap(setup => Log.info('Fetched the programme priority setup:', this, setup)),
      );
  }
}
