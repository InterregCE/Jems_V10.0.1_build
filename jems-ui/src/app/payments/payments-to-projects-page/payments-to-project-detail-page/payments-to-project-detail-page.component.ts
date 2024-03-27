import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {combineLatest, Observable, of, Subject} from 'rxjs';
import {
  AuditControlCorrectionDTO,
  AvailableCorrectionsForPaymentDTO,
  OutputUser,
  PaymentDetailDTO,
  PaymentPartnerDTO,
  PaymentPartnerInstallmentDTO,
  UserDTO
} from '@cat/api';
import {PaymentsToProjectPageStore} from '../payments-to-projects-page.store';
import {ActivatedRoute} from '@angular/router';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {PaymentsToProjectDetailPageConstants} from './payments-to-project-detail-page.constants';
import {PaymentsDetailPageStore} from './payments-detail-page.store';
import {catchError, map, take, tap} from 'rxjs/operators';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {FormService} from '@common/components/section/form/form.service';
import {SecurityService} from '../../../security/security.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {HttpErrorResponse} from '@angular/common/http';
import {LocaleDatePipe} from '@common/pipe/locale-date.pipe';
import {Alert} from '@common/components/forms/alert';
import {APIError} from '@common/models/APIError';
import {TranslateService} from '@ngx-translate/core';
import {PaymentsPageSidenavService} from '../../payments-page-sidenav.service';

