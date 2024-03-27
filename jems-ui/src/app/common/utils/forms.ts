import {Observable} from 'rxjs';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {
  ConfirmDialogComponent
} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.data';

export class Forms {

  static confirmDialog(dialog: MatDialog, title: string, message: string, messageArguments?: any): Observable<boolean> {
    return this.confirmRef(
      dialog,
      {title, message: {i18nKey: message, i18nArguments: messageArguments}}
    ).afterClosed();
  }

  static confirm(dialog: MatDialog, data: ConfirmDialogData): Observable<boolean> {
    return this.confirmRef(
      dialog,
      data
    ).afterClosed();
  }

  static confirmRef(dialog: MatDialog, data: ConfirmDialogData): MatDialogRef<ConfirmDialogComponent> {
    return dialog.open(ConfirmDialogComponent, {
      autoFocus: false,
      maxWidth: '30rem',
      data
    });
  }
}
