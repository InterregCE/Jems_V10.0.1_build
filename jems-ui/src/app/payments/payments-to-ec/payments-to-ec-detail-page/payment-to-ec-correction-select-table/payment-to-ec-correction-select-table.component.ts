import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {Alert} from '@common/components/forms/alert';
import {MatDialog} from '@angular/material/dialog';
import {AbstractControl, FormArray, FormGroup} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {PaymentToEcInclusionRow} from './payment-to-ec-inlcusion-row';
import {MatTableDataSource} from '@angular/material/table';
import {filter, take, tap} from 'rxjs/operators';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {MatCheckbox} from '@angular/material/checkbox/checkbox';
import {Forms} from '@common/utils/forms';
import {PaymentToEcAmountUpdate} from './payment-to-ec-amount-update';
import {PaymentToEcRowSelected} from './paymnet-to-ec-row-selected';
import {MatSort} from '@angular/material/sort';
import {PaymentToEcCorrectionLinkingDTO} from "@cat/api";

@Component({
  selector: 'jems-payment-to-ec-correction-select-table',
  templateUrl: './payment-to-ec-correction-select-table.component.html',
  styleUrls: ['./payment-to-ec-correction-select-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class PaymentToEcCorrectionSelectTableComponent {
  @Input()
  displayedColumns: string[] = [];

  @Input()
  form: FormGroup;

  @Input()
  data: {
    ecId: number;
    isEditable: boolean;
    content: PaymentToEcInclusionRow[];
  };

  @Input()
  dataSource: MatTableDataSource<AbstractControl> = new MatTableDataSource([]);

  @Input()
  dialogTitle: string;

  @Input()
  dialogMessage: string;

  @Output()
  submitAmountChanged$: EventEmitter<PaymentToEcAmountUpdate> = new EventEmitter<PaymentToEcAmountUpdate>();

  @Output()
  selectionChanged$: EventEmitter<PaymentToEcRowSelected> = new EventEmitter<PaymentToEcRowSelected>();

  @Output()
  sortChanged$: EventEmitter<Partial<MatSort>> = new EventEmitter<Partial<MatSort>>();

  editedRowIndex: number | null = null;
  Alert = Alert;
  PaymentToEcCorrectionLinkingDTO = PaymentToEcCorrectionLinkingDTO;

  constructor(
    public formService: FormService,
    private confirmDialog: MatDialog,
  ) {
  }

  get array(): FormArray {
    return this.form.get('array') as FormArray;
  }

  editAmounts(rowIndex: number) {
    this.editedRowIndex = rowIndex;
  }

  resetAmounts(rowIndex: number, row: PaymentToEcInclusionRow) {
    this.array.at(rowIndex).patchValue({
      autoPublicContribution: row.autoPublicContribution,
      publicContribution: row.publicContribution,
      privateContribution: row.privateContribution,
      comment: ''
    });
    this.formService.setDirty(true);
  }

  submitAmountChanges(rowIndex: number, correctionId: number) {
    const dataToUpdate = {
      correctedPublicContribution: this.array.at(rowIndex).get('publicContribution')?.value,
      correctedAutoPublicContribution: this.array.at(rowIndex).get('autoPublicContribution')?.value,
      correctedPrivateContribution: this.array.at(rowIndex).get('privateContribution')?.value,
      comment: this.array.at(rowIndex).get('comment')?.value,
      correctionId: correctionId
    } as PaymentToEcAmountUpdate;
    this.submitAmountChanged$.emit(dataToUpdate);
    this.editedRowIndex = null;
  }

  discardChanges(rowIndex: number, unchangedRow: PaymentToEcInclusionRow) {
    this.array.at(rowIndex).patchValue({
      autoPublicContribution: unchangedRow.correctedAutoPublicContribution,
      publicContribution: unchangedRow.correctedPublicContribution,
      privateContribution: unchangedRow.correctedPrivateContribution,
      comment: unchangedRow.comment,
    });
    this.editedRowIndex = null;
    this.formService.setDirty(false);
  }

  selectionChanged(ecId: number, correctionId: number, checked: boolean, event: MatCheckboxChange): void {
    event.source.checked = checked;
    if (checked) {
      this.deselect(ecId, correctionId, event.source);
    } else {
      this.select(ecId, correctionId, event.source);
    }
  }

  private select(ecId: number, correctionId: number, checkbox: MatCheckbox) {
    const selection = {
      ecId: ecId,
      selected: true,
      correctionId: correctionId
    } as PaymentToEcRowSelected;

    Forms.confirm(
      this.confirmDialog,
      {
        title: this.dialogTitle,
        message: {
          i18nKey: this.dialogMessage
        },
      }).pipe(
      take(1),
      filter(Boolean),
      tap(() => this.selectionChanged$.emit(selection)),
      tap(() => checkbox.checked = true),
    ).subscribe();
  }

  private deselect(ecId: number, correctionId: number, checkbox: MatCheckbox) {
    const selection = {
      ecId: ecId,
      selected: false,
      correctionId: correctionId
    } as PaymentToEcRowSelected;

    Forms.confirm(
      this.confirmDialog,
      {
        title: this.dialogTitle,
        message: {
          i18nKey: this.dialogMessage
        },
      }).pipe(
      take(1),
      filter(Boolean),
      tap(() => this.selectionChanged$.emit(selection)),
      tap(() => checkbox.checked = false),
    ).subscribe();
  }
}
