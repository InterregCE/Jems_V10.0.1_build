import {Component} from '@angular/core';
import {combineLatest, Observable, of} from 'rxjs';
import {catchError, map, take, tap} from 'rxjs/operators';
import {
  ProjectVerificationReportExpenditureStore
} from '@project/project-application/report/project-verification-report/project-verification-report-expenditure-tab/project-verification-report-expenditure.store';
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {
  ProjectVerificationReportExpenditureConstants
} from '@project/project-application/report/project-verification-report/project-verification-report-expenditure-tab/project-verification-report-expenditure.constants';
import {FormService} from '@common/components/section/form/form.service';
import {
  ExpenditureInvestmentBreakdownLineDTO,
  ExpenditureParkingMetadataDTO,
  ProjectPartnerReportExpenditureItemDTO,
  ProjectPartnerReportLumpSumDTO,
  ProjectPartnerReportProcurementDTO, ProjectReportDTO,
  ProjectReportVerificationExpenditureLineDTO,
  ProjectReportVerificationExpenditureLineUpdateDTO,
  ProjectReportVerificationRiskBasedDTO,
  TypologyErrorsDTO
} from '@cat/api';
import {MatSlideToggleChange} from '@angular/material/slide-toggle';
import {CustomTranslatePipe} from '@common/pipe/custom-translate-pipe';
import {TranslateByInputLanguagePipe} from '@common/pipe/translate-by-input-language.pipe';
import {ProjectReportPageStore} from '@project/project-application/report/project-report/project-report-page-store.service';
import {Alert} from '@common/components/forms/alert';
import {
  ExpenditureItemParkedByChipComponent,
  ExpenditureParkedByEnum,
} from '@project/project-application/report/partner-report-detail-page/partner-report-expenditures-tab/expenditure-parked-by-chip/expenditure-item-parked-by-chip.component';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';

@Component({
  selector: 'jems-project-verification-report-expenditure-tab',
  templateUrl: './project-verification-report-expenditure-tab.component.html',
  styleUrls: ['./project-verification-report-expenditure-tab.component.scss'],
  providers: [FormService]
})
export class ProjectVerificationReportExpenditureTabComponent {

  Alert = Alert;
  PartnerRole = ProjectPartnerReportExpenditureItemDTO.PartnerRoleEnum;
  constants = ProjectVerificationReportExpenditureConstants;
  EXPENDITURE_CONTROL = ProjectVerificationReportExpenditureConstants.EXPENDITURE_FORM_CONTROL_NAMES.expenditure;
  VERIFICATION_CONTROL = ProjectVerificationReportExpenditureConstants.EXPENDITURE_FORM_CONTROL_NAMES.verification;
  VERIFICATION_DATA = ProjectVerificationReportExpenditureConstants.EXPENDITURE_FORM_CONTROL_NAMES.verificationData;
  columnsToDisplay: string[] = [
    'partner',
    'report',
    'costItemID',
    'costGDPR',
    'parkedBy',
    'costOption',
    'costCategory',
    'investment',
    'contract',
    'internalReferenceNumber',
    'invoiceNumber',
    'invoiceDate',
    'dateOfPayment',
    'description',
    'comment',
    'totalValueInvoice',
    'vat',
    'numberOfUnits',
    'pricePerUnit',
    'declaredAmount',
    'currencyCode',
    'currencyConversionRate',
    'declaredAmountInEur',
    'attachments',
    'partOfSample',
    'deductedAmount',
    'certifiedAmount',
    'typologyOfErrorId',
    'parked',
    'verificationComment',

    // new columns
    'partOfVerificationSample',
    'deductedByJs',
    'deductedByMa',
    'amountAfterVerification',
    'verificationTypologyOfErrorId',
    'verificationParked',
    'jsmaVerificationComment',
  ];
  columnWidthsToDisplay: any[];

  form: FormGroup;
  tableData: AbstractControl[] = [];

  data$: Observable<{
    projectId: number;
    riskBasedVerification: ProjectReportVerificationRiskBasedDTO;
    aggregatedExpenditures: ProjectReportVerificationExpenditureLineDTO[];
    typologyOfErrors: TypologyErrorsDTO[];
    isEditable: boolean;
    projectReport: ProjectReportDTO;
  }>;