@UntilDestroy()
@Component({
  selector: 'jems-payments-to-project-detail-page',
  templateUrl: './payments-to-project-detail-page.component.html',
  styleUrls: ['./payments-to-project-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
  providers: [FormService]
})
export class PaymentsToProjectDetailPageComponent implements OnInit {
  Alert = Alert;
  constants = PaymentsToProjectDetailPageConstants;
  tableData: AbstractControl[] = [];
  paymentId = this.activatedRoute.snapshot.params.paymentId;
  columnsToDisplay = ['partner', 'partnerName', 'amountApproved', 'addInstallment'];
  updateInstallmentsError$ = new Subject<HttpErrorResponse | null>();
  updateInstallmentsSuccess$ = new Subject<boolean>();
  toggleStatesOfPaymentRows: boolean[] = [];
  userCanEdit$: Observable<boolean>;
  PaymentTypeEnum = PaymentDetailDTO.PaymentTypeEnum;

  partnerPaymentsForm = this.formBuilder.group({
    id: '',
    projectCustomIdentifier: '',
    partnerPayments: this.formBuilder.array([this.formBuilder.group({
        installments: this.formBuilder.array([])
      })
    ])
  });
  initialPaymentDetail: PaymentDetailDTO;
  currentUserDetails: UserDTO;
  data$: Observable<{
    paymentDetail: PaymentDetailDTO;
    currentUser: UserDTO;
    availableCorrections: AvailableCorrectionsForPaymentDTO[];
  }>;
  compareCorrections = (c1: AuditControlCorrectionDTO, c2: AuditControlCorrectionDTO) => c1?.id === c2?.id;

  constructor(private paymentToProjectsStore: PaymentsToProjectPageStore,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private formService: FormService,
              private paymentsDetailPageStore: PaymentsDetailPageStore,
              private securityService: SecurityService,
              private localeDatePipe: LocaleDatePipe,
              private translateService: TranslateService,
              private changeDetectorRef: ChangeDetectorRef,
              private paymentsPageSidenav: PaymentsPageSidenavService) {
    this.userCanEdit$ = this.paymentToProjectsStore.userCanEdit$;
  }

  ngOnInit(): void {
      this.data$ = combineLatest([
      this.paymentsDetailPageStore.paymentDetail$,
      this.securityService.currentUserDetails,
      this.paymentsDetailPageStore.availableCorrections$,
    ])
      .pipe(
        map(([paymentDetail, currentUser, availableCorrections]: any) => ({
          paymentDetail,
          currentUser,
          availableCorrections,
        })),
        tap((data) => this.initialPaymentDetail = data.paymentDetail),
        tap(data => this.currentUserDetails = data.currentUser),
        tap(data => this.resetForm(data.paymentDetail))
      );

    this.updateInstallmentsError$.pipe(
      tap(error => {
        if(error) {
          this.formService.setError(error);
          this.updateInstallmentsError$.next(null);
        } else {
          this.formService.setError(null);
        }
      }),
      untilDestroyed(this)
    ).subscribe();

    this.updateInstallmentsSuccess$.pipe(
      tap(() => this.formService.setSuccess('payments.detail.table.have.success'))
    ).subscribe();

    this.formService.init(this.partnerPaymentsForm, of(true));
  }

  resetForm(paymentDetail: PaymentDetailDTO) {
    this.partnerPaymentsForm.get('id')?.setValue(this.paymentId);
    this.partnerPaymentsForm.get('projectCustomIdentifier')?.setValue(paymentDetail.projectCustomIdentifier);

    this.partnerPayments.clear();

    paymentDetail.partnerPayments.forEach((partnerPayment, index) => this.addPartnerPayment(partnerPayment, index));

    this.updateAllPaymentRowToggleStates();

    this.userCanEdit$.pipe(
      tap(userCanEdit => this.disableAllFields(userCanEdit)),
      untilDestroyed(this)
    ).subscribe();
  }

  disableAllFields(userCanEdit: boolean) {
    if(!userCanEdit) {
      this.partnerPaymentsForm.disable();
    }
  }

  addInstallment(installment: PaymentPartnerInstallmentDTO | null, paymentIndex: number): void {
    const group = this.formBuilder.group({
      id: installment?.id ? installment.id : null,
      amountPaid: installment?.amountPaid ? this.formBuilder.control(installment.amountPaid) : this.computeAvailableSum(paymentIndex),
      paymentDate: this.formBuilder.control(installment?.paymentDate ? installment.paymentDate : null),
      comment: this.formBuilder.control(installment?.comment ? installment.comment : '', [Validators.maxLength(500)]),
      savePaymentInfo: this.formBuilder.control(installment?.savePaymentInfo ? installment.savePaymentInfo : false),
      savePaymentInfoUser: this.formBuilder.control(installment?.savePaymentInfoUser ? this.getOutputUserObject(installment.savePaymentInfoUser) : null),
      savePaymentDate: this.formBuilder.control(installment?.savePaymentDate ? installment.savePaymentDate : null),
      paymentConfirmed: this.formBuilder.control(installment?.paymentConfirmed ? installment.paymentConfirmed : false),
      paymentConfirmedUser: this.formBuilder.control(installment?.paymentConfirmedUser ? this.getOutputUserObject(installment.paymentConfirmedUser) : null),
      paymentConfirmedDate: this.formBuilder.control(installment?.paymentConfirmedDate ? installment.paymentConfirmedDate : null),
      correction: this.formBuilder.control(installment?.correction || 0),
    });
    this.disableFieldsIfPaymentIsSaved(group);
    this.disableFieldsIfPaymentIsConfirmed(group);
    this.installmentsArray(paymentIndex).push(group);
  }

  addPartnerPayment(partnerPayment: PaymentPartnerDTO, paymentIndex: number): void {
    this.partnerPayments.push(this.formBuilder.group({
        id: partnerPayment.id,
        partnerId: partnerPayment.partnerId,
        partnerNumber: this.formBuilder.control(partnerPayment.partnerNumber),
        partnerRole: this.formBuilder.control(partnerPayment.partnerRole),
        partnerAbbreviation: this.formBuilder.control(partnerPayment.partnerAbbreviation),
        amountApproved: this.formBuilder.control(partnerPayment.amountApproved),
        nameInEnglish: this.formBuilder.control(partnerPayment.nameInEnglish),
        nameInOriginalLanguage: this.formBuilder.control(partnerPayment.nameInOriginalLanguage),
        installments: this.formBuilder.array([])
      })
    );
    partnerPayment.installments.forEach( (installment: PaymentPartnerInstallmentDTO) => {
      this.addInstallment(installment, paymentIndex);
    });
  }

  get partnerPayments(): FormArray {
    return this.partnerPaymentsForm.get(this.constants.FORM_CONTROL_NAMES.partnerPayments) as FormArray;
  }

  installmentsArray(paymentIndex: number): FormArray {
    return this.partnerPayments.at(paymentIndex).get(this.constants.FORM_CONTROL_NAMES.installments) as FormArray;
  }

  updatePaymentInstallments(paymentId: number, paymentDetail: PaymentDetailDTO, data: PaymentDetailDTO) {
    paymentDetail.paymentType = data.paymentType;
    paymentDetail.projectId = data.projectId;
    paymentDetail.projectAcronym = data.projectAcronym;
    paymentDetail.amountApprovedPerFund = data.amountApprovedPerFund;
    paymentDetail.dateOfLastPayment = data.dateOfLastPayment;
    paymentDetail.partnerPayments.forEach(pp => // transform 0 into null
      pp.installments.forEach(i => i.correction = i.correction || null)
    );
    this.paymentsDetailPageStore.updatePaymentInstallments(paymentId, paymentDetail.partnerPayments).pipe(
        take(1),
        tap(() => this.updateInstallmentsSuccess$.next(true)),
        catchError(error => {
          this.updateInstallmentsError$.next(error);
          const apiError = error.error as APIError;
          if (apiError?.formErrors) {
            Object.keys(apiError.formErrors).forEach(key => {
              const fieldAndIndexArr = key.split('-');
              if(fieldAndIndexArr.length == 3) {
                const field = fieldAndIndexArr[0];
                const partnerIndex = Number(fieldAndIndexArr[1]);
                const installmentIndex = Number(fieldAndIndexArr[2]);
                const control = this.installmentsArray(partnerIndex).at(installmentIndex).get(field);
                control?.setErrors({required: this.translateService.instant(apiError.formErrors[key].i18nKey)});
                control?.markAsDirty();
              }
            });
            this.changeDetectorRef.detectChanges();
          }
          this.formService.setError(error);
          throw error;
        }),
        untilDestroyed(this)
      )
      .subscribe(updatedPaymentDetail => this.paymentsDetailPageStore.savedPaymentDetail$.next(updatedPaymentDetail));
  }

  computeAvailableSum(paymentIndex: number): number {
    let amountPaid = 0;
    this.installmentsArray(paymentIndex).controls.forEach((formGroup: AbstractControl) => {
      amountPaid += formGroup?.get(this.constants.FORM_CONTROL_NAMES.amountPaid)?.value;
    });
    return this.partnerPayments.at(paymentIndex).value.amountApproved - amountPaid;
  }

  setSavePaymentDate(isChecked: boolean, paymentIndex: number, installmentIndex: number) {
    if (isChecked) {
      this.installmentsArray(paymentIndex).at(installmentIndex).get('amountPaid')?.disable();
      this.installmentsArray(paymentIndex).at(installmentIndex).get('savePaymentDate')?.setValue(this.getFormattedCurrentLocaleDate());
      this.installmentsArray(paymentIndex).at(installmentIndex).get('savePaymentInfoUser')?.setValue(this.getOutputUserObject(this.currentUserDetails));
    } else {
      this.installmentsArray(paymentIndex).at(installmentIndex).get('amountPaid')?.enable();
      this.installmentsArray(paymentIndex).at(installmentIndex).get('savePaymentDate')?.setValue(null);
      this.installmentsArray(paymentIndex).at(installmentIndex).get('savePaymentInfoUser')?.setValue(null);
    }
  }

  disableFieldsIfPaymentIsSaved(group: FormGroup) {
    if (group.get('savePaymentInfo')?.value) {
      group.get('amountPaid')?.disable();
    }
  }

  disableFieldsIfPaymentIsConfirmed(group: FormGroup) {
    if (group.get('paymentConfirmed')?.value) {
      group.get('paymentDate')?.disable();
    }
  }

  getOutputUserObject(userDetails: UserDTO | OutputUser): OutputUser {
    return {
      id: userDetails.id,
      email: userDetails.email,
      name: userDetails.name,
      surname: userDetails.surname
    } as OutputUser;
  }

  getFormattedCurrentLocaleDate() {
    const date = new Date();
    return date.toISOString().substring(0,10);
  }

  setConfirmPaymentDate(isChecked: boolean, paymentIndex: number, installmentIndex: number) {
    if (isChecked) {
      this.installmentsArray(paymentIndex).at(installmentIndex).get('paymentDate')?.setValidators([Validators.required]);
      this.installmentsArray(paymentIndex).at(installmentIndex).get('paymentConfirmedDate')?.setValue(this.getFormattedCurrentLocaleDate());
      this.installmentsArray(paymentIndex).at(installmentIndex).get('paymentConfirmedUser')?.setValue(this.getOutputUserObject(this.currentUserDetails));

      if(!this.isPaymentDateEmpty(paymentIndex, installmentIndex)) {
        this.installmentsArray(paymentIndex).at(installmentIndex).get(this.constants.FORM_CONTROL_NAMES.paymentDate)?.disable();
      }
    } else {
      this.installmentsArray(paymentIndex).at(installmentIndex).get('paymentDate')?.clearValidators();
      this.installmentsArray(paymentIndex).at(installmentIndex).get('paymentConfirmedDate')?.setValue(null);
      this.installmentsArray(paymentIndex).at(installmentIndex).get('paymentConfirmedUser')?.setValue(null);
      this.installmentsArray(paymentIndex).at(installmentIndex).get(this.constants.FORM_CONTROL_NAMES.paymentDate)?.enable();
    }
  }

  isPaymentDateEmpty(paymentIndex: number, installmentIndex: number): boolean {
    return !this.installmentsArray(paymentIndex).at(installmentIndex).get(this.constants.FORM_CONTROL_NAMES.paymentDate)?.value;
  }

  removeItem(paymentIndex: number, installmentIndex: number) {
    this.installmentsArray(paymentIndex).removeAt(installmentIndex);
    this.formService.setDirty(true);
    this.updateAllPaymentRowToggleStates();
  }

  isPaymentAuthorised(paymentIndex: number, installmentIndex: number): boolean {
    return this.installmentsArray(paymentIndex).at(installmentIndex).get('savePaymentInfo')?.value;
  }

  isInstallmentAlreadyAuthorised(paymentIndex: number, installmentIndex: number): boolean {
    if (installmentIndex >= this.initialPaymentDetail.partnerPayments[paymentIndex].installments.length) {
      return false;
    }
    return  this.initialPaymentDetail.partnerPayments[paymentIndex].installments[installmentIndex].savePaymentInfo || false;
  }

  canInstallmentBeDeleted(paymentIndex: number, installmentIndex: number) {
    return !this.isPaymentAuthorised(paymentIndex, installmentIndex) && !this.isInstallmentAlreadyAuthorised(paymentIndex, installmentIndex);
  }

  isPaymentAuthorisationDisabled(paymentIndex: number, installmentIndex: number): boolean {
    return this.isPaymentConfirmed(paymentIndex, installmentIndex) ||
    this.isPaymentAlreadyConfirmed(paymentIndex, installmentIndex);
  }

  isPaymentConfirmationDisabled(paymentIndex: number, installmentIndex: number) {
    return  !this.isPaymentAuthorised(paymentIndex, installmentIndex);
  }

  isPaymentConfirmed(paymentIndex: number, installmentIndex: number): boolean {
    return this.installmentsArray(paymentIndex).at(installmentIndex)
      .get(this.constants.FORM_CONTROL_NAMES.paymentConfirmed)?.value;
  }

  isPaymentAlreadyConfirmed(paymentIndex: number, installmentIndex: number): boolean {
    if (installmentIndex >= this.initialPaymentDetail.partnerPayments[paymentIndex].installments.length) {
      return false;
    }
    return  this.initialPaymentDetail.partnerPayments[paymentIndex].installments[installmentIndex].paymentConfirmed || false;
  }

  addInstallmentButtonClicked(installment: PaymentPartnerInstallmentDTO | null, paymentIndex: number): void {
    this.addInstallment(installment, paymentIndex);
    this.formService.setDirty(true);
  }

  getFormattedDate(value: string): any {
    return this.localeDatePipe.transform(value);
  }

  togglePaymentRowAtIndex(index: number): void {
    this.toggleStatesOfPaymentRows[index] = !this.toggleStatesOfPaymentRows[index];
  }

  getPaymentRowToggleStateAtIndex(index: number): boolean {
    return this.toggleStatesOfPaymentRows[index];
  }

  updateAllPaymentRowToggleStates(): void {
    if (!this.toggleStatesOfPaymentRows.length) {
      this.toggleStatesOfPaymentRows = new Array(this.partnerPayments.length).fill(false);
    }
    else {
      this.toggleStatesOfPaymentRows.forEach((toggleState, index) => {
        if (!this.installmentsArray(index).length)
          {this.toggleStatesOfPaymentRows[index] = false;}
      });
    }
  }

  getUnauthorisedAmountForIndex(paymentIndex: number): number {
    let amountUnauthorised = this.partnerPayments.at(paymentIndex).value.amountApproved;

    this.installmentsArray(paymentIndex).controls.forEach((formGroup: AbstractControl, index) => {
      if (this.isPaymentAuthorised(paymentIndex, index))
        {amountUnauthorised -= formGroup?.get(this.constants.FORM_CONTROL_NAMES.amountPaid)?.value;}
    });

    return amountUnauthorised;
  }

  showPaymentIndicatorForIndex(paymentIndex: number): boolean {
    if (this.computeAvailableSum(paymentIndex) > 0)
      {return true;}
    else {
      for (let i=0; i<this.installmentsArray(paymentIndex).length; i++) {
        if (!this.isPaymentConfirmed(paymentIndex, i))
          {return true;}
      }
    }
    return false;
  }

  getAvailableCorrectionsForPartner(availableCorrections: AvailableCorrectionsForPaymentDTO[], partnerId: number): AuditControlCorrectionDTO[] {
    return availableCorrections.find(el => el.partnerId === partnerId)?.corrections ?? [];
  }

  isPaymentDateRequired(paymentIndex: number, installmentIndex: number): boolean {
    return this.installmentsArray(paymentIndex).at(installmentIndex).get('paymentConfirmed')?.value && this.isPaymentDateEmpty(paymentIndex, installmentIndex);
  }
}

