import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit,} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {catchError, map, take, tap} from 'rxjs/operators';
import {HttpErrorResponse} from '@angular/common/http';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable, of} from 'rxjs';
import {
  CurrencyDTO,
  IdNamePairDTO,
  ProjectPartnerControlReportExpenditureVerificationDTO,
  ProjectPartnerControlReportExpenditureVerificationUpdateDTO,
  ProjectPartnerReportDTO,
  ProjectPartnerReportExpenditureCostDTO,
  ProjectPartnerReportLumpSumDTO,
  ProjectPartnerReportUnitCostDTO,
  TypologyErrorsDTO
} from '@cat/api';
import {
  InvestmentSummary
} from '@project/work-package/project-work-package-page/work-package-detail-page/workPackageInvestment';
import {CurrencyCodesEnum} from '@common/services/currency.store';
import {
  PartnerFileManagementStore
} from '@project/project-application/report/partner-report-detail-page/partner-file-management-store';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {RoutingService} from '@common/services/routing.service';
import {CustomTranslatePipe} from '@common/pipe/custom-translate-pipe';
import {TranslateByInputLanguagePipe} from '@common/pipe/translate-by-input-language.pipe';
import {
  PartnerControlReportExpenditureConstants
} from '@project/project-application/report/partner-control-report/partner-control-expenditure-verification-tab/partner-control-report-expenditure-verification-tab.constants';
import {
  PartnerControlReportFileExpenditureVerificationStore
} from '@project/project-application/report/partner-control-report/partner-control-expenditure-verification-tab/partner-control-report-file-expenditure-verification-store';
import {Alert} from '@common/components/forms/alert';
import {MatSlideToggleChange} from '@angular/material/slide-toggle';

