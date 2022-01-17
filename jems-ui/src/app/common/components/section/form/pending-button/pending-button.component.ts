import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '../../../../utils/forms';
import {take} from 'rxjs/internal/operators';
import {tap} from 'rxjs/operators';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.data';

@Component({
  selector: 'app-pending-button',
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
