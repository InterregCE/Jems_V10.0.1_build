import {CanDeactivate, Route} from '@angular/router';
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
  dialogRef: MatDialogRef<ConfirmDialogComponent, any>;

  canDeactivate = () => this.canLeave();

  private canLeave(): Observable<boolean> {
    return this.canLeavePage()
      ? of(true)
      : this.confirmToLeaveResult();
  }

  public canLeavePage(): boolean {
    return !this.routingService.confirmLeaveSet.size;
  }

  public confirmToLeaveResult(): Observable<boolean> {
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

  applyGuardToLeafRoutes(routes: Route[]): void {
    routes.forEach(route => {
      if (route.children) {
        this.applyGuardToLeafRoutes(route.children);
      } else {
        route.canDeactivate = [ConfirmLeaveGuard];
      }
    });
  }

}
