import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {
  PagePaymentToEcLinkingDTO,
  PaymentApplicationToEcDetailDTO,
  PaymentToEcAmountSummaryDTO,
  PaymentToEcLinkingDTO,
  PaymentToEcLinkingUpdateDTO
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {PaymentToEcFtlsTabStoreService} from './payment-to-ec-ftls-tab-store.service';
import {catchError, filter, map, switchMap, take, tap} from 'rxjs/operators';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {PaymentsToEcDetailPageStore} from '../payment-to-ec-detail-page-store.service';
import {MatCheckbox} from '@angular/material/checkbox/checkbox';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {APIError} from '@common/models/APIError';
import {Alert} from '@common/components/forms/alert';
import {AbstractControl, FormArray, FormBuilder} from '@angular/forms';

@UntilDestroy()
@Component({
  selector: 'jems-payments-application-to-ec-ftls-tab',
  templateUrl: './payment-to-ec-ftls-tab.component.html',
  styleUrls: ['./payment-to-ec-ftls-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class PaymentToEcFtlsTabComponent implements OnInit {

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
    ecFTLSs: this.formBuilder.array([]),
  });

  data$: Observable<{
    ecId: number;
    ecFTLSs: PagePaymentToEcLinkingDTO;
    isEditable: boolean;
    // cumulativeForCurrentTab: PaymentToEcAmountSummaryDTO;
  }>;

  cumulativeForCurrentTab$: Observable<{
    data: PaymentToEcAmountSummaryDTO;
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
      this.pageStore.page$,
      this.detailPageStore.paymentToEcId$,
      this.detailPageStore.userCanEdit$,
      this.detailPageStore.updatedPaymentApplicationStatus$,
    ]).pipe(
      tap(([ecFTLSs ]) => {
        if (ecFTLSs.content) {
          this.initializeForm(ecFTLSs.content);
        }
      }),
      map(([ecFTLSs, ecId, userCanEdit, ecStatus ]) => ({
        ecFTLSs,
        ecId,
        isEditable: userCanEdit && ecStatus === PaymentApplicationToEcDetailDTO.StatusEnum.Draft,
      })),
    );

    this.cumulativeForCurrentTab$ =
      this.pageStore.cumulativeForCurrentTab().pipe(
      map((cumulativeForCurrentTab) => ({
        data: cumulativeForCurrentTab
      })),
    );
    this.formService.init(this.form);
  }

  ngOnInit(): void {
    this.pageStore.retrieveListError$.pipe(untilDestroyed(this)).subscribe(value => {
      if (value) {
        this.showErrorMessage(value);
      }
    });
  }

  selectionChanged(ecId: number, ftlsPaymentId: number, checked: boolean, event: MatCheckboxChange): void {
    event.source.checked = checked;
    if (checked) {
      this.deselectEcFTLSArtNot94Not95(ecId, ftlsPaymentId, event.source);
    } else {
      this.selectEcFTLSArtNot94Not95(ecId, ftlsPaymentId, event.source);
    }
  }

  private selectEcFTLSArtNot94Not95(ecId: number, ftlsPaymentId: number, checkbox: MatCheckbox) {
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
      switchMap(() => this.pageStore.selectPaymentToEc(ecId, ftlsPaymentId)
        .pipe(
          tap(() => checkbox.checked = true),
          catchError((error) => this.showErrorMessage(error.error))
        ))
    ).subscribe();
  }

  private deselectEcFTLSArtNot94Not95(ecId: number, ftlsPaymentId: number, checkbox: MatCheckbox) {
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
      switchMap(() => this.pageStore.deselectPaymentFromEc(ftlsPaymentId)
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

  resetAmounts(rowIndex: number, ftls: PaymentToEcLinkingDTO) {
    this.ecFTLSs.at(rowIndex).patchValue({
      ftlsAutoPublicContribution: ftls.autoPublicContribution,
      ftlsPublicContribution: ftls.publicContribution,
      ftlsPrivateContribution: ftls.privateContribution
    });
    this.formService.setDirty(true);
  }

  submitAmountChanges(rowIndex: number, ecId: number, ftlsPaymentId: number) {
    const dataToUpdate = {
      correctedPublicContribution: this.ecFTLSs.at(rowIndex).get('ftlsPublicContribution')?.value,
      correctedAutoPublicContribution: this.ecFTLSs.at(rowIndex).get('ftlsAutoPublicContribution')?.value,
      correctedPrivateContribution: this.ecFTLSs.at(rowIndex).get('ftlsPrivateContribution')?.value
    } as PaymentToEcLinkingUpdateDTO;

    this.pageStore.updateLinkedPayment(ftlsPaymentId, dataToUpdate).pipe(
      take(1),
      tap(_ => this.showSuccessMessageAfterUpdate()),
      catchError(err => this.showErrorMessage(err.error)),
      untilDestroyed(this)
    ).subscribe();
    this.editedRowIndex = null;
  }

  discardChanges(rowIndex: number, unchangedFtls: PaymentToEcLinkingDTO) {
    this.ecFTLSs.at(rowIndex).patchValue({
      ftlsAutoPublicContribution: unchangedFtls.correctedAutoPublicContribution,
      ftlsPublicContribution: unchangedFtls.correctedPublicContribution,
      ftlsPrivateContribution: unchangedFtls.correctedPrivateContribution
    });
    this.editedRowIndex = null;
    this.formService.setDirty(false);
  }

  private initializeForm(ecFTLSs: PaymentToEcLinkingDTO[]) {
    this.ecFTLSs.clear();
    ecFTLSs.forEach(e => this.addFTLS(e));
    this.formService.setEditable(true);
    this.formService.resetEditable();
    this.dataSource = [...this.ecFTLSs.controls];
  }

  get ecFTLSs(): FormArray {
    return this.form.get('ecFTLSs') as FormArray;
  }

  addFTLS(ftls: PaymentToEcLinkingDTO) {
    const item = this.formBuilder.group({
      ftlsAutoPublicContribution: this.formBuilder.control(ftls.correctedAutoPublicContribution),
      ftlsPublicContribution: this.formBuilder.control(ftls.correctedPublicContribution),
      ftlsPrivateContribution: this.formBuilder.control(ftls.correctedPrivateContribution),
    });
    this.ecFTLSs.push(item);
  }
}