  constructor(
    private expenditureStore: ProjectVerificationReportExpenditureStore,
    private reportPageStore: ProjectReportPageStore,
    private formBuilder: FormBuilder,
    private formService: FormService,
    private customTranslatePipe: CustomTranslatePipe,
    private translateByInputLanguagePipe: TranslateByInputLanguagePipe,
    private projectReportDetailStore: ProjectReportDetailPageStore
  ) {
    this.initForm();

    this.data$ = combineLatest([
      this.expenditureStore.projectId$,
      this.expenditureStore.riskBasedVerification$,
      this.expenditureStore.aggregatedExpenditures$,
      this.expenditureStore.typologyOfErrors$,
      this.expenditureStore.isEditable$,
      this.projectReportDetailStore.projectReport$
    ]).pipe(
      map(([projectId, riskBasedVerification, aggregatedExpenditures, typologyOfErrors, isEditable, projectReport]) => ({
        projectId,
        riskBasedVerification,
        aggregatedExpenditures,
        typologyOfErrors,
        isEditable,
        projectReport
      })),
      tap(data => this.resetForm(data.aggregatedExpenditures)),
    );
    this.setColumnWidths();
  }

  private initForm(): void {
    this.form = this.formBuilder.group({
      expenditureLines: this.formBuilder.array([])
    });
    this.formService.init(this.form, this.expenditureStore.isEditable$);
  }

  get expenditureLines(): FormArray {
    return this.form.get(this.constants.EXPENDITURE_FORM_CONTROL_NAMES.expenditureLines) as FormArray;
  }

  private expenditure(item: AbstractControl) {
    return item.get(this.constants.EXPENDITURE_FORM_CONTROL_NAMES.expenditureData);
  }

  expenditureItem(item: AbstractControl, control: string) {
    return this.expenditure(item)?.get(control)?.value;
  }

  verification(item: AbstractControl) {
    return item.get(this.constants.EXPENDITURE_FORM_CONTROL_NAMES.verificationData);
  }

  verificationItem(item: AbstractControl, control: string) {
    return this.verification(item)?.get(control)?.value;
  }

  private patchVerificationValue(item: AbstractControl, control: string, value: any) {
    this.verification(item)?.get(control)?.patchValue(value);
  }

  resetForm(aggregatedExpenditures: ProjectReportVerificationExpenditureLineDTO[]) {
    this.expenditureLines.clear();

    aggregatedExpenditures
      .map(expenditureLine => this.createExpenditureFormControl(expenditureLine))
      .forEach(expenditureLine => this.expenditureLines.push(expenditureLine));

    this.tableData = [...this.expenditureLines.controls];
    this.formService.resetEditable();
    this.disableParkToggleForReIncludedOrDeletedExpenditures(this.expenditureLines);
  }

  private setColumnWidths() {
    this.columnWidthsToDisplay = [
      {minInRem: 2.5, maxInRem: 2.5}, // partner
      {minInRem: 2.5, maxInRem: 2.5}, // report
      {minInRem: 2.5, maxInRem: 2.5}, // id;
      {minInRem: 1, maxInRem: 1},     // gdpr
      {minInRem: 6, maxInRem: 6},     // parked by
      {minInRem: 11, maxInRem: 11},   // cost option
      {minInRem: 11, maxInRem: 11},   // cost category
      {minInRem: 8, maxInRem: 8},     // investment
      {minInRem: 8, maxInRem: 8},     // contract
      {minInRem: 5, maxInRem: 8},     // internal reference
      {minInRem: 5, maxInRem: 8},     // invoice number
      {minInRem: 8, maxInRem: 8},     // invoice date
      {minInRem: 8, maxInRem: 8},     // payment date
      {minInRem: 16},                 // description
      {minInRem: 16},                 // comment
      {minInRem: 8, maxInRem: 8},     // total invoice value
      {minInRem: 8, maxInRem: 8},     // vat
      {minInRem: 8, maxInRem: 8},     // number of units
      {minInRem: 8, maxInRem: 8},     // price per unit
      {minInRem: 8, maxInRem: 8},     // declared amount
      {minInRem: 5, maxInRem: 5},     // currency
      {minInRem: 5, maxInRem: 5},     // conversion rate
      {minInRem: 8, maxInRem: 8},     // declared amount in EUR
      {minInRem: 13, maxInRem: 16},   // attachment
      // CONTROL VERIFICATION
      {minInRem: 3, maxInRem: 4},     // partOfSample
      {minInRem: 7, maxInRem: 8},     // deductedAmount
      {minInRem: 7, maxInRem: 8},     // certifiedAmount
      {minInRem: 8, maxInRem: 10},    // typologyOfError
      {minInRem: 3, maxInRem: 4},     // parked
      {minInRem: 15, maxInRem: 20},   // verificationComment
      // JS/MA VERIFICATION
      {minInRem: 4, maxInRem: 4},     // part of sample
      {minInRem: 7, maxInRem: 8},     // deducted by js
      {minInRem: 7, maxInRem: 8},     // deducted by ma
      {minInRem: 7, maxInRem: 8},     // amount after verification
      {minInRem: 8, maxInRem: 10},    // typologyOfError
      {minInRem: 3, maxInRem: 4},     // parked
      {minInRem: 15, maxInRem: 20},   // verification comment
    ];
    // );
  }


