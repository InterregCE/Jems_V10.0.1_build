import {Injectable} from '@angular/core';
import {Log} from 'src/app/common/utils/log';
import {combineLatest, Observable} from 'rxjs';
import {
  CallDetailDTO,
  OutputProgrammeStrategy,
  ProgrammeFundDTO,
  ProgrammeFundService,
  ProgrammePriorityService,
  ProgrammeStrategyService
} from '@cat/api';
import {CallPriorityCheckbox} from '../containers/model/call-priority-checkbox';
import {map, shareReplay, tap} from 'rxjs/operators';
import {PermissionService} from '../../security/permissions/permission.service';
import {ActivatedRoute} from '@angular/router';
import {CallStore} from '../services/call-store.service';
import {ProgrammeEditableStateStore} from '../../programme/programme-page/services/programme-editable-state-store.service';

@Injectable()
export class CallDetailPageStore {

  call$: Observable<CallDetailDTO>;
  isApplicant$: Observable<boolean>;
  allPriorities$: Observable<CallPriorityCheckbox[]>;
  allActiveStrategies$: Observable<OutputProgrammeStrategy[]>;
  allFunds$: Observable<ProgrammeFundDTO[]>;
  callIsEditable$: Observable<boolean>;
  isFirstCall$: Observable<boolean>;

  constructor(public callStore: CallStore,
              private activatedRoute: ActivatedRoute,
              private permissionService: PermissionService,
              private programmeEditableStateStore: ProgrammeEditableStateStore,
              private programmePriorityService: ProgrammePriorityService,
              private programmeStrategyService: ProgrammeStrategyService,
              private programmeFundService: ProgrammeFundService) {
    this.call$ = this.callStore.call$;
    this.isApplicant$ = this.callStore.isApplicant$;
    this.allPriorities$ = this.allPriorities();
    this.allActiveStrategies$ = this.allActiveStrategies();
    this.allFunds$ = this.allFunds();
    this.callIsEditable$ = this.callIsEditable();
    this.isFirstCall$ = this.isFirstCall();
  }

  private allPriorities(): Observable<CallPriorityCheckbox[]> {
    return this.programmePriorityService.get()
      .pipe(
        tap(priorities => Log.info('Fetched the priorities:', this, priorities)),
        map(priorities => priorities.map(priority => CallPriorityCheckbox.fromPriority(priority)))
      );
  }

  private allActiveStrategies(): Observable<OutputProgrammeStrategy[]> {
    return this.programmeStrategyService.getProgrammeStrategies()
      .pipe(
        tap(programmeStrategies => Log.info('Fetched programme strategies:', this, programmeStrategies))
      );
  }

  private allFunds(): Observable<ProgrammeFundDTO[]> {
    return this.programmeFundService.getProgrammeFundList()
      .pipe(
        tap(programmeFunds => Log.info('Fetched programme funds:', this, programmeFunds))
      );
  }

  private callIsEditable(): Observable<boolean> {
    return combineLatest([this.call$, this.isApplicant$])
      .pipe(
        map(([call, isApplicant]) => call?.status !== CallDetailDTO.StatusEnum.PUBLISHED && !isApplicant),
        shareReplay(1)
      );
  }

  private isFirstCall(): Observable<boolean> {
    return this.programmeEditableStateStore.isProgrammeEditableDependingOnCall$
      .pipe(
        map(isProgrammeEditingLimited => !isProgrammeEditingLimited),
      );
  }
}
