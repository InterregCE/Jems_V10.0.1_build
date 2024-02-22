import {ActivatedRouteSnapshot, CanDeactivate, RouterStateSnapshot, UrlTree} from '@angular/router';
import {inject, Injectable} from '@angular/core';
import {RoutingService} from '@common/services/routing.service';
import {Observable, of} from 'rxjs';


@Injectable({providedIn: 'root'})
export class ConfirmLeaveGuard implements CanDeactivate<boolean> {

  routingService = inject(RoutingService);

  canDeactivate(component: boolean, currentRoute: ActivatedRouteSnapshot, currentState: RouterStateSnapshot, nextState?: RouterStateSnapshot): Observable<boolean> {
    // if the user is on a page with a dirty form, show a confirm to leave dialog
    return this.routingService.canLeavePage()
      ? of(true)
      : this.routingService.showConfirmToLeaveDialog();
  }

}
