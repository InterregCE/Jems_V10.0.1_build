import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {map} from 'rxjs/operators';
import {PaymentsToProjectPageStore} from './payments-to-projects-page.store';

@Injectable()
export class PaymentsToProjectDetailBreadcrumbResolver implements Resolve<Observable<number>> {

  constructor(private paymentsToProjectPageStore: PaymentsToProjectPageStore) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<number>> {
    return of(this.paymentsToProjectPageStore.payment$.pipe(
      map(payment => payment.id)
    ));
  }
}
