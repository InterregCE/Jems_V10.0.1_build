import {ActivatedRouteSnapshot, CanDeactivate, Route, RouterStateSnapshot, UrlTree} from '@angular/router';
import {inject, Injectable} from '@angular/core';
import {RoutingService} from '@common/services/routing.service';
import {Observable, of} from 'rxjs';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {ConfirmDialogComponent} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {Forms} from '@common/utils/forms';

@Injectable({providedIn: 'root'})
export class ConfirmLeaveGuard implements CanDeactivate<boolean> {

  private routingService = inject(RoutingService);
  private dialog = inject(MatDialog);

  dialogOpen = () => !!this.dialog.openDialogs.length;
  dialogRef: MatDialogRef<ConfirmDialogComponent>;

  public canDeactivate(component: boolean, currentRoute: ActivatedRouteSnapshot, currentState: RouterStateSnapshot, nextState?: RouterStateSnapshot): Observable<boolean> {
    const currentStateCreate = currentState?.url.includes("/create");
    const nextStateNoAuth = nextState?.url.includes("/no-auth");

    return currentStateCreate || nextStateNoAuth || this.canLeavePage()
      ? of(true)
      : this.confirmToLeaveResult();
  }

  public applyGuardToLeafRoutes(routes: Route[]): void {
    routes.forEach(route => {
      if (route.children) {
        this.applyGuardToLeafRoutes(route.children);
      } else {
        route.canDeactivate = [ConfirmLeaveGuard];
      }
    });
  }

  private canLeavePage(): boolean {
    return !this.routingService.confirmLeaveSet.size;
  }

  private confirmToLeaveResult(): Observable<boolean> {
    if (!this.dialogOpen())
      this.dialogRef = this.showDialog();

    return this.dialogRef.afterClosed();
  }

  private showDialog(): MatDialogRef<ConfirmDialogComponent> {
    return Forms.confirmRef(
      this.dialog,
      {
        title: 'common.sidebar.dialog.title',
        warnMessage: 'common.sidebar.dialog.message'
      }
    );
  }

}
