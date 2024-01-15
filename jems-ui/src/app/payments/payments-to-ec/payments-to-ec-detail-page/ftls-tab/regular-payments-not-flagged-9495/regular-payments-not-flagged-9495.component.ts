import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {
  PagePaymentToEcLinkingDTO,
  PaymentApplicationToEcDetailDTO,
  PaymentDetailDTO,
  PaymentToEcLinkingUpdateDTO
} from '@cat/api';
import {APIError} from '@common/models/APIError';
import {PaymentToEcRegularProjectsTabStoreService} from '../payment-to-ec-regular-projects-tab-store.service';
import {PaymentsToEcDetailPageStore} from '../../payment-to-ec-detail-page-store.service';
import {MatDialog} from '@angular/material/dialog';
import {catchError, filter, map, switchMap, take, tap} from 'rxjs/operators';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {Alert} from '@common/components/forms/alert';
import {MatCheckbox} from '@angular/material/checkbox/checkbox';
import {Forms} from '@common/utils/forms';
import PaymentTypeEnum = PaymentDetailDTO.PaymentTypeEnum;

@UntilDestroy()
@Component({
  selector: 'jems-regular-payments-not-flagged-9495',
  templateUrl: './regular-payments-not-flagged-9495.component.html',
  styleUrls: ['./regular-payments-not-flagged-9495.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class RegularPaymentsNotFlagged9495Component implements OnInit {

  data$: Observable<{
    ecId: number;
    paymentToEcLinking: PagePaymentToEcLinkingDTO;
    isEditable: boolean;
  }>;

  Alert = Alert;
  error$ = new BehaviorSubject<APIError | null>(null);
  success$ = new BehaviorSubject(false);
  PaymentTypeEnum = PaymentTypeEnum;

  constructor(
    public pageStore: PaymentToEcRegularProjectsTabStoreService,
    private detailPageStore: PaymentsToEcDetailPageStore,
    private confirmDialog: MatDialog,
  ) {
    this.data$ = combineLatest([
      this.pageStore.regularPage$,
      this.detailPageStore.paymentToEcId$,
      this.detailPageStore.userCanEdit$,
      this.detailPageStore.updatedPaymentApplicationStatus$,
    ]).pipe(
      map(([paymentToEcLinking, ecId, userCanEdit, ecStatus]) => ({
        paymentToEcLinking,
        ecId,
        isEditable: userCanEdit && ecStatus === PaymentApplicationToEcDetailDTO.StatusEnum.Draft
      })),
    );
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

  updateLinkedPayment(regularPaymentId: number, updateDto: PaymentToEcLinkingUpdateDTO) {
    this.pageStore.updateLinkedPayment(regularPaymentId, updateDto).pipe(
      take(1),
      tap(_ => this.showSuccessMessageAfterUpdate()),
      catchError(err => this.showErrorMessage(err.error)),
      untilDestroyed(this)
    ).subscribe();
  }

  private showSuccessMessageAfterUpdate(): void {
    this.success$.next(true);
    setTimeout(() => this.success$.next(false), 4000);
  }
}
