import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';
import {take, tap} from 'rxjs/operators';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.data';

@Component({
  selector: 'jems-pending-button',
  templateUrl: './pending-button.component.html',
  styleUrls: ['./pending-button.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PendingButtonComponent {

  @Input()
  icon: string;
  @Input()
  pending = false;
  @Input()
  disabled = false;
  @Input()
  confirm: ConfirmDialogData;
  @Input()
  type: 'primary' | 'secondary' = 'primary';

  @Output()
  clicked = new EventEmitter<void>();

  constructor(private dialog: MatDialog) {
  }

  click(): void {
    if (!this.confirm) {
      this.clicked.emit();
      return;
    }
    Forms.confirm(this.dialog, this.confirm)
      .pipe(
        take(1),
        tap(confirmed => {
          if (confirmed) {
            this.clicked.emit();
          }
        })
      ).subscribe();
  }

}
