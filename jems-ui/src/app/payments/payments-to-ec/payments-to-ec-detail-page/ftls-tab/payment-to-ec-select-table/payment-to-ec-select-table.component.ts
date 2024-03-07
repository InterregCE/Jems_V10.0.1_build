import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  ViewChild
} from '@angular/core';
import {
  PagePaymentToEcLinkingDTO, PaymentDetailDTO,
  PaymentToEcLinkingDTO,
  PaymentToEcLinkingUpdateDTO,
  PaymentToProjectDTO
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {AbstractControl, FormArray, FormBuilder} from '@angular/forms';
import {Subject} from 'rxjs';
import {FormService} from '@common/components/section/form/form.service';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {MatSort} from '@angular/material/sort';
import {
  AdvancePaymentsDetailPageConstants
} from '../../../../advance-payments-page/advance-payments-detail-page/advance-payments-detail-page.constants';
import PaymentTypeEnum = PaymentDetailDTO.PaymentTypeEnum;

@Component({
  selector: 'jems-payment-to-ec-select-table',
  templateUrl: './payment-to-ec-select-table.component.html',
  styleUrls: ['./payment-to-ec-select-table.component.scss'],
  providers: [FormService]
})
export class PaymentToEcSelectTableComponent implements OnChanges {

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
  @Input()
  paymentType: PaymentTypeEnum;
  @Input()
  flaggedArt9495: boolean;

  @Output()
  selectionChanged = new EventEmitter<{ ecId: number; paymentId: number; checked: boolean; checkbox: MatCheckboxChange }>();
  @Output()
  submitPayment = new EventEmitter<{ ecId: number; paymentId: number; updateDto: PaymentToEcLinkingUpdateDTO }>();

  constants = AdvancePaymentsDetailPageConstants;
  PaymentTypeEnum = PaymentTypeEnum;

  form = this.formBuilder.group({
    paymentToEcLinking: this.formBuilder.array([]),
  });

  displayedColumns: string[] = [];
  displayedColumnsAll = [
    'select',
    'paymentId',
    'projectId',
    'projectAcronym',
    'priorityAxis',
    'claimNo',
    'maApprovalDate',
    'totalEligible',
    'totalEligibleWithoutScoArt9495',
    'unionContribution',
    'fundAmount',
    'partnerContribution',
    'publicContribution',
    'autoPublicContribution',
    'privateContribution',
    'correction'
  ];
  dataSource: MatTableDataSource<AbstractControl> = new MatTableDataSource([]);
  editedRowIndex: number | null = null;
  @ViewChild('paymentToEcTable', {read: ElementRef}) private paymentToEcTable: ElementRef;

  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
  ) {
    this.formService.init(this.form);
  }

  ngOnChanges(): void {
    const flagFilteredColumns = this.flaggedArt9495 ? this.displayedColumnsAll
      : this.displayedColumnsAll.filter(col => !['totalEligibleWithoutScoArt9495', 'unionContribution'].includes(col));
    this.displayedColumns = this.data.isEditable ? flagFilteredColumns
      : flagFilteredColumns.filter(col => !['select', 'correction'].includes(col));
    this.initializeForm(this.data.paymentToEcLinking.content);
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
      totalEligibleWithoutScoArt9495: this.formBuilder.control(link.correctedTotalEligibleWithoutSco),
      unionContribution: this.formBuilder.control(link.correctedFundAmountUnionContribution),
      fundAmount: this.formBuilder.control(this.flaggedArt9495 ? link.correctedFundAmountPublicContribution : link.payment.fundAmount)
    });
    this.paymentToEcLinking.push(item);
  }

  paymentSelectionChanged(ecId: number, paymentId: number, checked: boolean, event: MatCheckboxChange): void {
    this.selectionChanged.emit({ecId, paymentId, checked, checkbox: event});
  }

  submitAmountChanges(rowIndex: number, ecId: number, paymentId: number) {
    const updateDto = {
      correctedPublicContribution: this.paymentToEcLinking.at(rowIndex).get('publicContribution')?.value,
      correctedAutoPublicContribution: this.paymentToEcLinking.at(rowIndex).get('autoPublicContribution')?.value,
      correctedPrivateContribution: this.paymentToEcLinking.at(rowIndex).get('privateContribution')?.value,
      correctedTotalEligibleWithoutSco: this.paymentToEcLinking.at(rowIndex).get('totalEligibleWithoutScoArt9495')?.value,
      correctedFundAmountUnionContribution: this.paymentToEcLinking.at(rowIndex).get('unionContribution')?.value,
      correctedFundAmountPublicContribution: this.paymentToEcLinking.at(rowIndex).get('fundAmount')?.value
    } as PaymentToEcLinkingUpdateDTO;
    this.submitPayment.emit({ecId,paymentId, updateDto});
    this.editedRowIndex = null;
    this.formService.setDirty(false);
  }

  resetAmounts(rowIndex: number, linkingDTO: PaymentToEcLinkingDTO) {
    this.paymentToEcLinking.at(rowIndex).patchValue({
      autoPublicContribution: linkingDTO.autoPublicContribution,
      publicContribution: linkingDTO.publicContribution,
      privateContribution: linkingDTO.privateContribution,
      fundAmount: linkingDTO.payment.fundAmount,
      totalEligibleWithoutScoArt9495: linkingDTO.payment.fundAmount + linkingDTO.partnerContribution,
      unionContribution: 0
    });
    this.formService.setDirty(true);
  }

  discardChanges(rowIndex: number, linkingDTO: PaymentToEcLinkingDTO) {
    this.paymentToEcLinking.at(rowIndex).patchValue({
      autoPublicContribution: linkingDTO.correctedAutoPublicContribution,
      publicContribution: linkingDTO.correctedPublicContribution,
      privateContribution: linkingDTO.correctedPrivateContribution,
      totalEligibleWithoutScoArt9495: linkingDTO.correctedTotalEligibleWithoutSco,
      unionContribution: linkingDTO.correctedFundAmountUnionContribution,
      fundAmount: this.flaggedArt9495 ? linkingDTO.correctedFundAmountPublicContribution : linkingDTO.payment.fundAmount
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
    this.paymentToEcTable.nativeElement.scrollLeft = this.paymentToEcTable.nativeElement.scrollWidth;
  }

  getRouterLinkForRegularPayment(paymentToProjectDTO: PaymentToProjectDTO): string {
    return `/app/project/detail/${paymentToProjectDTO.projectId}/projectReports/${paymentToProjectDTO.paymentClaimId}`;
  }

  changeUnionContribution(rowIndex: number) {
    const updatedUnionContribution = this.paymentToEcLinking.at(rowIndex).get('unionContribution')?.value;
    const totalCorrection = this.data.paymentToEcLinking.content[rowIndex].payment.fundAmount + this.data.paymentToEcLinking.content[rowIndex].partnerContribution;
    this.paymentToEcLinking.at(rowIndex).patchValue({totalEligibleWithoutScoArt9495: totalCorrection - updatedUnionContribution});
  }

}
