import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {ActivatedRoute, Router} from '@angular/router';
import {
  PartnerReportProcurementStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-detail/partner-report-procurement-store.service';
import {combineLatest, Observable} from 'rxjs';
import {CurrencyDTO, ProjectPartnerReportDTO, ProjectPartnerReportProcurementDTO} from '@cat/api';
import {catchError, map, take, tap} from 'rxjs/operators';
import {FormBuilder, Validators} from '@angular/forms';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';

@UntilDestroy()
@Component({
  selector: 'jems-partner-procurement-identification',
  templateUrl: './partner-report-procurement-identification.component.html',
  styleUrls: ['./partner-report-procurement-identification.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PartnerReportProcurementIdentificationComponent {

  @Input()
  private procurementId: number;

  @Output()
  onIdChange = new EventEmitter<number>();

  isReportReopenedLimited = false;
  form = this.formBuilder.group({
    id: 0,
    reportNumber: [{
      value: '-',
      disabled: true,
    }],
    contractName: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(300),
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
    this.formService.init(this.form);
    this.data$ = combineLatest([
      this.procurementStore.procurement$,
      this.procurementStore.currencies$,
      this.partnerReportDetailPageStore.partnerReport$,
      this.partnerReportDetailPageStore.reportEditable$,
    ]).pipe(
      tap(([procurement, currencies, report, editable]) => {
          this.isReportReopenedLimited = report.status === ProjectPartnerReportDTO.StatusEnum.ReOpenSubmittedLimited || report.status === ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLimited;
          this.initializeForm(procurement, report, editable);
      }),
      map(([procurement, currencies, report]) => ({ procurement, currencies, reportNumber: report.reportNumber })),
    );
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
    if (this.isReportReopenedLimited && !isCreate)
      {this.form.controls.contractName.disable();}
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
        this.onIdChange.emit(procurement.id);
      }),
      tap(() => this.formService.setSuccess('project.application.partner.report.procurements.save.success')),
      catchError(error => this.formService.setError(error)),
    ).subscribe();
  }

  updateProcurement() {
    this.procurementStore.updateProcurement(this.isReportReopenedLimited ? this.form.getRawValue() : this.form.value)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.partner.report.procurements.save.success')),
        catchError(error => this.formService.setError(error)),
      ).subscribe();
  }

  discardChanges(originalData: ProjectPartnerReportProcurementDTO, currentReportNumber: number) {
    if (this.isCreate()) {
      this.router.navigate(['..'], { relativeTo: this.activatedRoute });
    } else {
      this.resetForm(originalData, currentReportNumber);
    }
  }
}
