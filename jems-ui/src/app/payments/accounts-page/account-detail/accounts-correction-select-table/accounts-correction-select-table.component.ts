import {Component, ElementRef, EventEmitter, Input, OnChanges, OnInit, Output, ViewChild} from '@angular/core';
import {PagePaymentAccountCorrectionLinkingDTO, PaymentAccountCorrectionLinkingDTO, PaymentAccountCorrectionLinkingUpdateDTO,} from '@cat/api';
import {Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {MatTableDataSource} from '@angular/material/table';
import {AbstractControl, FormArray, FormBuilder} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {MatCheckbox} from '@angular/material/checkbox/checkbox';
import {Forms} from '@common/utils/forms';
import {filter, take, tap} from 'rxjs/operators';
import {PaymentAccountCorrectionSelected} from './payment-account-correction-selected';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'jems-accounts-correction-select-table',
  templateUrl: './accounts-correction-select-table.component.html',
  styleUrls: ['./accounts-correction-select-table.component.scss'],
  providers: [FormService]
})
export class AccountsCorrectionSelectTableComponent implements OnChanges {

  MAX_VALUE = 999_999_999.99;
  ScenarioEnum = PaymentAccountCorrectionLinkingDTO.ScenarioEnum;


  @Input()
  data: {
    paymentAccountId: number;
    correctionLinking: PagePaymentAccountCorrectionLinkingDTO;
    isEditable: boolean;
  };

  @Input()
  newSort: Subject<Partial<MatSort>>;
  @Input()
  newSize: Subject<number>;
  @Input()
  newIndex: Subject<number>;

  @Output()
  selectionChanged$ = new EventEmitter<PaymentAccountCorrectionSelected>();
  @Output()
  submitPayment$ = new EventEmitter<{ correctionId: number; updateDto: PaymentAccountCorrectionLinkingUpdateDTO }>();

  form = this.formBuilder.group({
    correctionLinking: this.formBuilder.array([]),
  });

  displayedColumns: string[] = [];
  displayedColumnsAll = [
    'select',
    'projectId',
    'projectAcronym',
    'priorityAxis',
    'correctionNo',
    'scenario',
    'controllingBody',
    'totalEligible',
    'fundAmount',
    'partnerContribution',
    'publicContribution',
    'autoPublicContribution',
    'privateContribution',
    'comment',
    'correction'
  ];
  dataSource: MatTableDataSource<AbstractControl> = new MatTableDataSource([]);
  editedRowIndex: number | null = null;
  @ViewChild('correctionTable', {read: ElementRef}) private correctionTable: ElementRef;

  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
    private confirmDialog: MatDialog,
  ) {
    this.formService.init(this.form);
  }

  ngOnChanges(): void {
    this.initializeForm(this.data.correctionLinking.content);
    this.displayedColumns = this.data.isEditable ? this.displayedColumnsAll :
      this.displayedColumnsAll.filter(col => !['select', 'correction'].includes(col));
  }

  get correctionLinking(): FormArray {
    return this.form.get('correctionLinking') as FormArray;
  }

  correction(index: number): PaymentAccountCorrectionLinkingDTO {
    return this.data.correctionLinking.content[index];
  }

  private initializeForm(correctionLinks: PaymentAccountCorrectionLinkingDTO[]) {
    this.correctionLinking.clear();
    correctionLinks.forEach(e => this.addLink(e));
    this.formService.setEditable(true);
    this.formService.resetEditable();
    this.dataSource.data = [...this.correctionLinking.controls];
  }


  addLink(link: PaymentAccountCorrectionLinkingDTO) {
    const item = this.formBuilder.group({
      fundAmount: this.formBuilder.control(link.correctedFundAmount),
      autoPublicContribution: this.formBuilder.control(link.correctedAutoPublicContribution),
      publicContribution: this.formBuilder.control(link.correctedPublicContribution),
      privateContribution: this.formBuilder.control(link.correctedPrivateContribution),
      comment: this.formBuilder.control(link.comment) ?? '',
    });
    this.correctionLinking.push(item);
  }

  selectionChanged(paymentAccountId: number, correctionId: number, checked: boolean, event: MatCheckboxChange): void {
    event.source.checked = checked;
    const selection = {
      paymentAccountId,
      correctionId,
      selected: !checked,
    } as PaymentAccountCorrectionSelected;
    if (checked) {
      this.deselect(selection, event.source);
    } else {
      this.select(selection, event.source);
    }
  }

  private select(selection: PaymentAccountCorrectionSelected, checkbox: MatCheckbox) {
    Forms.confirm(
      this.confirmDialog,
      {
        title: 'payments.accounts.corrections.select.title',
        message: {
          i18nKey: 'payments.accounts.corrections.select.message'
        },
      }).pipe(
      take(1),
      filter(Boolean),
      tap(() => this.selectionChanged$.emit(selection)),
      tap(() => checkbox.checked = true),
    ).subscribe();
  }

  private deselect(selection: PaymentAccountCorrectionSelected, checkbox: MatCheckbox) {
    Forms.confirm(
      this.confirmDialog,
      {
        title: 'payments.accounts.corrections.deselect.title',
        message: {
          i18nKey: 'payments.accounts.corrections.deselect.message'
        },
      }).pipe(
      take(1),
      filter(Boolean),
      tap(() => this.selectionChanged$.emit(selection)),
      tap(() => checkbox.checked = false),
    ).subscribe();
  }

  submitAmountChanges(rowIndex: number, correctionId: number) {
    const updateDto = {
      correctedPublicContribution: this.correctionLinking.at(rowIndex).get('publicContribution')?.value,
      correctedAutoPublicContribution: this.correctionLinking.at(rowIndex).get('autoPublicContribution')?.value,
      correctedPrivateContribution: this.correctionLinking.at(rowIndex).get('privateContribution')?.value,
      correctedFundAmount: this.correctionLinking.at(rowIndex).get('fundAmount')?.value,
      comment: this.correctionLinking.at(rowIndex).get('comment')?.value || '',
    } as PaymentAccountCorrectionLinkingUpdateDTO;
    this.submitPayment$.emit({correctionId, updateDto});
    this.editedRowIndex = null;
    this.formService.setDirty(false);
  }

  resetAmounts(rowIndex: number, linkingDTO: PaymentAccountCorrectionLinkingDTO) {
    this.correctionLinking.at(rowIndex).patchValue({
      fundAmount: linkingDTO.fundAmount,
      autoPublicContribution: linkingDTO.autoPublicContribution,
      publicContribution: linkingDTO.publicContribution,
      privateContribution: linkingDTO.privateContribution,
    });
    this.formService.setDirty(true);
  }

  discardChanges(rowIndex: number, linkingDTO: PaymentAccountCorrectionLinkingDTO) {
    this.correctionLinking.at(rowIndex).patchValue({
      fundAmount: linkingDTO.correctedFundAmount,
      autoPublicContribution: linkingDTO.correctedAutoPublicContribution,
      publicContribution: linkingDTO.correctedPublicContribution,
      privateContribution: linkingDTO.correctedPrivateContribution,
    });
    this.editedRowIndex = null;
    this.formService.setDirty(false);
  }

  editAmounts(rowIndex: number) {
    this.editedRowIndex = rowIndex;
    setTimeout(() => {
      this.scrollToRight();
    });
  }

  scrollToRight(): void {
    this.correctionTable.nativeElement.scrollLeft = this.correctionTable.nativeElement.scrollWidth;
  }

}
