import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {
  PagePaymentToEcLinkingDTO,
  PaymentApplicationToEcDetailDTO,
  PaymentDetailDTO,
  PaymentToEcAmountSummaryDTO, PaymentToEcLinkingUpdateDTO
} from '@cat/api';
import {APIError} from '@common/models/APIError';
import {Alert} from '@common/components/forms/alert';
import PaymentTypeEnum = PaymentDetailDTO.PaymentTypeEnum;
import {PaymentsToEcDetailPageStore} from '../payment-to-ec-detail-page-store.service';
import {MatDialog} from '@angular/material/dialog';
import {catchError, filter, map, switchMap, take, tap} from 'rxjs/operators';
import {ProjectsFlaggedArt9495TabStoreService} from './projects-flagged-art-9495-tab-store.service';
import {MatCheckbox} from '@angular/material/checkbox/checkbox';
import {Forms} from '@common/utils/forms';
import {MatCheckboxChange} from '@angular/material/checkbox';

@UntilDestroy()
@Component({
  selector: 'jems-projects-flagged-art-9495-tab',
  templateUrl: './projects-flagged-art-9495-tab.component.html',
  styleUrls: ['./projects-flagged-art-9495-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProjectsFlaggedArt9495TabComponent implements OnInit {

  cumulativeForCurrentTab$: Observable<{
    data: PaymentToEcAmountSummaryDTO;
  }>;

  ftlsData: {
    ecId: number;
    paymentToEcLinking: PagePaymentToEcLinkingDTO;
    isEditable: boolean;
  };

  regularData: {
    ecId: number;
    paymentToEcLinking: PagePaymentToEcLinkingDTO;
    isEditable: boolean;
  };

  data$: Observable<{
    ecId: number;
    ftlsPaymentToEcLinking: PagePaymentToEcLinkingDTO;
    regularPaymentToEcLinking: PagePaymentToEcLinkingDTO;
    isEditable: boolean;
  }>;

  Alert = Alert;
  PaymentTypeEnum = PaymentTypeEnum;
  ftlsError$ = new BehaviorSubject<APIError | null>(null);
  regularError$ = new BehaviorSubject<APIError | null>(null);
  ftlsSuccess$ = new BehaviorSubject(false);
  regularSuccess$ = new BehaviorSubject(false);

  constructor(
    public pageStore: ProjectsFlaggedArt9495TabStoreService,
    private detailPageStore: PaymentsToEcDetailPageStore,
    private confirmDialog: MatDialog,
  ) {
    this.data$ = combineLatest([
      this.pageStore.ftlsPage$,
      this.pageStore.regularPage$,
      this.detailPageStore.paymentToEcId$,
      this.detailPageStore.userCanEdit$,
      this.detailPageStore.updatedPaymentApplicationStatus$,
    ]).pipe(
      map(([ftlsPaymentToEcLinking, regularPaymentToEcLinking, ecId, userCanEdit, ecStatus]) => ({
        ftlsPaymentToEcLinking,
        regularPaymentToEcLinking,
        ecId,
        isEditable: userCanEdit && ecStatus === PaymentApplicationToEcDetailDTO.StatusEnum.Draft,
      })),
      tap(data => {
        this.ftlsData = {ecId: data.ecId, paymentToEcLinking: data.ftlsPaymentToEcLinking, isEditable: data.isEditable};
        this.regularData = {ecId: data.ecId, paymentToEcLinking: data.regularPaymentToEcLinking, isEditable: data.isEditable};
      })
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
        this.showFtlsErrorMessage(value);
      }
    });

    this.pageStore.regularRetrieveListError$.pipe(untilDestroyed(this)).subscribe(value => {
      if (value) {
        this.showRegularErrorMessage(value);
      }
    });
  }

  private showFtlsErrorMessage(error: APIError): Observable<null> {
    this.ftlsError$.next(error);
    setTimeout(() => {
      if (this.ftlsError$.value?.id === error.id) {
        this.ftlsError$.next(null);
      }
    }, 10000);
    return of(null);
  }

  private showRegularErrorMessage(error: APIError): Observable<null> {
    this.regularError$.next(error);
    setTimeout(() => {
      if (this.regularError$.value?.id === error.id) {
        this.regularError$.next(null);
      }
    }, 10000);
    return of(null);
  }

  selectionChanged(ecId: number, ftlsPaymentId: number, checked: boolean, event: MatCheckboxChange, isFtls: boolean): void {
    event.source.checked = checked;
    if (checked) {
      this.deselectPaymentArt94Or95(ecId, ftlsPaymentId, event.source, isFtls);
    } else {
      this.selectPaymentArt94Or95(ecId, ftlsPaymentId, event.source, isFtls);
    }
  }

  private selectPaymentArt94Or95(ecId: number, paymentId: number, checkbox: MatCheckbox, isFtls: boolean) {
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
      switchMap(() => this.pageStore.selectPaymentToEc(ecId, paymentId)
        .pipe(
          tap(() => checkbox.checked = true),
          catchError((error) => isFtls ? this.showFtlsErrorMessage(error.error) : this.showRegularErrorMessage(error.error))
        ))
    ).subscribe();
  }

  private deselectPaymentArt94Or95(ecId: number, paymentId: number, checkbox: MatCheckbox, isFtls: boolean) {
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
      switchMap(() => this.pageStore.deselectPaymentFromEc(ecId, paymentId)
        .pipe(
          tap(() => checkbox.checked = false),
          catchError((error) => isFtls ? this.showFtlsErrorMessage(error.error) : this.showRegularErrorMessage(error.error))
        ))
    ).subscribe();
  }

  updateLinkedPayment(ecId: number, paymentId: number, updateDto: PaymentToEcLinkingUpdateDTO, isFtls: boolean) {
    this.pageStore.updateLinkedPayment(ecId, paymentId, updateDto).pipe(
      take(1),
      tap(_ => isFtls ? this.showSuccessMessageAfterFtlsUpdate() : this.showSuccessMessageAfterRegularUpdate()),
      catchError(err => isFtls ? this.showFtlsErrorMessage(err.error) : this.showRegularErrorMessage(err.error)),
      untilDestroyed(this)
    ).subscribe();
  }

  private showSuccessMessageAfterFtlsUpdate(): void {
    this.ftlsSuccess$.next(true);
    setTimeout(() => this.ftlsSuccess$.next(false), 4000);
  }

  private showSuccessMessageAfterRegularUpdate(): void {
    this.regularSuccess$.next(true);
    setTimeout(() => this.regularSuccess$.next(false), 4000);
  }
}
