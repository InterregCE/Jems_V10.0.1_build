import {Observable} from 'rxjs';
import {MatDialog} from '@angular/material/dialog';
import {
  ConfirmDialogComponent,
  ConfirmDialogData
} from '@common/components/modals/confirm-dialog/confirm-dialog.component';

export class Forms {

  static confirmDialog(dialog: MatDialog, title: string, message: string, messageArguments?: any): Observable<boolean> {
    const dialogRef = dialog.open(ConfirmDialogComponent, {
      autoFocus: false,
      maxWidth: '30rem',
      data: {title, message: {i18nKey: message, i18nArguments: messageArguments}}
    });
    return dialogRef.afterClosed();
  }

  static confirm(dialog: MatDialog, data: ConfirmDialogData): Observable<boolean> {
    const dialogRef = dialog.open(ConfirmDialogComponent, {
      autoFocus: false,
      maxWidth: '30rem',
      data
    });
    return dialogRef.afterClosed();
  }
}
