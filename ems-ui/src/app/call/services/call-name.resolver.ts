import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {CallStore} from './call-store.service';
import {map, take} from 'rxjs/operators';

@Injectable()
export class CallNameResolver implements Resolve<string> {

  constructor(private callStore: CallStore) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<string> {
    this.callStore.init(route.params.callId);
    return this.callStore
      .getCallById()
      .pipe(
        map(call => call.name),
        take(1)
      );
  }

}
