import {Injectable} from '@angular/core';
import {
  CallDetailDTO,
  CallService,
  FlatRateSetupDTO,
  PreSubmissionPluginsDTO,
  ProgrammeCostOptionService,
  ProgrammeLumpSumListDTO,
  ProgrammeUnitCostListDTO,
  UserRoleCreateDTO
} from '@cat/api';
import {BehaviorSubject, merge, Observable, of, Subject} from 'rxjs';
import {map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {PermissionService} from '../../security/permissions/permission.service';
import {RoutingService} from '@common/services/routing.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Injectable({
  providedIn: 'root'
})
export class CallStore {
  public static CALL_DETAIL_PATH = '/app/call/detail';
  callId$: Observable<number>;
  call$: Observable<CallDetailDTO>;
  unitCosts$: Observable<ProgrammeUnitCostListDTO[]>;
  lumpSums$: Observable<ProgrammeLumpSumListDTO[]>;
  userCanApply$: Observable<boolean>;
  callIsReadable$: Observable<boolean>;
  callIsEditable$: Observable<boolean>;
  callIsPublished$: Observable<boolean>;

  savedCall$ = new Subject<CallDetailDTO>();
  callType$ = new BehaviorSubject<CallDetailDTO.TypeEnum>(CallDetailDTO.TypeEnum.STANDARD);

  constructor(private callService: CallService,
              private programmeCostOptionService: ProgrammeCostOptionService,
              private permissionService: PermissionService,
              private router: RoutingService) {
    this.callId$ = this.callId();
    this.call$ = this.call();
    this.unitCosts$ = this.unitCosts();
    this.lumpSums$ = this.lumpSums();
    this.userCanApply$ = this.permissionService.hasPermission(PermissionsEnum.ProjectCreate);
    this.callIsEditable$ = this.permissionService.hasPermission(PermissionsEnum.CallUpdate);
    this.callIsReadable$ = this.permissionService.hasPermission(PermissionsEnum.CallRetrieve);
    this.callIsPublished$ = this.callIsPublished();
  }

  saveFlatRates(flatRates: FlatRateSetupDTO): Observable<CallDetailDTO> {
    return this.callId$.pipe(
      switchMap(callId => this.callService.updateCallFlatRateSetup(callId, flatRates)),
      tap(saved => this.savedCall$.next(saved)),
      tap(saved => Log.info('Updated call flat rates:', this, saved))
    );
  }

  saveLumpSums(lumpSumIds: number[]): Observable<CallDetailDTO> {
    return this.callId$.pipe(
      switchMap(callId => this.callService.updateCallLumpSums(callId, lumpSumIds)),
      tap(saved => this.savedCall$.next(saved)),
      tap(saved => Log.info('Updated call lump sums:', this, saved))
    );
  }

  saveUnitCosts(unitCostIds: number[]): Observable<CallDetailDTO> {
    return this.callId$.pipe(
      switchMap(callId => this.callService.updateCallUnitCosts(callId, unitCostIds)),
      tap(saved => this.savedCall$.next(saved)),
      tap(saved => Log.info('Updated call unit costs:', this, saved))
    );
  }

  savePreSubmissionCheckSettings(pluginKeys: PreSubmissionPluginsDTO): Observable<CallDetailDTO> {
    return this.callId$.pipe(
      switchMap(callId => this.callService.updatePreSubmissionCheckSettings(callId, pluginKeys)),
      tap(saved => this.savedCall$.next(saved)),
      tap(saved => Log.info('Updated call pre-submission check settings:', this, saved))
    );
  }

  isSPFCall(): Observable<boolean> {
    return this.callType$.asObservable().pipe(
      map(callType => callType === CallDetailDTO.TypeEnum.SPF)
    );
  }

  private callId(): Observable<number> {
    return this.router.routeParameterChanges(CallStore.CALL_DETAIL_PATH, 'callId')
      .pipe(map(Number));
  }

  private call(): Observable<CallDetailDTO> {
    const initialCall$ = this.callId$
      .pipe(
        switchMap(id => id ? this.callService.getCallById(Number(id)) : of({} as CallDetailDTO)),
        tap(call => {
          if (call.type) {
            this.callType$.next(call.type);
          }
        }),
        tap(call => Log.info('Fetched the call:', this, call)),
      );
    return merge(initialCall$, this.savedCall$)
      .pipe(
        shareReplay(1)
      );
  }

  private unitCosts(): Observable<ProgrammeUnitCostListDTO[]> {
    return this.programmeCostOptionService.getProgrammeUnitCosts()
      .pipe(
        tap(list => Log.info('Fetched the Unit Costs:', this, list)),
      );
  }

  private lumpSums(): Observable<ProgrammeLumpSumListDTO[]> {
    return this.programmeCostOptionService.getProgrammeLumpSums()
      .pipe(
        tap(list => Log.info('Fetched the Lump Sums:', this, list))
      );
  }

  private callIsPublished(): Observable<boolean> {
    return this.call$
      .pipe(
        map(call => call?.status === CallDetailDTO.StatusEnum.PUBLISHED)
      );
  }
}
