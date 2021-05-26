import {Observable} from 'rxjs';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDialogComponent} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {Alert} from '@common/components/forms/alert';

export class Forms {

  static confirmDialog(dialog: MatDialog, title: string, message: string, messageArguments?: any, hasProminentText?: boolean, prominentText?: string, prominentTextArguments?: any, type?: Alert): Observable<boolean> {
    const dialogRef = dialog.open(ConfirmDialogComponent, {
      autoFocus: false,
      maxWidth: '30rem',
      data: {title, message, arguments: messageArguments, hasProminentText, prominentText, prominentTextArguments, type}
    });
    return dialogRef.afterClosed();
  }
}
