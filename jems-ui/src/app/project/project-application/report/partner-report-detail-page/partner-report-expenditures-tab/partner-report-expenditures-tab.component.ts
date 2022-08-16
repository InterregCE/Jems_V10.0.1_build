import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {catchError, map, take, tap} from 'rxjs/operators';
import {HttpErrorResponse} from '@angular/common/http';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {
  PartnerReportExpendituresTabConstants
} from '@project/project-application/report/partner-report-detail-page/partner-report-expenditures-tab/partner-report-expenditures-tab.constants';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {
  PartnerReportExpendituresStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-expenditures-tab/partner-report-expenditures-store.service';
import {MatSelectChange} from '@angular/material/select/select';
import {
  CurrencyDTO,
  IdNamePairDTO,
  ProjectPartnerReportDTO,
  ProjectPartnerReportExpenditureCostDTO,
  ProjectPartnerReportLumpSumDTO,
  ProjectPartnerReportUnitCostDTO
} from '@cat/api';
import {BudgetCostCategoryEnum} from '@project/model/lump-sums/BudgetCostCategoryEnum';
import {
  InvestmentSummary
} from '@project/work-package/project-work-package-page/work-package-detail-page/workPackageInvestment';
import {CurrencyCodesEnum} from '@common/services/currency.store';
import {NumberService} from '@common/services/number.service';
import {
  PartnerFileManagementStore
} from '@project/project-application/report/partner-report-detail-page/partner-file-management-store';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {RoutingService} from '@common/services/routing.service';
import {v4 as uuid} from 'uuid';

@UntilDestroy()
@Component({
  selector: 'jems-partner-expenditures-cost',
  templateUrl: './partner-report-expenditures-tab.component.html',
  styleUrls: ['./partner-report-expenditures-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportExpendituresTabComponent implements OnInit {
  CurrencyCodesEnum = CurrencyCodesEnum;
  CostCategoryEnum = ProjectPartnerReportExpenditureCostDTO.CostCategoryEnum;
  reportExpendituresForm: FormGroup;
  tableData: AbstractControl[] = [];
  constants = PartnerReportExpendituresTabConstants;
  currencies: CurrencyDTO[];
  currentReport: ProjectPartnerReportDTO;
  isReportEditable$: Observable<boolean>;
  data$: Observable<{
    expendituresCosts: ProjectPartnerReportExpenditureCostDTO[];
    costCategories: string[];
    investmentsSummary: InvestmentSummary[];
    contractIDs: IdNamePairDTO[];
    columnsToDisplay: string[];
    withConfigs: TableConfig[];
    unitCosts: ProjectPartnerReportUnitCostDTO[];
    lumpSums: ProjectPartnerReportLumpSumDTO[];
  }>;
  tableConfiguration$: Observable<{
    columnsToDisplay: string[];
    withConfigs: TableConfig[];
  }>;
  reportCosts$: Observable<{
    unitCosts: ProjectPartnerReportUnitCostDTO[];
    lumpSums: ProjectPartnerReportLumpSumDTO[];
  }>;

  lumpSumsAvailable: boolean;
  unitCostsAvailable: boolean;
  lumpSumHasValue = false;
  unitCostHasValue = false;

  availableLumpSums: ProjectPartnerReportLumpSumDTO[];
  availableUnitCosts: ProjectPartnerReportUnitCostDTO[];
  availableCurrenciesPerRow: CurrencyDTO[][] = [];

  readonly PERIOD_PREPARATION: number = 0;
  readonly PERIOD_CLOSURE: number = 255;

  constructor(public pageStore: PartnerReportExpendituresStore,
              protected changeDetectorRef: ChangeDetectorRef,
              private formBuilder: FormBuilder,
              private formService: FormService,
              private partnerFileManagementStore: PartnerFileManagementStore,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore,
              private router: RoutingService) {
    this.isReportEditable$ = this.pageStore.isEditable$;
  }

  ngOnInit(): void {
    this.initForm();
    this.tableConfiguration$ = combineLatest([
      this.pageStore.investmentsSummary$,
      this.partnerReportDetailPageStore.reportEditable$,
      this.pageStore.reportLumpSums$,
      this.pageStore.reportUnitCosts$
    ]).pipe(
      map(([investments, editable, lumpSums, unitCosts]) => ({
          columnsToDisplay: this.getColumnsToDisplay(investments, editable, lumpSums.length > 0 || unitCosts.length > 0),
          withConfigs: this.getTableConfig(investments, editable, lumpSums.length > 0 || unitCosts.length > 0)
        })
      )
    );
    this.pageStore.currentReport$.pipe(untilDestroyed(this)).subscribe(report=> this.currentReport = report);
    this.pageStore.currencies$.pipe(untilDestroyed(this)).subscribe(currencies=> this.currencies = currencies);

    this.dataAsObservable();
  }

  onCostCategoryChange(change: MatSelectChange, control: FormGroup): void {
    control.patchValue({costCategory: change.value});
    if (this.isStaffCostsSelectedForCostCategoryRow(control) ||
      this.isTravelAndAccommodationSelectedForCostCategoryRow(control)) {
      control.patchValue({investmentId: ''});
      control.get('investmentId')?.disable();
    } else {
      control.get('investmentId')?.enable();
    }
    if (this.isStaffCostsSelectedForCostCategoryRow(control)) {
      control.patchValue({contractId: ''});
      control.patchValue({invoiceNumber: ''});
      control.patchValue({vat: ''});
      control.get('vat')?.disable();
      control.get('contractId')?.disable();
      control.get('invoiceNumber')?.disable();
    } else {
      control.get('vat')?.enable();
      control.get('contractId')?.enable();
      control.get('invoiceNumber')?.enable();
    }
  }

  onCostOptionChange(change: any, control: FormGroup, index: number): void {
    this.clearFieldsOnCostOptionSelection(control);

    if (change.value === null) {
      this.availableCurrenciesPerRow[index] = this.currencies;
      this.lumpSumHasValue = false;
      this.setControlsDisableStatusForNoCostOption(control);
    } else {
      if (change.value['type'] === 'lumpSum') {
        this.lumpSumHasValue = true;
        this.availableCurrenciesPerRow[index] =  this.getAvailableCurrenciesByType('lumpSum');
        control.patchValue({costCategory: this.CostCategoryEnum.Multiple});
        control.patchValue({numberOfUnits: 1});
        control.patchValue({pricePerUnit: this.availableLumpSums.filter(lumpSum => lumpSum.id === change.value['id'])[0].cost});
        control.patchValue({declaredAmount: this.availableLumpSums.filter(lumpSum => lumpSum.id === change.value['id'])[0].cost});
        control.patchValue({currencyCode: CurrencyCodesEnum.EUR});
        control.patchValue({currencyConversionRate: this.getConversionRateByCode(CurrencyCodesEnum.EUR)});
        control.patchValue({declaredAmountInEur: this.availableLumpSums.filter(lumpSum => lumpSum.id === change.value['id'])[0].cost});

        this.disableCostOptionSelectionRelatedFields(control, 'lumpSum', index);
      } else {
        const unitCost = this.availableUnitCosts.filter(uc => uc.id === change.value['id'])[0];
        const currencyCode = this.getUnitCostCurrency(unitCost);
        const currencyConversionRate = this.getConversionRateByCode(currencyCode);
        const pricePerUnit = this.getUnitCostPricePerUnitByCurrency(unitCost, currencyCode);
        this.availableCurrenciesPerRow[index] = this.getAvailableCurrenciesByType('unitCost', change);
        this.unitCostHasValue = true;

        control.patchValue({
          costCategory: unitCost.category,
          numberOfUnits: 1,
          pricePerUnit,
          declaredAmount: pricePerUnit,
          currencyCode,
          currencyConversionRate,
          declaredAmountInEur: this.getAmountInEur(currencyConversionRate, pricePerUnit),
        });
        this.disableCostOptionSelectionRelatedFields(control, 'unitCost', index);
      }
    }
  }

  getAvailableCurrenciesByType(type: string | null, unitCost?: any) {
    switch(type) {
      case 'lumpSum': return this.currencies.filter((currency) => currency.code === CurrencyCodesEnum.EUR);
      case 'unitCost': return this.currencies.filter((currency) => currency.code === CurrencyCodesEnum.EUR || currency.code === this.availableUnitCosts.filter(el => (el.id === unitCost?.value?.id || el.id === unitCost?.id) )[0].foreignCurrencyCode);
      default: return this.currencies;
    }
  }

  setControlsDisableStatusForNoCostOption(control: FormGroup) {
    control.get('costCategory')?.enable();
    control.get('contractId')?.enable();
    control.get('internalReferenceNumber')?.enable();
    control.get('invoiceNumber')?.enable();
    control.get('invoiceDate')?.enable();
    control.get('dateOfPayment')?.enable();
    control.get('totalValueInvoice')?.enable();
    control.get('vat')?.enable();
    control.get('numberOfUnits')?.disable();
    control.get('pricePerUnit')?.disable();
    control.get('declaredAmount')?.enable();
    control.get('currencyCode')?.enable();
    control.get('currencyConversionRate')?.enable();
    control.get('declaredAmountInEur')?.enable();
    control.get('investmentId')?.enable();
  }

  getUnitCostPricePerUnitByCurrency(unitCost: ProjectPartnerReportUnitCostDTO, selectedCurrency: string): number {
    return selectedCurrency ? unitCost.foreignCurrencyCode === selectedCurrency ? unitCost.costPerUnitForeignCurrency : unitCost.costPerUnit : 0;
  }

  getUnitCostDeclaredAmountByCurrency(expenditureIndex: number): number {
    return NumberService.product([this.items.at(expenditureIndex).get('pricePerUnit')?.value, this.items.at(expenditureIndex).get('numberOfUnits')?.value]);
  }

  getUnitCostCurrency(unitCost: ProjectPartnerReportUnitCostDTO): string {
    return  this.hasPartnerCurrencySetToEur() ? CurrencyCodesEnum.EUR : unitCost.foreignCurrencyCode ? '' : CurrencyCodesEnum.EUR;
  }

  disableOnReset(control: FormGroup, index: number): void {
    if (this.isStaffCostsSelectedForCostCategoryRow(control) ||
      this.isTravelAndAccommodationSelectedForCostCategoryRow(control)) {
      control.get('investmentId')?.disable();
    }
    if (this.isStaffCostsSelectedForCostCategoryRow(control)) {
      control.get('vat')?.disable();
      control.get('contractId')?.disable();
      control.get('invoiceNumber')?.disable();
    }
    if((control?.get(this.constants.FORM_CONTROL_NAMES.costOptions) as FormControl)?.value !== null) {
      this.disableCostOptionSelectionRelatedFields(control, control.value?.costOptions?.type, index);
    }
  }

  onInvestmentNumberChange(change: MatSelectChange, control: FormGroup): void {
    control.patchValue({investmentId: change.value});
  }

  isStaffCostsSelectedForCostCategoryRow(control: FormGroup): boolean {
    return (control?.get(this.constants.FORM_CONTROL_NAMES.costCategory) as FormControl)?.value
      === BudgetCostCategoryEnum.STAFF_COSTS;
  }

  isTravelAndAccommodationSelectedForCostCategoryRow(control: FormGroup): boolean {
    return (control?.get(this.constants.FORM_CONTROL_NAMES.costCategory) as FormControl)?.value
      === BudgetCostCategoryEnum.TRAVEL_AND_ACCOMMODATION_COSTS;
  }

  resetForm(partnerReportExpenditures: ProjectPartnerReportExpenditureCostDTO[]): void {
    this.availableCurrenciesPerRow = [];
    this.items.clear();
    partnerReportExpenditures.forEach(partnerReportExpenditure => this.addExpenditure(partnerReportExpenditure));
    this.tableData = [...this.items.controls];
    this.formService.resetEditable();
    this.items.controls.forEach((formGroup: FormGroup, index) => (
      this.disableOnReset(formGroup, index)));

    setTimeout(() => this.changeDetectorRef.detectChanges());
  }

  removeItem(index: number): void {
    this.items.removeAt(index);
    this.availableCurrenciesPerRow.splice(index, 1);
    this.tableData = [...this.items.controls];
    this.formService.setDirty(true);
  }

  addNewItem(): void {
    const item = this.formBuilder.group({
      id: null,
      costOptions: null,
      costCategory: ['', Validators.required],
      investmentId: '',
      contractId: '',
      internalReferenceNumber: ['', Validators.maxLength(30)],
      invoiceNumber: ['', Validators.maxLength(30)],
      invoiceDate: '',
      dateOfPayment: '',
      description: [[]],
      comment: [[]],
      totalValueInvoice: 0,
      vat: 0,
      numberOfUnits: 0,
      pricePerUnit: 0,
      declaredAmount: 0,
      currencyCode: this.currentReport.identification?.currency ,
      currencyConversionRate: this.getConversionRateByCode(this.currentReport.identification?.currency),
      declaredAmountInEur: 0,
      attachment: [],
    });
    this.items.push(item);
    item.get('numberOfUnits')?.disable();
    item.get('pricePerUnit')?.disable();
    this.tableData = [...this.items.controls];
    this.formService.setDirty(true);
    this.availableCurrenciesPerRow.push(this.getAvailableCurrenciesByType(null));

    setTimeout(() => this.changeDetectorRef.detectChanges());
  }

  updateReportExpenditures(): void {
    this.pageStore.updateExpenditures(this.formToReportExpenditures()).pipe(
      tap(() => this.formService.setSuccess('project.application.partner.report.expenditures.cost.save.success')),
      catchError((error: HttpErrorResponse) => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe();
  }

  lengthOfForm(formGroup: FormGroup, formControlName: string): number {
    return formGroup.get(formControlName)?.value?.length;
  }

  private initForm(): void {
    this.reportExpendituresForm = this.formBuilder.group({
      items: this.formBuilder.array([], Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS))
    });
    this.formService.init(this.reportExpendituresForm, this.pageStore.isEditable$);
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
      this.pageStore.expendituresCosts$,
      this.pageStore.costCategories$,
      this.pageStore.investmentsSummary$,
      this.pageStore.contractIDs$,
      this.tableConfiguration$,
      this.reportCosts$
    ]).pipe(
      map(([expendituresCosts, costCategories, investmentsSummary, contractIDs, tableConfiguration, reportCosts]) => ({
          expendituresCosts,
          costCategories,
          investmentsSummary,
          contractIDs,
          ...tableConfiguration,
          ...reportCosts
        })
      ),
      tap(data => this.resetForm(data.expendituresCosts))
    );

  }

  private getColumnsToDisplay(investments: InvestmentSummary[], isEditable: boolean, isCostOptionsAvailable: boolean): string[] {
    const columnsToDisplay = [
      'costItemID',
      'costCategory',
      'contractId',
      'internalReferenceNumber',
      'invoiceNumber',
      'invoiceDate',
      'dateOfPayment',
      'description',
      'comment',
      'totalValueInvoice',
      'vat',
      'declaredAmount',
      'currencyCode',
      'currencyConversionRate',
      'declaredAmountInEur',
      'uploadFunction',
    ];
    if (isEditable) {
      columnsToDisplay.push('actions');
    }
    if (investments.length > 0) {
      columnsToDisplay.splice(2, 0, 'investmentId');
    }
    if (isCostOptionsAvailable) {
      columnsToDisplay.splice(1, 0, 'costOptions');
      columnsToDisplay.splice(12, 0, 'numberOfUnits');
      columnsToDisplay.splice(13, 0, 'pricePerUnit');
    }
    return columnsToDisplay;
  }

  private getTableConfig(investments: InvestmentSummary[], isEditable: boolean, isCostOptionsAvailable: boolean): TableConfig[] {
    const tableConfig: TableConfig[] = [{minInRem: 1, maxInRem: 3}]; // id

    if (isCostOptionsAvailable) {
      tableConfig.push({minInRem: 11, maxInRem: 16}); // cost options
    }
    tableConfig.push(
      {minInRem: 11, maxInRem: 16}, // cost category
      {minInRem: 8, maxInRem: 8},   // contract id
      {minInRem: 5, maxInRem: 8},   // internal reference
      {minInRem: 5, maxInRem: 8},   // invoice number
      {minInRem: 8, maxInRem: 8},   // invoice date
      {minInRem: 8, maxInRem: 8},   // payment date
      {minInRem: 16}, // description
      {minInRem: 16}, // comment
      {minInRem: 8, maxInRem: 8},   // total invoice value
      {minInRem: 8, maxInRem: 8}    // vat
    );
    if (isCostOptionsAvailable) {
      tableConfig.push(
        {minInRem: 8, maxInRem: 8}, // number of units
        {minInRem: 8, maxInRem: 8}  // price per unit
      );
    }

     tableConfig.push(
      {minInRem: 8, maxInRem: 8},   // declared amount
      {minInRem: 5, maxInRem: 5},   // currency
      {minInRem: 5, maxInRem: 5},   // conversion rate
      {minInRem: 8, maxInRem: 8},   // declared amount in EUR
      {minInRem: 13, maxInRem: 16}  //attachment
     );

    if(isEditable){
      tableConfig.push({minInRem: 3, maxInRem: 3}); //delete
    }
    if (investments.length > 0) {
      tableConfig.splice(2, 0, {minInRem: 6});
    }
    return tableConfig;
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

  private addExpenditure(reportExpenditureCost: ProjectPartnerReportExpenditureCostDTO): void {
      const conversionRate = this.getConversionRateByCode(reportExpenditureCost.currencyCode || '', reportExpenditureCost);
      const costOption = this.getUnitCostOrLumpSumObject(reportExpenditureCost);
      this.availableCurrenciesPerRow.push(this.getAvailableCurrenciesByType(this.getUnitCostType(reportExpenditureCost), costOption));

      this.items.push(this.formBuilder.group(
        {
          id: this.formBuilder.control(reportExpenditureCost.id),
          costOptions: this.formBuilder.control(costOption),
          costCategory: this.formBuilder.control(reportExpenditureCost.costCategory),
          investmentId: this.formBuilder.control(reportExpenditureCost.investmentId),
          contractId: this.formBuilder.control(reportExpenditureCost.contractId),
          internalReferenceNumber: this.formBuilder.control(reportExpenditureCost.internalReferenceNumber,
            Validators.maxLength(30)),
          invoiceNumber: this.formBuilder.control(reportExpenditureCost.invoiceNumber,
            Validators.maxLength(30)),
          invoiceDate: this.formBuilder.control(reportExpenditureCost.invoiceDate),
          dateOfPayment: this.formBuilder.control(reportExpenditureCost.dateOfPayment),
          description: this.formBuilder.control(reportExpenditureCost.description),
          comment: this.formBuilder.control(reportExpenditureCost.comment),
          totalValueInvoice: this.formBuilder.control(reportExpenditureCost.totalValueInvoice),
          vat: this.formBuilder.control(reportExpenditureCost.vat),
          numberOfUnits: this.formBuilder.control(reportExpenditureCost.numberOfUnits),
          pricePerUnit: this.formBuilder.control(reportExpenditureCost.pricePerUnit),
          declaredAmount: this.formBuilder.control(reportExpenditureCost.declaredAmount),
          currencyCode: this.formBuilder.control(reportExpenditureCost.currencyCode),
          currencyConversionRate: this.formBuilder.control(conversionRate),
          declaredAmountInEur: this.formBuilder.control(this.getAmountInEur(conversionRate, reportExpenditureCost.declaredAmount || 0)),
          attachment: this.formBuilder.control(reportExpenditureCost.attachment, []),
        })
      );
  }

  private formToReportExpenditures(): ProjectPartnerReportExpenditureCostDTO[] {
   return this.items.controls.map((formGroup: FormGroup) => ({
      costCategory: [formGroup.value?.costCategory, Validators.required],
      internalReferenceNumber: [formGroup.getRawValue()?.internalReferenceNumber, Validators.maxLength(30)],
      invoiceNumber: [formGroup.getRawValue()?.invoiceNumber, Validators.maxLength(30)],
      ...formGroup.getRawValue(),
      lumpSumId: formGroup.getRawValue()?.costOptions?.type === 'lumpSum' ? formGroup.getRawValue()?.costOptions.id : null,
      unitCostId: formGroup.getRawValue()?.costOptions?.type === 'unitCost' ? formGroup.getRawValue()?.costOptions.id : null,
     }));
  }

  get items(): FormArray {
    return this.reportExpendituresForm.get(this.constants.FORM_CONTROL_NAMES.items) as FormArray;
  }

  attachment(index: number): FormControl {
    return this.items.at(index).get(this.constants.FORM_CONTROL_NAMES.attachment) as FormControl;
  }

  onCurrencyChange(expenditureIndex: number, newValue: MatSelectChange) {
    if (this.isCostOptionSelectedInCurrentFormGroup(expenditureIndex)) {
      const unitCost = this.items.at(expenditureIndex).get('costOptions')?.value;
      this.items.at(expenditureIndex).get('pricePerUnit')?.setValue(this.getUnitCostPricePerUnitByCurrency(unitCost, newValue.value));
      this.items.at(expenditureIndex).get('declaredAmount')?.setValue(this.getUnitCostDeclaredAmountByCurrency(expenditureIndex));
    }
    this.updateConversionRate(expenditureIndex, newValue);
  }

  updateConversionRate(expenditureIndex: number, newValue: MatSelectChange) {
    const declaredAmountInLocalCurrency = this.items.at(expenditureIndex).get('declaredAmount')?.value;
    const newConversionRate =  this.getConversionRateByCode(newValue.value);

    this.items.at(expenditureIndex).get('currencyConversionRate')?.setValue(newConversionRate);
    this.items.at(expenditureIndex).get('declaredAmountInEur')?.setValue(this.getAmountInEur(newConversionRate, declaredAmountInLocalCurrency));
  }

  getConversionRateByCode(currencyCode: string, reportExpenditureCost?: ProjectPartnerReportExpenditureCostDTO): number {
    if(this.currentReport.status === ProjectPartnerReportDTO.StatusEnum.Submitted && reportExpenditureCost) {
      return reportExpenditureCost.currencyConversionRate;
    }
    return this.currencies.find((currency) => currency.code === currencyCode)?.conversionRate || 0;
  }

  getAmountInEur(conversionRate: number, amountInLocalCurrency: number): number {
    return conversionRate && amountInLocalCurrency ? NumberService.roundNumber(NumberService.divide(amountInLocalCurrency, conversionRate)) : 0;
  }

  updateAmountInEur(expenditureIndex: number, declaredAmount: number) {
    const newConversionRate =  this.getConversionRateByCode(this.items.at(expenditureIndex).get('currencyCode')?.value);
    const declaredAmountInEur = declaredAmount && newConversionRate ?  NumberService.roundNumber(NumberService.divide(declaredAmount, newConversionRate)) : 0;

    this.items.at(expenditureIndex).get('declaredAmountInEur')?.setValue(NumberService.roundNumber(declaredAmountInEur));
  }

  onUpdateNumberOfUnits(expenditureIndex: number) {
    const total = NumberService.product([this.items.at(expenditureIndex).get('pricePerUnit')?.value , this.items.at(expenditureIndex).get('numberOfUnits')?.value]);
    this.items.at(expenditureIndex).get('declaredAmount')?.setValue(NumberService.roundNumber(total));
    this.updateAmountInEur(expenditureIndex, total);
  }

  onUploadFileToExpenditure(target: any, expenditureId: number, expenditureIndex: number) {
    if (!target || expenditureId === 0) {
      return;
    }

    const serviceId = uuid();
    this.router.confirmLeaveMap.set(serviceId, true);
    this.pageStore.uploadFile(target?.files[0], expenditureId)
      .pipe(
        take(1),
        catchError(err => this.formService.setError(err))
      )
      .subscribe(value => {
        this.attachment(expenditureIndex)?.patchValue(value);
        this.router.confirmLeaveMap.delete(serviceId);
      });
  }

  onDownloadFile(fileId: number) {
    this.partnerFileManagementStore.downloadFile(fileId)
      .pipe(take(1))
      .subscribe();
  }

  onDeleteFile(fileId: number, expenditureIndex: number) {
    this.partnerFileManagementStore.deleteFile(fileId)
      .pipe(take(1))
      .subscribe(_ => this.attachment(expenditureIndex)?.patchValue(null));
  }

  refreshListOfExpenditures(): void {
    this.pageStore.refreshExpenditures$.next(undefined);
  }

  isCostOptionSelectedInCurrentFormGroup(index: number): boolean {
    return this.items.at(index).get('costOptions')?.value !== null;
  }

  disableCostOptionSelectionRelatedFields(control: FormGroup, selectionType: string, index: number): void {
    control.get('costCategory')?.disable();
    control.get('contractId')?.disable();
    control.get('internalReferenceNumber')?.disable();
    control.get('invoiceNumber')?.disable();
    control.get('invoiceDate')?.disable();
    control.get('dateOfPayment')?.disable();
    control.get('totalValueInvoice')?.disable();
    control.get('vat')?.disable();
    control.get('numberOfUnits')?.disable();
    control.get('declaredAmount')?.disable();
    control.get('pricePerUnit')?.disable();
    control.get('currencyCode')?.disable();
    control.get('currencyConversionRate')?.disable();
    control.get('declaredAmountInEur')?.disable();
    control.get('investmentId')?.disable();
    if (selectionType === 'unitCost') {
      control.get('numberOfUnits')?.enable();
      if (this.isUnitCostForeignCurrencyAvailable(index) && !this.hasPartnerCurrencySetToEur()) {
          control.get('currencyCode')?.enable();
        }
    }
  }

  isUnitCostForeignCurrencyAvailable(index: number): boolean {
    return this.availableCurrenciesPerRow[index]?.length > 1;
  }

  clearFieldsOnCostOptionSelection(control: FormGroup): void {
    control.patchValue({costCategory: null});
    control.patchValue({contractId: null});
    control.patchValue({internalReferenceNumber: null});
    control.patchValue({invoiceNumber: null});
    control.patchValue({invoiceDate: null});
    control.patchValue({dateOfPayment: null});
    control.patchValue({totalValueInvoice: null});
    control.patchValue({description: []});
    control.patchValue({comment: []});
    control.patchValue({vat: null});
    control.patchValue({investmentId: null});
    control.patchValue({numberOfUnits: 1});
    control.patchValue({pricePerUnit: 0});
    control.patchValue({declaredAmount: 0});
    control.patchValue({currencyCode: this.currentReport.identification?.currency});
    control.patchValue({currencyConversionRate: this.getConversionRateByCode(this.currentReport.identification?.currency)});
    control.patchValue({declaredAmountInEur: 0});
  }

  hasPartnerCurrencySetToEur(): boolean {
    return  this.currentReport.identification.currency === CurrencyCodesEnum.EUR;
  }
}
