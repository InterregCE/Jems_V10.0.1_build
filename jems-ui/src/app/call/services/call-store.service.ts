import {Injectable} from '@angular/core';
import {CallService, FlatRateSetupDTO, InputCallCreate, InputCallUpdate, OutputCall, ProgrammeUnitCostListDTO, ProgrammeLumpSumListDTO, ProgrammeCostOptionService} from '@cat/api';
import {Observable, ReplaySubject} from 'rxjs';
import {tap} from 'rxjs/operators';
import {Log} from '../../common/utils/log';

@Injectable()
export class CallStore {
  public static CALL_DETAIL_PATH = '/app/call/detail';
  private callId: number;
  call$ = new ReplaySubject<OutputCall | any>(1);
  unitCosts$ = new ReplaySubject<ProgrammeUnitCostListDTO[] | any>(1);
  lumpSums$ = new ReplaySubject<ProgrammeLumpSumListDTO[] | any>(1);

  constructor(private callService: CallService,
              private programmeCostOptionService: ProgrammeCostOptionService) {
  }

  init(callId: number | string): void {
    if (callId && callId === this.callId) {
      return;
    }
    if (!callId) {
      this.call$.next({});
      this.unitCosts$.next([]);
      this.lumpSums$.next([]);
      return;
    }
    this.callId = Number(callId);
    this.callService.getCallById(this.callId)
      .pipe(
        tap(call => Log.info('Fetched project call:', this, call)),
        tap(call => this.call$.next(call)),
      ).subscribe();

    this.programmeCostOptionService.getProgrammeUnitCosts()
      .pipe(
        tap(list => Log.info('Fetched the Unit Costs:', this, list)),
        tap(list => this.unitCosts$.next(list)),
      ).subscribe();

    this.programmeCostOptionService.getProgrammeLumpSums()
      .pipe(
        tap(list => Log.info('Fetched the Lump Sums:', this, list)),
        tap(list => this.lumpSums$.next(list)),
      ).subscribe();
  }

  saveCall(call: InputCallUpdate): Observable<OutputCall> {
    return this.callService.updateCall(call)
      .pipe(
        tap(saved => this.call$.next(saved)),
        tap(saved => Log.info('Updated call:', this, saved))
      );
  }

  createCall(call: InputCallCreate): Observable<OutputCall> {
    return this.callService.createCall(call)
      .pipe(
        tap(created => this.call$.next(created)),
        tap(created => Log.info('Created call:', this, created)),
      );
  }

  publishCall(callId: number): Observable<OutputCall> {
    return this.callService.publishCall(callId)
      .pipe(
        tap(saved => this.call$.next(saved)),
        tap(saved => Log.info('Published call:', this, saved)),
      );
  }

  saveFlatRates(flatRates: FlatRateSetupDTO): Observable<OutputCall> {
    return this.callService.updateCallFlatRateSetup(this.callId, flatRates)
      .pipe(
        tap(saved => Log.info('Updated call flat rates:', this, saved))
      );
  }

  saveLumpSums(lumpSumIds: number[]): Observable<OutputCall> {
    return this.callService.updateCallLumpSums(this.callId, lumpSumIds)
      .pipe(
        tap(saved => Log.info('Updated call lump sums:', this, saved))
      );
  }

  saveUnitCosts(unitCostIds: number[]): Observable<OutputCall> {
    return this.callService.updateCallUnitCosts(this.callId, unitCostIds)
      .pipe(
        tap(saved => Log.info('Updated call unit costs:', this, saved))
      );
  }
}
