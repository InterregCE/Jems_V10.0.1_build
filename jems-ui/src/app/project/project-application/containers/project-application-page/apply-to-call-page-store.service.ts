import {Injectable} from '@angular/core';
import {CallDetailDTO, CallService} from '@cat/api';
import {Observable} from 'rxjs';
import {filter, switchMap, tap} from 'rxjs/operators';
import {RoutingService} from '../../../../common/services/routing.service';
import {Log} from '../../../../common/utils/log';

@Injectable()
export class ApplyToCallPageStore {
  public static APPLY_TO_CALL_PATH = '/project/applyTo';

  call$: Observable<CallDetailDTO>;

  constructor(private callService: CallService,
              private router: RoutingService) {
    this.call$ = this.call();
  }

  private call(): Observable<CallDetailDTO> {
    return this.router.routeParameterChanges(ApplyToCallPageStore.APPLY_TO_CALL_PATH, 'callId')
      .pipe(
        filter(id => !!id),
        switchMap(id => this.callService.getCallById(Number(id))),
        tap(call => Log.info('Fetched the call:', this, call)),
      );
  }
}
