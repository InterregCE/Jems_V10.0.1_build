import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import { Alert } from '@common/components/forms/alert';

@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.scss']
})
export class ConfirmDialogComponent {
  Alert = Alert;

  title: string;
  message: string;
  arguments?: any;
  hasProminentText?: boolean;
  prominentText: string;
  prominentTextArguments?: any;
  type?: Alert;

  constructor(public dialogRef: MatDialogRef<ConfirmDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData) {
    this.title = data.title;
    this.message = data.message;
    this.arguments = data.arguments;
    this.hasProminentText = data.hasProminentText;
    this.prominentText = data.prominentText === undefined ? '' : data.prominentText;
    this.prominentTextArguments = data.prominentTextArguments;
    this.type = data.type;
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }

  onDismiss(): void {
    this.dialogRef.close(false);
  }
}

export class ConfirmDialogData {
  public title: string;
  public message: string;
  public arguments?: any;
  public hasProminentText?: boolean;
  public prominentText?: string;
  public prominentTextArguments?: any;
  public type?: Alert;
}
