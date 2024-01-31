import {ChangeDetectionStrategy, Component, ElementRef, EventEmitter, Input, Output, ViewChild} from '@angular/core';
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
import {PaymentToEcCorrectionLinkingDTO} from '@cat/api';
import {
  AdvancePaymentsDetailPageConstants
} from '../../../advance-payments-page/advance-payments-detail-page/advance-payments-detail-page.constants';
import {ReplaySubject} from 'rxjs';

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
  selectDialogTitle: string;

  @Input()
  selectDialogMessage: string;

  @Input()
  unselectDialogTitle: string;

  @Input()
  unselectDialogMessage: string;

  @Input()
  discardChanges$: ReplaySubject<void>;

  @Output()
  submitAmountChanged$: EventEmitter<PaymentToEcAmountUpdate> = new EventEmitter<PaymentToEcAmountUpdate>();

  @Output()
  selectionChanged$: EventEmitter<PaymentToEcRowSelected> = new EventEmitter<PaymentToEcRowSelected>();

  @Output()
  sortChanged$: EventEmitter<Partial<MatSort>> = new EventEmitter<Partial<MatSort>>();

  @ViewChild('paymentToEcTable', {read: ElementRef}) private paymentToEcTable: ElementRef;

  constants = AdvancePaymentsDetailPageConstants;

  editedRowIndex: number | null = null;
  Alert = Alert;
  PaymentToEcCorrectionLinkingDTO = PaymentToEcCorrectionLinkingDTO;

  constructor(
    public formService: FormService,
    private confirmDialog: MatDialog,
  ) {
    this.formService.init(this.form);
  }

  get array(): FormArray {
    return this.form.get('array') as FormArray;
  }

  editAmounts(rowIndex: number) {
    this.editedRowIndex = rowIndex;
    setTimeout(() => {
      this.scrollToRight();
    });
  }

  scrollToRight(): void {
    this.paymentToEcTable.nativeElement.scrollLeft = this.paymentToEcTable.nativeElement.scrollWidth;
  }

  resetAmounts(rowIndex: number, row: PaymentToEcInclusionRow) {
    this.array.at(rowIndex).patchValue({
      totalEligibleWithoutArt94or95: row.totalEligibleWithoutArt94or95,
      unionContribution: row.unionContribution,
      fundAmount: row.amountApprovedPerFund,
      autoPublicContribution: row.autoPublicContribution,
      publicContribution: row.publicContribution,
      privateContribution: row.privateContribution
    });
    this.formService.setDirty(true);
  }

  submitAmountChanges(rowIndex: number, correctionId: number) {
    const dataToUpdate = {
      correctedPublicContribution: this.array.at(rowIndex).get('publicContribution')?.value,
      correctedAutoPublicContribution: this.array.at(rowIndex).get('autoPublicContribution')?.value,
      correctedPrivateContribution: this.array.at(rowIndex).get('privateContribution')?.value,
      correctedTotalEligibleWithoutArt94or95: this.array.at(rowIndex).get('totalEligibleWithoutArt94or95')?.value,
      correctedUnionContribution: this.array.at(rowIndex).get('unionContribution')?.value,
      correctedFundAmount: this.array.at(rowIndex).get('fundAmount')?.value,
      comment: this.array.at(rowIndex).get('comment')?.value,
      correctionId
    } as PaymentToEcAmountUpdate;
    this.submitAmountChanged$.emit(dataToUpdate);
    this.editedRowIndex = null;
    this.formService.setDirty(false);
  }

  discardChanges(rowIndex: number, unchangedRow: PaymentToEcInclusionRow) {
    this.array.at(rowIndex).patchValue({
      totalEligibleWithoutArt94or95: unchangedRow.correctedTotalEligibleWithoutArt94or95,
      unionContribution: unchangedRow.correctedUnionContribution,
      fundAmount: unchangedRow.correctedFundAmount,
      autoPublicContribution: unchangedRow.correctedAutoPublicContribution,
      publicContribution: unchangedRow.correctedPublicContribution,
      privateContribution: unchangedRow.correctedPrivateContribution,
      comment: unchangedRow.comment,
    });
    this.editedRowIndex = null;
    this.formService.setDirty(false);
    this.discardChanges$.next();
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
      ecId,
      selected: true,
      correctionId
    } as PaymentToEcRowSelected;

    Forms.confirm(
      this.confirmDialog,
      {
        title: this.selectDialogTitle,
        message: {
          i18nKey: this.selectDialogMessage
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
      ecId,
      selected: false,
      correctionId
    } as PaymentToEcRowSelected;

    Forms.confirm(
      this.confirmDialog,
      {
        title: this.unselectDialogTitle,
        message: {
          i18nKey: this.unselectDialogMessage
        },
      }).pipe(
      take(1),
      filter(Boolean),
      tap(() => this.selectionChanged$.emit(selection)),
      tap(() => checkbox.checked = false),
    ).subscribe();
  }

  canSave(): boolean {
    return this.form.valid;
  }

  changeUnionContribution(rowIndex: number) {
    const updatedUnionContribution = this.array.at(rowIndex).get('unionContribution')?.value;
    const totalCorrection = this.data.content[rowIndex].amountApprovedPerFund + this.data.content[rowIndex].partnerContribution;
    this.array.at(rowIndex).patchValue({totalEligibleWithoutArt94or95: totalCorrection - updatedUnionContribution});
  }
}