@UntilDestroy()
@Component({
  selector: 'jems-partner-control-report-expenditure-verification-page',
  templateUrl: './partner-control-report-expenditure-verification-tab.component.html',
  styleUrls: ['./partner-control-report-expenditure-verification-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerControlReportExpenditureVerificationTabComponent implements OnInit {
  Alert = Alert;
  reportExpendituresForm: FormGroup;
  tableData: AbstractControl[] = [];
  constants = PartnerControlReportExpenditureConstants;
  currencies: CurrencyDTO[];
  currentReport: ProjectPartnerReportDTO;
  isReportEditable$: Observable<boolean>;
  data$: Observable<{
    expendituresCosts: ProjectPartnerReportExpenditureCostDTO[];
    costCategories: string[];
    investmentsSummary: InvestmentSummary[];
    contractIDs: IdNamePairDTO[];
    unitCosts: ProjectPartnerReportUnitCostDTO[];
    lumpSums: ProjectPartnerReportLumpSumDTO[];
    typologyOfErrors: TypologyErrorsDTO[];
  }>;
  reportCosts$: Observable<{
    unitCosts: ProjectPartnerReportUnitCostDTO[];
    lumpSums: ProjectPartnerReportLumpSumDTO[];
  }>;

  lumpSumsAvailable: boolean;
  unitCostsAvailable: boolean;
  columnsToDisplay: string[];
  columnsWidthsToDisplay: any[];
  collapsedColumns: string[];
  collapsedColumnsWidths: any[];

  availableLumpSums: ProjectPartnerReportLumpSumDTO[];
  availableUnitCosts: ProjectPartnerReportUnitCostDTO[];
  availableCurrenciesPerRow: CurrencyDTO[][] = [];
  contractIDs: IdNamePairDTO[] = [];
  investmentsSummary: InvestmentSummary[] = [];

  readonly PERIOD_PREPARATION: number = 0;
  readonly PERIOD_CLOSURE: number = 255;

  constructor(public pageStore: PartnerControlReportFileExpenditureVerificationStore,
              protected changeDetectorRef: ChangeDetectorRef,
              private formBuilder: FormBuilder,
              private formService: FormService,
              private partnerFileManagementStore: PartnerFileManagementStore,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore,
              private router: RoutingService,
              private customTranslatePipe: CustomTranslatePipe,
              private translateByInputLanguagePipe: TranslateByInputLanguagePipe) {
    this.isReportEditable$ = this.pageStore.isEditable$;
  }

  ngOnInit(): void {
    this.initForm();
    combineLatest([
      this.pageStore.investmentsSummary$,
      this.pageStore.reportLumpSums$,
      this.pageStore.reportUnitCosts$
    ]).pipe(
      map(([investments, lumpSums, unitCosts]) => {
          this.setColumnsToDisplay(investments, lumpSums.length > 0 || unitCosts.length > 0);
          this.setColumnsWidths(investments, lumpSums.length > 0 || unitCosts.length > 0);
        }
      ),
      untilDestroyed(this)
    ).subscribe();
    this.pageStore.currentReport$.pipe(untilDestroyed(this)).subscribe(report=> this.currentReport = report);
    this.pageStore.currencies$.pipe(untilDestroyed(this)).subscribe(currencies=> this.currencies = currencies);

    this.dataAsObservable();
  }

  getAvailableCurrenciesByType(type: string | null, unitCost?: any) {
    switch(type) {
      case 'lumpSum': return this.currencies.filter((currency) => currency.code === CurrencyCodesEnum.EUR);
      case 'unitCost': return this.currencies.filter((currency) => currency.code === CurrencyCodesEnum.EUR
        || currency.code === this.availableUnitCosts
          .filter(el => (el.id === unitCost?.value?.id || el.id === unitCost?.id) )[0].foreignCurrencyCode);
      default: return this.currencies;
    }
  }

  disableOnReset(control: FormGroup, index: number): void {
    control.get(this.constants.FORM_CONTROL_NAMES.certifiedAmount)?.disable();
  }

  resetForm(partnerReportExpenditures: ProjectPartnerControlReportExpenditureVerificationDTO[]): void {
    this.availableCurrenciesPerRow = [];
    this.items.clear();
    partnerReportExpenditures.forEach((partnerReportExpenditure, expenditureIndex) => this.addExpenditure(partnerReportExpenditure, expenditureIndex));
    this.tableData = [...this.items.controls];
    this.formService.init(this.reportExpendituresForm, this.pageStore.isEditable$);

    this.items.controls.forEach((formGroup: FormGroup, index) => (
      this.disableOnReset(formGroup, index)));
  }

  updateReportExpendituresControl(): void {
    this.pageStore.updateExpendituresControl(this.formToReportExpenditures()).pipe(
      tap(() => this.formService.setSuccess('project.application.partner.report.expenditures.cost.save.success')),
      catchError((error: HttpErrorResponse) => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe();
  }

  private initForm(): void {
    this.reportExpendituresForm = this.formBuilder.group({
      items: this.formBuilder.array([], Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS))
    });
  }

  private dataAsObservable(): void {
    this.reportCosts$ = combineLatest([
      this.pageStore.reportUnitCosts$,
      this.pageStore.reportLumpSums$,
    ]).pipe(
      map(([unitCosts, lumpSums]) => ({
          unitCosts: unitCosts.map(unit => ({...unit, type: 'unitCost'})),
          lumpSums: lumpSums.map(lumpSum => ({...lumpSum, type: 'lumpSum'}))
        })
      ),
      tap(data => this.lumpSumsAvailable = data.lumpSums.length > 0),
      tap(data => this.unitCostsAvailable = data.unitCosts.length > 0),
      tap(data => this.availableLumpSums = data.lumpSums),
      tap(data => this.availableUnitCosts = data.unitCosts)
    );

    this.data$ = combineLatest([
      this.pageStore.typologyOfErrors$,
      this.pageStore.reportExpenditureControl$,
      this.pageStore.costCategories$,
      this.pageStore.investmentsSummary$,
      this.pageStore.contractIDs$,
      this.reportCosts$,
    ]).pipe(
      map(([typologyOfErrors, expendituresCosts, costCategories, investmentsSummary, contractIDs, reportCosts]) => ({
        typologyOfErrors,
        expendituresCosts,
          costCategories,
          investmentsSummary,
          contractIDs,
          ...reportCosts,
        })
      ),
      tap(data => this.resetForm(data.expendituresCosts)),
      tap(data => this.contractIDs = data.contractIDs),
      tap(data => this.investmentsSummary = data.investmentsSummary),
    );

  }

  private setColumnsToDisplay(investments: InvestmentSummary[], isCostOptionsAvailable: boolean){

    let columnsToDisplay = [];
    const columnsToDisplayFirstPart = [
      'costItemID',
      'costCategory',
    ];

    const columnsToDisplayLastPart = [
      'declaredAmount',
      'currencyCode',
      'currencyConversionRate',
      'declaredAmountInEur',
      'uploadFunction',
    ];

    this.collapsedColumns = [
      'contractId',
      'internalReferenceNumber',
      'invoiceNumber',
      'invoiceDate',
      'dateOfPayment',
      'description',
      'comment',
      'totalValueInvoice',
      'vat',
    ];

    columnsToDisplay = columnsToDisplayFirstPart.concat(this.collapsedColumns);

    if (investments.length > 0) {
      columnsToDisplay.splice(2, 0, 'investmentId');
    }

    if (isCostOptionsAvailable) {
      columnsToDisplay.splice(1, 0, 'costOptions');
      columnsToDisplay.splice(12, 0, 'numberOfUnits');
      columnsToDisplay.splice(13, 0, 'pricePerUnit');
    }

    columnsToDisplay = columnsToDisplay.concat(columnsToDisplayLastPart);

    columnsToDisplay.push('partOfSample');
    columnsToDisplay.push('deductedAmount');
    columnsToDisplay.push('certifiedAmount');
    columnsToDisplay.push('typologyOfErrorId');
    columnsToDisplay.push('parked');
    columnsToDisplay.push('verificationComment');

    this.columnsToDisplay = columnsToDisplay;
  }

  private setColumnsWidths(investments: InvestmentSummary[], isCostOptionsAvailable: boolean) {
    this.columnsWidthsToDisplay = [{minInRem: 1, maxInRem: 3}];
    if (isCostOptionsAvailable) {
      this.columnsWidthsToDisplay.push({minInRem: 11, maxInRem: 16}); // cost options
    }

    this.columnsWidthsToDisplay.push(
      {minInRem: 11, maxInRem: 16}, // cost category
    );

    this.collapsedColumnsWidths = [
      {minInRem: 8, maxInRem: 8},   // contract id
      {minInRem: 5, maxInRem: 8},   // internal reference
      {minInRem: 5, maxInRem: 8},   // invoice number
      {minInRem: 8, maxInRem: 8},   // invoice date
      {minInRem: 8, maxInRem: 8},   // payment date
      {minInRem: 16}, // description
      {minInRem: 16}, // comment
      {minInRem: 8, maxInRem: 8},   // total invoice value
      {minInRem: 8, maxInRem: 8}    // vat
    ];

    if (isCostOptionsAvailable) {
      this.collapsedColumnsWidths.push(
        {minInRem: 8, maxInRem: 8}, // number of units
        {minInRem: 8, maxInRem: 8}  // price per unit
      );
    }
    this.columnsWidthsToDisplay = this.columnsWidthsToDisplay.concat(this.collapsedColumnsWidths);
    this.columnsWidthsToDisplay.push(
      {minInRem: 8, maxInRem: 8},   // declared amount
      {minInRem: 5, maxInRem: 5},   // currency
      {minInRem: 5, maxInRem: 5},   // conversion rate
      {minInRem: 8, maxInRem: 8},   // declared amount in EUR
      {minInRem: 13, maxInRem: 16},  //attachment
      {minInRem: 3, maxInRem: 4},   // partOfSample
      {minInRem: 7, maxInRem: 8},   // certifiedAmount
      {minInRem: 7, maxInRem: 8},   // deductedAmount
      {minInRem: 8, maxInRem: 10},   // typologyOfError
      {minInRem: 3, maxInRem: 4},   // parked
      {minInRem: 15, maxInRem: 20},   // verificationComment
    );

    if (investments.length > 0) {
      this.columnsWidthsToDisplay.splice(2, 0, {minInRem: 11});
    }
  }

  private getUnitCostOrLumpSumObject(reportExpenditureCost: ProjectPartnerReportExpenditureCostDTO): ProjectPartnerReportUnitCostDTO | ProjectPartnerReportLumpSumDTO | undefined {
    return (reportExpenditureCost.lumpSumId !== null ?
      this.availableLumpSums.find(lumpSum => lumpSum.id === reportExpenditureCost.lumpSumId) :
      this.availableUnitCosts.find(unitCost => unitCost.id === reportExpenditureCost.unitCostId));
  }

  getUnitCostType(reportExpenditureCost: ProjectPartnerReportExpenditureCostDTO) {
    if(!reportExpenditureCost.lumpSumId && !reportExpenditureCost.unitCostId) {
      return '';
    }
    return reportExpenditureCost.lumpSumId ? 'lumpSum' : 'unitCost';
  }

  private addExpenditure(reportExpenditureControl: ProjectPartnerControlReportExpenditureVerificationDTO, expenditureIndex: number): void {
    const costOption = this.getUnitCostOrLumpSumObject(reportExpenditureControl);
    this.availableCurrenciesPerRow.push(this.getAvailableCurrenciesByType(this.getUnitCostType(reportExpenditureControl), costOption));
    this.items.push(this.formBuilder.group(
      {
        id: this.formBuilder.control(reportExpenditureControl.id),
        number: this.formBuilder.control(reportExpenditureControl.number),
        costOptions: this.formBuilder.control(costOption),
        costCategory: this.formBuilder.control(reportExpenditureControl.costCategory),
        investmentId: this.formBuilder.control(reportExpenditureControl.investmentId),
        attachment: this.formBuilder.control(reportExpenditureControl.attachment, []),
        partOfSample: this.formBuilder.control(reportExpenditureControl.partOfSample),
        declaredAmountInEur: this.formBuilder.control(reportExpenditureControl.declaredAmountAfterSubmission),
        certifiedAmount: this.formBuilder.control(this.getCertifiedAmount(reportExpenditureControl)),
        deductedAmount: this.formBuilder.control(this.getDeductedAmount(reportExpenditureControl)),
        typologyOfErrorId: this.formBuilder.control(reportExpenditureControl.typologyOfErrorId || null),
        parked: this.formBuilder.control(reportExpenditureControl.parked),
        verificationComment: this.formBuilder.control(reportExpenditureControl.verificationComment, [Validators.maxLength(1000)]),
      })
    );

    if(reportExpenditureControl.deductedAmount) {
      this.items.controls[expenditureIndex].get(this.constants.FORM_CONTROL_NAMES.typologyOfErrorId)?.setValidators([Validators.required]);
    }
  }

  private getCertifiedAmount(reportExpenditureControl: ProjectPartnerControlReportExpenditureVerificationDTO) {
    return reportExpenditureControl.parked ? 0 : reportExpenditureControl.certifiedAmount || reportExpenditureControl.declaredAmountAfterSubmission;
  }

  private getDeductedAmount(reportExpenditureControl: ProjectPartnerControlReportExpenditureVerificationDTO) {
    return reportExpenditureControl.parked ? 0 : reportExpenditureControl.deductedAmount || reportExpenditureControl.declaredAmountAfterSubmission - this.getCertifiedAmount(reportExpenditureControl);
  }

  private formToReportExpenditures(): ProjectPartnerControlReportExpenditureVerificationUpdateDTO[] {
    return this.items.controls.map((formGroup: FormGroup) => ({
      ...formGroup.getRawValue(),
    }));
  }

  get items(): FormArray {
    return this.reportExpendituresForm.get(this.constants.FORM_CONTROL_NAMES.items) as FormArray;
  }

  attachment(index: number): FormControl {
    return this.items.at(index).get(this.constants.FORM_CONTROL_NAMES.attachment) as FormControl;
  }

  onDownloadFile(fileId: number) {
    this.partnerFileManagementStore.downloadFile(fileId)
      .pipe(take(1))
      .subscribe();
  }

  refreshListOfExpenditures(): void {
    this.pageStore.refreshExpenditures$.next(undefined);
  }

  getCostOptionsDefinition(costOption: any): Observable<string> {
    if (!costOption) {
      return of(this.customTranslatePipe.transform('common.not.applicable.option'));
    } else if (costOption.type === 'unitCost') {
      const prefix = costOption.projectDefined ? 'E.2.1_' : '';
      return this.translateByInputLanguagePipe.transform(costOption.name).pipe(map(n => prefix + n));
    } else {
      let postfix = '';
      if (costOption.period && costOption.period === this.PERIOD_PREPARATION) {
        postfix = '-' + this.customTranslatePipe.transform('project.application.form.section.part.e.period.preparation');
      } else if (costOption.period && costOption.period === this.PERIOD_CLOSURE) {
        postfix = '-' + this.customTranslatePipe.transform('project.application.form.section.part.e.period.preparation');
      } else if(costOption.period) {
        postfix = '-' + costOption.period;
      }
      return this.translateByInputLanguagePipe.transform(costOption.name).pipe(map(n => n + postfix));
    }
  }

  getLimitedTextInputTooltip(content: string | null, limit: number): string {
    return content ? `${content} (${content.length}/${limit})` : '';
  }

  getInvestmentIdValue(investmentId: number): string {
    const summary = this.investmentsSummary.find(s => s.id === investmentId);
    return summary?.toString() ?? '';
  }

  getContractName(contractNames: IdNamePairDTO[], contractId: number) {
    const contractName = contractNames.find(contract => contract.id === contractId)?.name;
    return contractName ? contractName : 'N/A';
  }

  getValueOrZero(value: number): number {
    return value ?? 0;
  }

  getTypologyOfErrorsTooltip(typologyOfErrors: [], selectedValue: string): string {
    if(typologyOfErrors.length < 1) {
      return this.customTranslatePipe.transform('project.application.partner.report.control.expenditure.typology.error.warning');
    }
    return selectedValue;
  }

  updateCertifiedAmount(expenditureIndex: number, declaredAmountInEur: number, deductedAmount: number) {
    if(declaredAmountInEur - deductedAmount !== 0) {
      this.items.at(expenditureIndex).get(this.constants.FORM_CONTROL_NAMES.typologyOfErrorId)?.setValidators([Validators.required]);
      this.items.at(expenditureIndex).get(this.constants.FORM_CONTROL_NAMES.typologyOfErrorId)?.setErrors({required: true});
      this.items.at(expenditureIndex).get(this.constants.FORM_CONTROL_NAMES.typologyOfErrorId)?.markAsDirty();
    }
    else {
      this.items.at(expenditureIndex).get(this.constants.FORM_CONTROL_NAMES.typologyOfErrorId)?.clearValidators();
    }
    this.items.at(expenditureIndex).get(this.constants.FORM_CONTROL_NAMES.certifiedAmount)?.setValue(declaredAmountInEur - deductedAmount);
    this.items.at(expenditureIndex)?.get(this.constants.FORM_CONTROL_NAMES.typologyOfErrorId)?.updateValueAndValidity();
  }

  parkedChange(expenditureIndex: number, event: MatSlideToggleChange) {
    if (event.source.checked) {
      this.items.at(expenditureIndex).get(this.constants.FORM_CONTROL_NAMES.deductedAmount)?.setValue(0);
      this.items.at(expenditureIndex).get(this.constants.FORM_CONTROL_NAMES.certifiedAmount)?.setValue(0);
      this.items.at(expenditureIndex).get(this.constants.FORM_CONTROL_NAMES.typologyOfErrorId)?.setValue(null);
      this.items.at(expenditureIndex).get(this.constants.FORM_CONTROL_NAMES.typologyOfErrorId)?.setErrors(null);
      this.items.at(expenditureIndex).get(this.constants.FORM_CONTROL_NAMES.typologyOfErrorId)?.clearValidators();
      this.items.at(expenditureIndex).get(this.constants.FORM_CONTROL_NAMES.typologyOfErrorId)?.updateValueAndValidity();
    } else {
      const declared = this.items.at(expenditureIndex).get(this.constants.FORM_CONTROL_NAMES.declaredAmountInEur)?.value;
      this.items.at(expenditureIndex).get(this.constants.FORM_CONTROL_NAMES.certifiedAmount)?.setValue(declared);
    }
  }
}
