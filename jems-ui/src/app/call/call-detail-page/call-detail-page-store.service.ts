import {Injectable} from '@angular/core';
import {Log} from '@common/utils/log';
import {Observable} from 'rxjs';
import {
  CallDetailDTO, CallDTO, CallService, CallUpdateRequestDTO,
  OutputProgrammeStrategy,
  ProgrammeFundDTO,
  ProgrammeFundService,
  ProgrammePriorityService,
  ProgrammeStrategyService,
} from '@cat/api';
import {CallPriorityCheckbox} from '../containers/model/call-priority-checkbox';
import {map, tap, withLatestFrom} from 'rxjs/operators';
import {PermissionService} from '../../security/permissions/permission.service';
import {ActivatedRoute} from '@angular/router';
import {CallStore} from '../services/call-store.service';
import {ProgrammeEditableStateStore} from '../../programme/programme-page/services/programme-editable-state-store.service';

@Injectable()
export class CallDetailPageStore {
  call$: Observable<CallDetailDTO>;
  userCanApply$: Observable<boolean>;
  allPriorities$: Observable<CallPriorityCheckbox[]>;
  allActiveStrategies$: Observable<OutputProgrammeStrategy[]>;
  allFunds$: Observable<ProgrammeFundDTO[]>;
  callIsReadable$: Observable<boolean>;
  callIsEditable$: Observable<boolean>;
  isFirstCall$: Observable<boolean>;
  callIsPublished$: Observable<boolean>;

  constructor(public callStore: CallStore,
              private activatedRoute: ActivatedRoute,
              private permissionService: PermissionService,
              private programmeEditableStateStore: ProgrammeEditableStateStore,
              private programmePriorityService: ProgrammePriorityService,
              private programmeStrategyService: ProgrammeStrategyService,
              private programmeFundService: ProgrammeFundService,
              private callService: CallService) {
    this.call$ = this.callStore.call$;
    this.userCanApply$ = this.callStore.userCanApply$;
    this.allPriorities$ = this.allPriorities();
    this.allActiveStrategies$ = this.allActiveStrategies();
    this.allFunds$ = this.allFunds();
    this.callIsReadable$ = this.callStore.callIsReadable$;
    this.callIsEditable$ = this.callStore.callIsEditable$;
    this.isFirstCall$ = this.isFirstCall();
    this.callIsPublished$ = this.callStore.callIsPublished$;
  }

  saveCall(call: CallUpdateRequestDTO): Observable<CallDetailDTO> {
    return this.callService.updateCall(call)
      .pipe(
        tap(saved => this.callStore.savedCall$.next(saved)),
        tap(saved => Log.info('Updated call:', this, saved))
      );
  }

  createCall(call: CallUpdateRequestDTO): Observable<CallDetailDTO> {
    return this.callService.createCall(call)
      .pipe(
        tap(created => this.callStore.savedCall$.next(created)),
        tap(created => Log.info('Created call:', this, created)),
      );
  }

  publishCall(callId: number): Observable<CallDTO> {
    return this.callService.publishCall(callId)
      .pipe(
        withLatestFrom(this.isFirstCall$),
        tap(([call, isFirstCall]) => {
          if (isFirstCall) {
            this.programmeEditableStateStore.firstCallPublished$.next();
          }
        }),
        map(([call, isFirstCall]) => call),
        tap(saved => Log.info('Published call:', this, saved)),
      );
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

  private isFirstCall(): Observable<boolean> {
    return this.programmeEditableStateStore.isProgrammeEditableDependingOnCall$
      .pipe(
        map(isProgrammeEditingLimited => !isProgrammeEditingLimited),
      );
  }

}
