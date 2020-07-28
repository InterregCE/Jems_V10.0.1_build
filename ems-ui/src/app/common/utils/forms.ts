import {Observable} from 'rxjs';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDialogComponent} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {FormControl, FormGroup, ValidatorFn} from '@angular/forms';

export class Forms {

  static confirmDialog(dialog: MatDialog, title: string, message: string): Observable<boolean> {
    const dialogRef = dialog.open(ConfirmDialogComponent, {
      maxWidth: '30rem',
      data: {title, message}
    });
    return dialogRef.afterClosed();
  }

  static toFormGroup(items: { [key: string]: Array<ValidatorFn> }): FormGroup {
    const group: any = {};

    Object.keys(items).forEach(item => {
      group[item] = new FormControl('', items[item]);
    })
    return new FormGroup(group);
  }
}