  save() {
    const updateExpenditureVerificationDTO = this.getExpenditureVerification();
    this.expenditureStore.updateExpenditureVerification(updateExpenditureVerificationDTO)
      .pipe(
        take(1),
        tap(savedExpenditureVerification => this.patchExpenditureVerification(savedExpenditureVerification)),
        tap(() => this.formService.setSuccess('project.application.project.verification.tab.expenditure.risk.form.save.success')),
        catchError(err => this.formService.setError(err)),
      ).subscribe();
  }

  private createExpenditureFormControl(expenditureLine: ProjectReportVerificationExpenditureLineDTO): FormGroup {
    return this.formBuilder.group({
      expenditure: this.createExpenditureDataForm(expenditureLine.expenditure),
      verification: this.createExpenditureVerificationForm(expenditureLine)
    });
  }

  private createExpenditureDataForm(expenditureLine: ProjectPartnerReportExpenditureItemDTO): FormGroup {
    return this.formBuilder.group({
      id: this.formBuilder.control(expenditureLine.id),
      number: this.formBuilder.control(expenditureLine.number),
      partnerId: this.formBuilder.control(expenditureLine.partnerId),
      partnerRole: this.formBuilder.control(expenditureLine.partnerRole),
      partnerNumber: this.formBuilder.control(expenditureLine.partnerNumber),
      partnerReportId: this.formBuilder.control(expenditureLine.partnerReportId),
      partnerReportNumber: this.formBuilder.control(expenditureLine.partnerReportNumber),
      lumpSum: this.formBuilder.control(expenditureLine.lumpSum),
      unitCost: this.formBuilder.control(expenditureLine.unitCost),
      gdpr: this.formBuilder.control(expenditureLine.gdpr),
      costCategory: this.formBuilder.control(expenditureLine.costCategory),
      investment: this.formBuilder.control(expenditureLine.investment),
      contract: this.formBuilder.control(expenditureLine.contract),
      internalReferenceNumber: this.formBuilder.control(expenditureLine.internalReferenceNumber),
      invoiceNumber: this.formBuilder.control(expenditureLine.invoiceNumber),
      invoiceDate: this.formBuilder.control(expenditureLine.invoiceDate),
      dateOfPayment: this.formBuilder.control(expenditureLine.dateOfPayment),
      description: this.formBuilder.control(expenditureLine.description),
      comment: this.formBuilder.control(expenditureLine.comment),
      totalValueInvoice: this.formBuilder.control(expenditureLine.totalValueInvoice),
      vat: this.formBuilder.control(expenditureLine.vat),
      numberOfUnits: this.formBuilder.control(expenditureLine.numberOfUnits),
      pricePerUnit: this.formBuilder.control(expenditureLine.pricePerUnit),
      declaredAmount: this.formBuilder.control(expenditureLine.declaredAmount),
      currencyCode: this.formBuilder.control(expenditureLine.currencyCode),
      currencyConversionRate: this.formBuilder.control(expenditureLine.currencyConversionRate),
      declaredAmountAfterSubmission: this.formBuilder.control(expenditureLine.declaredAmountAfterSubmission),
      attachment: this.formBuilder.control(expenditureLine.attachment),
      partOfSample: this.formBuilder.control(expenditureLine.partOfSample),
      partOfSampleLocked: this.formBuilder.control(expenditureLine.partOfSampleLocked),
      certifiedAmount: this.formBuilder.control(expenditureLine.certifiedAmount),
      deductedAmount: this.formBuilder.control(expenditureLine.deductedAmount),
      typologyOfErrorId: this.formBuilder.control(expenditureLine.typologyOfErrorId),
      parked: this.formBuilder.control(expenditureLine.parked),
      verificationComment: this.formBuilder.control(expenditureLine.verificationComment, Validators.maxLength(this.constants.MAX_LENGTH_VERIFY_COMMENT)),
      parkingMetadata: this.formBuilder.control(expenditureLine.parkingMetadata),
    });
  }

