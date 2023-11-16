import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {PagePaymentToEcLinkingDTO, PaymentToEcLinkingDTO, PaymentToEcLinkingUpdateDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {AbstractControl, FormArray, FormBuilder} from '@angular/forms';
import {Subject} from 'rxjs';
import {FormService} from '@common/components/section/form/form.service';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {MatSort} from '@angular/material/sort';

@Component({
  selector: 'jems-payment-to-ec-select-table',
  templateUrl: './payment-to-ec-select-table.component.html',
  styleUrls: ['./payment-to-ec-select-table.component.scss'],
  providers: [FormService]
})
export class PaymentToEcSelectTableComponent implements OnInit {

  @Input()
  data: {
    ecId: number;
    paymentToEcLinking: PagePaymentToEcLinkingDTO;
    isEditable: boolean;
  };

  @Input()
  newSort: Subject<Partial<MatSort>>;
  @Input()
  newSize: Subject<number>;
  @Input()
  newIndex: Subject<number>;

  @Output()
  selectionChanged = new EventEmitter<{ ecId: number; paymentId: number; checked: boolean; checkbox: MatCheckboxChange }>();
  @Output()
  submitPayment = new EventEmitter<{ paymentId: number; updateDto: PaymentToEcLinkingUpdateDTO }>();


  form = this.formBuilder.group({
    paymentToEcLinking: this.formBuilder.array([]),
  });

  displayedColumns: string[] = [];
  displayedColumnsAll = [
    'select',
    'projectId',
    'projectAcronym',
    'priorityAxis',
    'claimNo',
    'maApprovalDate',
    'totalEligible',
    'fundAmount',
    'partnerContribution',
    'publicContribution',
    'autoPublicContribution',
    'privateContribution',
    'correction'
  ];
  dataSource: MatTableDataSource<AbstractControl> = new MatTableDataSource([]);
  editedRowIndex: number | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
  ) {
    this.formService.init(this.form);
  }

  ngOnInit(): void {
    this.initializeForm(this.data.paymentToEcLinking.content);
    this.displayedColumns = this.data.isEditable ? this.displayedColumnsAll
      : this.displayedColumnsAll.filter(col => !['select', 'correction'].includes(col));
  }

  get paymentToEcLinking(): FormArray {
    return this.form.get('paymentToEcLinking') as FormArray;
  }

  private initializeForm(ecLinks: PaymentToEcLinkingDTO[]) {
    this.paymentToEcLinking.clear();
    ecLinks.forEach(e => this.addLink(e));
    this.formService.setEditable(true);
    this.formService.resetEditable();
    this.dataSource.data = [...this.paymentToEcLinking.controls];
  }


  addLink(link: PaymentToEcLinkingDTO) {
    const item = this.formBuilder.group({
      autoPublicContribution: this.formBuilder.control(link.correctedAutoPublicContribution),
      publicContribution: this.formBuilder.control(link.correctedPublicContribution),
      privateContribution: this.formBuilder.control(link.correctedPrivateContribution),
    });
    this.paymentToEcLinking.push(item);
  }

  selectionChanged2(ecId: number, paymentId: number, checked: boolean, event: MatCheckboxChange): void {
    this.selectionChanged.emit({ecId, paymentId, checked, checkbox: event});
  }

  editAmounts(rowIndex: number) {
    this.editedRowIndex = rowIndex;
  }

  submitAmountChanges(rowIndex: number, ecId: number, paymentId: number) {
    const updateDto = {
      correctedPublicContribution: this.paymentToEcLinking.at(rowIndex).get('publicContribution')?.value,
      correctedAutoPublicContribution: this.paymentToEcLinking.at(rowIndex).get('autoPublicContribution')?.value,
      correctedPrivateContribution: this.paymentToEcLinking.at(rowIndex).get('privateContribution')?.value
    } as PaymentToEcLinkingUpdateDTO;
    this.submitPayment.emit({paymentId, updateDto});
    this.editedRowIndex = null;
  }

  resetAmounts(rowIndex: number, linkingDTO: PaymentToEcLinkingDTO) {
    this.paymentToEcLinking.at(rowIndex).patchValue({
      autoPublicContribution: linkingDTO.autoPublicContribution,
      publicContribution: linkingDTO.publicContribution,
      privateContribution: linkingDTO.privateContribution
    });
    this.formService.setDirty(true);
  }

  discardChanges(rowIndex: number, linkingDTO: PaymentToEcLinkingDTO) {
    this.paymentToEcLinking.at(rowIndex).patchValue({
      autoPublicContribution: linkingDTO.correctedAutoPublicContribution,
      publicContribution: linkingDTO.correctedPublicContribution,
      privateContribution: linkingDTO.correctedPrivateContribution
    });
    this.editedRowIndex = null;
    this.formService.setDirty(false);
  }

}
