import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {PagePaymentToEcLinkingDTO, PaymentApplicationToEcDetailDTO, PaymentToEcAmountSummaryDTO, PaymentToEcLinkingUpdateDTO} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {PaymentToEcRegularProjectsTabStoreService} from './payment-to-ec-regular-projects-tab-store.service';
import {catchError, filter, map, switchMap, take, tap} from 'rxjs/operators';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {PaymentsToEcDetailPageStore} from '../payment-to-ec-detail-page-store.service';
import {MatCheckbox} from '@angular/material/checkbox/checkbox';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {APIError} from '@common/models/APIError';
import {Alert} from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-payments-application-to-ec-regular-projects-tab',
  templateUrl: './payment-to-ec-regular-projects-tab.component.html',
  styleUrls: ['./payment-to-ec-regular-projects-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaymentToEcRegularProjectsTabComponent implements OnInit {

  data$: Observable<{
    ecId: number;
    paymentToEcLinking: PagePaymentToEcLinkingDTO;
    isEditable: boolean;
  }>;

  cumulativeForCurrentTab$: Observable<{
    data: PaymentToEcAmountSummaryDTO;
  }>;

  Alert = Alert;

  error$ = new BehaviorSubject<APIError | null>(null);
  success$ = new BehaviorSubject(false);

  constructor(
    public pageStore: PaymentToEcRegularProjectsTabStoreService,
    private detailPageStore: PaymentsToEcDetailPageStore,
    private confirmDialog: MatDialog,
    private changeDetectorRef: ChangeDetectorRef
  ) {
    this.data$ = combineLatest([
      this.pageStore.ftlsPage$,
      this.detailPageStore.paymentToEcId$,
      this.detailPageStore.userCanEdit$,
      this.detailPageStore.updatedPaymentApplicationStatus$,
    ]).pipe(
      map(([paymentToEcLinking, ecId, userCanEdit, ecStatus]) => ({
        paymentToEcLinking,
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
  }

  ngOnInit(): void {
    this.pageStore.ftlsRetrieveListError$.pipe(untilDestroyed(this)).subscribe(value => {
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

  updateLinkedPayment(paymentId: number, updateDto: PaymentToEcLinkingUpdateDTO) {
    this.pageStore.updateLinkedPayment(paymentId, updateDto).pipe(
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