  private createExpenditureVerificationForm(verificationExpenditure: ProjectReportVerificationExpenditureLineDTO): FormGroup {
    return this.formBuilder.group({
      expenditureId: this.formBuilder.control(verificationExpenditure.expenditure.id),
      partOfVerificationSample: this.formBuilder.control(verificationExpenditure.partOfVerificationSample),
      deductedByJs: this.formBuilder.control(verificationExpenditure.deductedByJs),
      deductedByMa: this.formBuilder.control(verificationExpenditure.deductedByMa),
      amountAfterVerification: this.formBuilder.control(verificationExpenditure.amountAfterVerification),
      typologyOfErrorId: this.formBuilder.control(verificationExpenditure.typologyOfErrorId),
      parked: this.formBuilder.control(verificationExpenditure.parked),
      parkedOn: this.formBuilder.control(verificationExpenditure.parkedOn),
      parkingMetadata: this.formBuilder.control(verificationExpenditure.parkingMetadata),
      verificationComment: this.formBuilder.control(verificationExpenditure.verificationComment, Validators.maxLength(this.constants.MAX_LENGTH_VERIFY_COMMENT)),
    });
  }

  private getExpenditureVerification(): ProjectReportVerificationExpenditureLineUpdateDTO[] {
    return this.expenditureLines.controls
      .map(item => ({
        expenditureId: this.verificationItem(item, this.VERIFICATION_CONTROL.expenditureId),
        partOfVerificationSample: this.verificationItem(item, this.VERIFICATION_CONTROL.partOfVerificationSample),
        deductedByJs: this.verificationItem(item, this.VERIFICATION_CONTROL.deductedByJs),
        deductedByMa: this.verificationItem(item, this.VERIFICATION_CONTROL.deductedByMa),
        typologyOfErrorId: this.verificationItem(item, this.VERIFICATION_CONTROL.typologyOfErrorId),
        parked: this.verificationItem(item, this.VERIFICATION_CONTROL.parked),
        verificationComment: this.verificationItem(item, this.VERIFICATION_CONTROL.verificationComment),
      }) as ProjectReportVerificationExpenditureLineUpdateDTO);
  }


  private patchExpenditureVerification(expenditureVerification: ProjectReportVerificationExpenditureLineDTO[]) {
    const verificationMap = new Map(expenditureVerification.map(item => [item.expenditure.id, item]));
    this.expenditureLines.controls
      .map(item => {
        const expenditureId = this.verificationItem(item, this.VERIFICATION_CONTROL.expenditureId);
        const verification = verificationMap.get(expenditureId);
        this.patchVerificationValue(item, this.VERIFICATION_CONTROL.partOfVerificationSample, verification?.partOfVerificationSample);
        this.patchVerificationValue(item, this.VERIFICATION_CONTROL.deductedByJs, verification?.deductedByJs);
        this.patchVerificationValue(item, this.VERIFICATION_CONTROL.deductedByMa, verification?.deductedByMa);
        this.patchVerificationValue(item, this.VERIFICATION_CONTROL.amountAfterVerification, verification?.amountAfterVerification);
        this.patchVerificationValue(item, this.VERIFICATION_CONTROL.typologyOfErrorId, verification?.typologyOfErrorId);
        this.patchVerificationValue(item, this.VERIFICATION_CONTROL.parked, verification?.parked);
        this.patchVerificationValue(item, this.VERIFICATION_CONTROL.verificationComment, verification?.verificationComment);
        this.patchVerificationValue(item, this.VERIFICATION_CONTROL.parkedOn, verification?.parkedOn);
      });

  }


