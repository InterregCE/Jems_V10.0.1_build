import {Observable} from 'rxjs';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDialogComponent} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {FormControl, FormGroup, ValidatorFn} from '@angular/forms';

export class Forms {

  static confirmDialog(dialog: MatDialog, title: string, message: string, messageArguments?: any): Observable<boolean> {
    const dialogRef = dialog.open(ConfirmDialogComponent, {
      maxWidth: '30rem',
      data: {title, message, arguments: messageArguments}
    });
    return dialogRef.afterClosed();
  }

  static toFormGroup(items: Map<string, ValidatorFn[]>): FormGroup {
    const group: any = {};

    items.forEach((value, item) => {
      group[item] = new FormControl('', value);
    })
    return new FormGroup(group);
  }
}
