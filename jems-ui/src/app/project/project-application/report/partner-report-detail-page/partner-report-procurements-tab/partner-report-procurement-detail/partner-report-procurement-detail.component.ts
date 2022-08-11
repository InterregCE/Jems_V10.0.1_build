import {ChangeDetectionStrategy, Component} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {ActivatedRoute, Router} from '@angular/router';
import {
  PartnerReportProcurementStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-detail/partner-report-procurement-store.service';
import {combineLatest, Observable} from 'rxjs';
import {CurrencyDTO, ProjectPartnerReportDTO, ProjectPartnerReportProcurementDTO} from '@cat/api';
import {map, take, tap} from 'rxjs/operators';
import {FormBuilder, Validators} from '@angular/forms';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';

@UntilDestroy()
@Component({
  selector: 'jems-partner-procurement-detail',
  templateUrl: './partner-report-procurement-detail.component.html',
  styleUrls: ['./partner-report-procurement-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PartnerReportProcurementDetailComponent {

  private procurementId = Number(this.activatedRoute?.snapshot?.params?.procurementId);

  form = this.formBuilder.group({
    id: 0,
    reportNumber: [{
      value: '-',
      disabled: true,
    }],
    contractName: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(50),
    ])],
    referenceNumber: ['', Validators.maxLength(30)],
    contractDate: null,
    contractType: ['', Validators.maxLength(30)],
    contractAmount: 0,
    currencyCode: ['', Validators.required],
    supplierName: ['', Validators.maxLength(30)],
    vatNumber: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(30),
    ])],
    comment: ['', Validators.maxLength(2000)],
  });

  data$: Observable<{
    procurement: ProjectPartnerReportProcurementDTO;
    currencies: CurrencyDTO[];
    reportNumber: number;
  }>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private procurementStore: PartnerReportProcurementStore,
    private formBuilder: FormBuilder,
    public formService: FormService,
    private partnerReportDetailPageStore: PartnerReportDetailPageStore,
    private router: Router,
  ) {
    this.procurementStore.procurementId$.next(this.procurementId);
    this.data$ = combineLatest([
      this.procurementStore.procurement$,
      this.procurementStore.currencies$,
      this.partnerReportDetailPageStore.partnerReport$,
      this.partnerReportDetailPageStore.reportEditable$,
    ]).pipe(
      tap(([procurement, currencies, report, editable]) =>
        this.initializeForm(procurement, report, editable)
      ),
      map(([procurement, currencies, report]) => ({ procurement, currencies, reportNumber: report.reportNumber })),
    );
    this.formService.init(this.form);
  }

  private initializeForm(
    procurementData: ProjectPartnerReportProcurementDTO,
    report: ProjectPartnerReportDTO,
    reportEditable: boolean,
  ) {
    const isCreate = !procurementData.id;
    const procurement = {
      ...procurementData,
      currencyCode: isCreate ? (report.identification.currency || 'EUR') : procurementData.currencyCode,
    };

    this.resetForm(procurement, report.reportNumber);

    const procurementIsFromThisReport = report.id === procurement.reportId;
    this.formService.setEditable(reportEditable && (isCreate || procurementIsFromThisReport));
    this.form.controls.reportNumber.disable();
    this.formService.setCreation(isCreate);
  }

  private resetForm(procurement: ProjectPartnerReportProcurementDTO, currentReportNumber: number) {
    this.form.reset();
    this.form.patchValue({
      id: procurement.id,
      reportNumber: `R.${procurement.reportNumber || currentReportNumber}`,
      contractName: procurement.contractName,
      referenceNumber: procurement.referenceNumber,
      contractDate: procurement.contractDate,
      contractType: procurement.contractType,
      contractAmount: procurement.contractAmount,
      currencyCode: procurement.currencyCode,
      supplierName: procurement.supplierName,
      vatNumber: procurement.vatNumber,
      comment: procurement.comment,
    });
  }

  saveForm() {
    if (this.isCreate()) {
      this.createProcurement();
    } else {
      this.updateProcurement();
    }
  }

  private isCreate(): boolean {
    return !this.form.get('id')?.value;
  }

  createProcurement() {
    this.procurementStore.createProcurement(this.form.value).pipe(
      take(1),
      tap((procurement) => {
        this.router.navigate(['..', procurement.id], {relativeTo: this.activatedRoute});
      }),
    ).subscribe();
  }

  updateProcurement() {
    this.procurementStore.updateProcurement(this.form.value)
      .pipe(take(1))
      .subscribe();
  }

  discardChanges(originalData: ProjectPartnerReportProcurementDTO, currentReportNumber: number) {
    if (this.isCreate()) {
      this.router.navigate(['..'], { relativeTo: this.activatedRoute });
    } else {
      this.resetForm(originalData, currentReportNumber);
    }
  }

}