  getCostOptionsDefinition(item: AbstractControl): Observable<string> {
    const PERIOD_PREPARATION = 0;
    const PERIOD_CLOSURE = 255;

    const lumpSum = this.expenditureItem(item, this.EXPENDITURE_CONTROL.lumpSum) as ProjectPartnerReportLumpSumDTO;
    const unitCost = this.expenditureItem(item, this.EXPENDITURE_CONTROL.unitCost);

    if (unitCost) {
      const prefix = unitCost.projectDefined ? 'E.2.1_' : '';
      return this.translateByInputLanguagePipe.transform(unitCost.name).pipe(map(n => prefix + n));
    } else if (lumpSum) {
      let postfix = '';
      if (lumpSum.period && lumpSum.period === PERIOD_PREPARATION) {
        postfix = '-' + this.customTranslatePipe.transform('project.application.form.section.part.e.period.preparation');
      } else if (lumpSum.period && lumpSum.period === PERIOD_CLOSURE) {
        postfix = '-' + this.customTranslatePipe.transform('project.application.form.section.part.e.period.preparation');
      } else if (lumpSum.period) {
        postfix = '-' + lumpSum.period;
      }
      return this.translateByInputLanguagePipe.transform(lumpSum.name).pipe(map(n => n + postfix));
    } else {
      return of(this.customTranslatePipe.transform('common.not.applicable.option'));
    }
  }

  getIdentifier(item: AbstractControl): string {
    const parkingMetadata = this.expenditureItem(item, this.EXPENDITURE_CONTROL.parkingMetadata) as ExpenditureParkingMetadataDTO;
    const partnerReportNumber = this.expenditureItem(item, this.EXPENDITURE_CONTROL.partnerReportNumber);
    const expenditureNumber = this.expenditureItem(item, this.EXPENDITURE_CONTROL.number);


    return !parkingMetadata?.reportOfOriginNumber ? `R${partnerReportNumber}.${expenditureNumber}`
      : `R${parkingMetadata?.reportOfOriginNumber}.${parkingMetadata?.originalExpenditureNumber}`;
  }

  getLimitedTextInputTooltip(content: string | null, limit: number): string {
    return content ? `${content} (${content.length}/${limit})` : '';
  }

  getValueOrZero(value: number): number {
    return value ?? 0;
  }

  getTypologyOfError(typologyOfErrors: TypologyErrorsDTO[], typologyOfErrorId: number) {
    return typologyOfErrors.find(error => error.id === typologyOfErrorId);
  }

  getTypologyOfErrorsTooltip(typologyOfErrors: TypologyErrorsDTO[], selectedValue: string): string {
    if (typologyOfErrors.length < 1) {
      return this.customTranslatePipe.transform('project.application.partner.report.control.expenditure.typology.error.warning');
    }
    return selectedValue;
  }

  onDownloadFile(partnerId: number, fileId: number) {
    this.expenditureStore.downloadFile(partnerId, fileId)
      .pipe(take(1))
      .subscribe();
  }

  parkedChange(item: FormControl, event: MatSlideToggleChange) {
    const deductedByJsControl = this.verification(item)?.get(this.VERIFICATION_CONTROL.deductedByJs);
    const deductedByMaControl = this.verification(item)?.get(this.VERIFICATION_CONTROL.deductedByMa);

    if (event.source.checked) {
      deductedByJsControl?.setValue(0);
      deductedByMaControl?.setValue(0);

      deductedByJsControl?.disable();
      deductedByMaControl?.disable();

      this.setAndDisablePartOfSample(item);

    } else {
      deductedByJsControl?.enable();
      deductedByMaControl?.enable();
    }
    this.deductedChanged(item);
  }

  deductedChanged(item: FormControl) {
    const certified = this.expenditureItem(item, this.EXPENDITURE_CONTROL.certifiedAmount);
    const deductedByJs = this.verificationItem(item, this.VERIFICATION_CONTROL.deductedByJs);
    const deductedByMa = this.verificationItem(item, this.VERIFICATION_CONTROL.deductedByMa);
    const parked = this.verificationItem(item, this.VERIFICATION_CONTROL.parked);
    this.verification(item)?.get(this.VERIFICATION_CONTROL.amountAfterVerification)?.patchValue(
      parked ? 0.0 : certified - deductedByJs - deductedByMa
    );
    this.setTypologyOfError(item, deductedByJs == 0 && deductedByMa == 0);
    this.setAndDisablePartOfSample(item);
  }

  getInvestmentIdentifier(investment?: ExpenditureInvestmentBreakdownLineDTO) {
    return of(investment ? `I${investment.workPackageNumber}.${investment.investmentNumber}`
      : this.customTranslatePipe.transform('common.not.applicable.option') as string);
  }

