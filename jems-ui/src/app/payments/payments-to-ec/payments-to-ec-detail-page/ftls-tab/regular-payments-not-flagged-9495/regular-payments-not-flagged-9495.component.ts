import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {
  PagePaymentToEcLinkingDTO,
  PaymentApplicationToEcDetailDTO,
  PaymentToEcLinkingDTO,
  PaymentToEcLinkingUpdateDTO
} from '@cat/api';
import {AbstractControl, FormArray, FormBuilder} from '@angular/forms';
import {APIError} from '@common/models/APIError';
import {PaymentToEcFtlsTabStoreService} from '../payment-to-ec-ftls-tab-store.service';
import {PaymentsToEcDetailPageStore} from '../../payment-to-ec-detail-page-store.service';
import {MatDialog} from '@angular/material/dialog';
import {catchError, filter, map, switchMap, take, tap} from 'rxjs/operators';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {Alert} from '@common/components/forms/alert';
import {MatCheckbox} from '@angular/material/checkbox/checkbox';
import {Forms} from '@common/utils/forms';

@UntilDestroy()
@Component({
  selector: 'jems-regular-payments-not-flagged-9495',
  templateUrl: './regular-payments-not-flagged-9495.component.html',
  styleUrls: ['./regular-payments-not-flagged-9495.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class RegularPaymentsNotFlagged9495Component implements OnInit {

  displayedColumns = [
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

  form = this.formBuilder.group({
    ecRegularPayments: this.formBuilder.array([]),
  });

  data$: Observable<{
    ecId: number;
    ecRegularPayments: PagePaymentToEcLinkingDTO;
    isEditable: boolean;
  }>;

  Alert = Alert;
  dataSource: AbstractControl[];
  error$ = new BehaviorSubject<APIError | null>(null);
  editedRowIndex: number | null = null;
  successfulUpdateMessage = false;

  constructor(
    public pageStore: PaymentToEcFtlsTabStoreService,
    private detailPageStore: PaymentsToEcDetailPageStore,
    private confirmDialog: MatDialog,
    private formBuilder: FormBuilder,
    public formService: FormService,
    private changeDetectorRef: ChangeDetectorRef
  ) {
    this.data$ = combineLatest([
      this.pageStore.regularPage$,
      this.detailPageStore.paymentToEcId$,
      this.detailPageStore.userCanEdit$,
      this.detailPageStore.updatedPaymentApplicationStatus$,
    ]).pipe(
      tap(([ecRegularPayments ]) => {
        if (ecRegularPayments.content) {
          this.initializeForm(ecRegularPayments.content);
        }
      }),
      map(([ecRegularPayments, ecId, userCanEdit, ecStatus ]) => ({
        ecRegularPayments,
        ecId,
        isEditable: userCanEdit && ecStatus === PaymentApplicationToEcDetailDTO.StatusEnum.Draft
      })),
    );
    this.formService.init(this.form);
  }

  ngOnInit(): void {
    this.pageStore.regularRetrieveListError$.pipe(untilDestroyed(this)).subscribe(value => {
      if (value) {
        this.showErrorMessage(value);
      }
    });
  }

  selectionChanged(ecId: number, regularPaymentId: number, checked: boolean, event: MatCheckboxChange): void {
    event.source.checked = checked;
    if (checked) {
      this.deselectEcRegularArtNot94Not95(ecId, regularPaymentId, event.source);
    } else {
      this.selectEcRegularArtNot94Not95(ecId, regularPaymentId, event.source);
    }
  }

  private selectEcRegularArtNot94Not95(ecId: number, regularPaymentId: number, checkbox: MatCheckbox) {
    Forms.confirm(
      this.confirmDialog,
      {
        title: 'payments.to.ec.detail.ftls.select.title',
        message: {
          i18nKey: 'payments.to.ec.detail.ftls.select.message'
        },
      }).pipe(
      take(1),
      filter(Boolean),
      switchMap(() => this.pageStore.selectPaymentToEc(ecId, regularPaymentId)
        .pipe(
          tap(() => checkbox.checked = true),
          catchError((error) => this.showErrorMessage(error.error))
        ))
    ).subscribe();
  }

  private deselectEcRegularArtNot94Not95(ecId: number, regularPaymentId: number, checkbox: MatCheckbox) {
    Forms.confirm(
      this.confirmDialog,
      {
        title: 'payments.to.ec.detail.ftls.deselect.title',
        message: {
          i18nKey: 'payments.to.ec.detail.ftls.deselect.message'
        },
      }).pipe(
      take(1),
      filter(Boolean),
      switchMap(() => this.pageStore.deselectPaymentFromEc(regularPaymentId)
        .pipe(
          tap(() => checkbox.checked = false),
          catchError((error) => this.showErrorMessage(error.error))
        ))
    ).subscribe();
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      if (this.error$.value?.id === error.id) {
        this.error$.next(null);
      }
    }, 10000);
    return of(null);
  }

  private showSuccessMessageAfterUpdate(): void {
    this.successfulUpdateMessage = true;
    setTimeout(() => {
      this.successfulUpdateMessage = false;
      this.changeDetectorRef.markForCheck();
    }, 4000);
  }

  editAmounts(rowIndex: number) {
    this.editedRowIndex = rowIndex;
  }

  resetAmounts(rowIndex: number, regularPayment: PaymentToEcLinkingDTO) {
    this.ecRegularPayments.at(rowIndex).patchValue({
      regularAutoPublicContribution: regularPayment.autoPublicContribution,
      regularPublicContribution: regularPayment.publicContribution,
      regularPrivateContribution: regularPayment.privateContribution
    });
    this.formService.setDirty(true);
  }

  submitAmountChanges(rowIndex: number, ecId: number, regularPaymentId: number) {
    const dataToUpdate = {
      correctedPublicContribution: this.ecRegularPayments.at(rowIndex).get('regularPublicContribution')?.value,
      correctedAutoPublicContribution: this.ecRegularPayments.at(rowIndex).get('regularAutoPublicContribution')?.value,
      correctedPrivateContribution: this.ecRegularPayments.at(rowIndex).get('regularPrivateContribution')?.value
    } as PaymentToEcLinkingUpdateDTO;

    this.pageStore.updateLinkedPayment(regularPaymentId, dataToUpdate).pipe(
      take(1),
      tap(_ => this.showSuccessMessageAfterUpdate()),
      catchError(err => this.showErrorMessage(err.error)),
      untilDestroyed(this)
    ).subscribe();
    this.editedRowIndex = null;
  }

  discardChanges(rowIndex: number, unchangedRegularPayment: PaymentToEcLinkingDTO) {
    this.ecRegularPayments.at(rowIndex).patchValue({
      regularAutoPublicContribution: unchangedRegularPayment.correctedAutoPublicContribution,
      regularPublicContribution: unchangedRegularPayment.correctedPublicContribution,
      regularPrivateContribution: unchangedRegularPayment.correctedPrivateContribution
    });
    this.editedRowIndex = null;
    this.formService.setDirty(false);
  }

  private initializeForm(ecRegularPayments: PaymentToEcLinkingDTO[]) {
    this.ecRegularPayments.clear();
    ecRegularPayments.forEach(e => this.addRegularPayment(e));
    this.formService.setEditable(true);
    this.formService.resetEditable();
    this.dataSource = [...this.ecRegularPayments.controls];
  }

  get ecRegularPayments(): FormArray {
    return this.form.get('ecRegularPayments') as FormArray;
  }

  addRegularPayment(regularPayment: PaymentToEcLinkingDTO) {
    const item = this.formBuilder.group({
      regularAutoPublicContribution: this.formBuilder.control(regularPayment.correctedAutoPublicContribution),
      regularPublicContribution: this.formBuilder.control(regularPayment.correctedPublicContribution),
      regularPrivateContribution: this.formBuilder.control(regularPayment.correctedPrivateContribution),
    });
    this.ecRegularPayments.push(item);
  }
}
