import {Observable} from 'rxjs';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDialogComponent} from '@common/components/modals/confirm-dialog/confirm-dialog.component';

export class Forms {

  static confirmDialog(dialog: MatDialog, title: string, message: string): Observable<boolean> {
    const dialogRef = dialog.open(ConfirmDialogComponent, {
      maxWidth: '30rem',
      data: {title, message}
    });
    return dialogRef.afterClosed();
  }
}