  getContractIdentifier(contract?: ProjectPartnerReportProcurementDTO) {
    return of(contract ? contract?.contractName
      : this.customTranslatePipe.transform('common.not.applicable.option') as string);
  }

  getContractId(contract?: ProjectPartnerReportProcurementDTO) {
    return contract?.id;
  }

  getProcurementLinkForItem(projectID: number, expenditureItem: AbstractControl): string {
    return '/app/project/detail/' + projectID
      + '/reporting/' + this.expenditureItem(expenditureItem, this.EXPENDITURE_CONTROL.partnerId)
      + '/reports/' + this.expenditureItem(expenditureItem, this.EXPENDITURE_CONTROL.partnerReportId)
      + '/procurements/' + this.getContractId(this.expenditure(expenditureItem)?.get(this.EXPENDITURE_CONTROL.contract)?.value);
  }

  getTooltipForParkedExpenditures(item: AbstractControl, projectReport: ProjectReportDTO): String {
    if (this.expenditureItem(item, this.EXPENDITURE_CONTROL.parked)) {
      return this.customTranslatePipe.transform('project.application.project.verification.tab.expenditure.table.disabled.fields.hover.message.parked.by.controller');
    } else if (this.reIncludedOrDeleted(item)) {
      return this.customTranslatePipe.transform('project.application.project.verification.tab.expenditure.table.disabled.fields.hover.message.parked.before.reopening');
    } else {
      return '';
    }
  }

  getParkedByControlOrJsMa(item: AbstractControl): boolean {
    return this.expenditureItem(item, this.EXPENDITURE_CONTROL.parked) || this.verificationItem(item, this.VERIFICATION_CONTROL.parked);
  }

  getParkedBy(item: AbstractControl): ExpenditureParkedByEnum {
    const parkedByControl = this.expenditureItem(item, this.EXPENDITURE_CONTROL.parked);
    const parkingMetadata = this.expenditureItem(item, this.EXPENDITURE_CONTROL.parkingMetadata);

    return ExpenditureItemParkedByChipComponent.getParkedBy(parkingMetadata, parkedByControl);
  }
  isExpenditureParkedOrFormDisabled(item: AbstractControl, isFormEditable: boolean): boolean {
    return this.getParkedByControlOrJsMa(item) || !isFormEditable;
  }

  private reIncludedOrDeleted(item: AbstractControl): boolean {
      const parked = this.verification(item)?.get(this.VERIFICATION_CONTROL.parked)?.value;
      const parkedOn = this.verification(item)?.get(this.VERIFICATION_CONTROL.parkedOn)?.value;
      if (parked && parkedOn == null ) {
          return true;
      }
      return false;
  }

  private setAndDisablePartOfSample(item: FormControl) {
    const deductedByJs = this.verificationItem(item, this.VERIFICATION_CONTROL.deductedByJs);
    const deductedByMa = this.verificationItem(item, this.VERIFICATION_CONTROL.deductedByMa);
    const parked = this.verificationItem(item, this.VERIFICATION_CONTROL.parked);
    const partOfSample = this.verification(item)?.get(this.VERIFICATION_CONTROL.partOfVerificationSample);

    if (deductedByJs != 0 || deductedByMa != 0 || parked) {
      partOfSample?.patchValue(true);
      partOfSample?.disable();
    } else {
      partOfSample?.enable();
    }
  }

  private setTypologyOfError(item: AbstractControl, clear: boolean) {
    const typologyOfError = this.verification(item)?.get(this.VERIFICATION_CONTROL.typologyOfErrorId);
    if (clear) {
      typologyOfError?.setValue(null);
      typologyOfError?.setErrors(null);
      typologyOfError?.clearValidators();
      typologyOfError?.updateValueAndValidity();
    } else {
      typologyOfError?.setValidators([Validators.required]);
      typologyOfError?.setErrors({required: true});
      typologyOfError?.markAsDirty();
      typologyOfError?.updateValueAndValidity();
    }
  }

  private disableParkToggleForReIncludedOrDeletedExpenditures(expenditureLines: FormArray ) {
      expenditureLines.controls.forEach(control =>
          this.reIncludedOrDeleted(control) ? this.verification(control)?.disable() : ''
      );
  }
}
