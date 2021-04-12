import {ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, Output} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '../../../../utils/forms';
import {take} from 'rxjs/internal/operators';
import {tap} from 'rxjs/operators';

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
  confirmTitle: string;
  @Input()
  confirmMessage: string;
  @Input()
  confirmArgs: string;

  @Output()
  clicked = new EventEmitter<void>();

  constructor(private dialog: MatDialog,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  click(): void {
    this.disabled = true;
    if (!this.confirmTitle) {
      this.clicked.emit();
      this.pending = true;
      this.changeDetectorRef.markForCheck();
      return;
    }
    Forms.confirmDialog(this.dialog, this.confirmTitle, this.confirmMessage, this.confirmArgs)
      .pipe(
        take(1),
        tap(confirmed => {
          if (confirmed) {
            this.clicked.emit();
            this.pending = true;
          } else {
            this.pending = false;
            this.disabled = false;
          }
          this.changeDetectorRef.markForCheck();
        })
      )
      .subscribe();
  }

}
