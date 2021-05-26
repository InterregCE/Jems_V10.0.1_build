import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '../../../../utils/forms';
import {take} from 'rxjs/internal/operators';
import {tap} from 'rxjs/operators';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.component';

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
    Forms.confirmDialog(this.dialog, this.confirm.title, this.confirm.message, this.confirm.arguments, this.confirm.hasProminentText, this.confirm.prominentText, this.confirm.prominentTextArguments, this.confirm.type)
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
