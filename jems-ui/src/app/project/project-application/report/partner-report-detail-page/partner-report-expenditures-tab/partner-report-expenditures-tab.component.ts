import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
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
  ProjectPartnerReportLumpSumDTO
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
    lumpSums: ProjectPartnerReportLumpSumDTO[];
  }>;
  tableConfiguration$: Observable<{
    columnsToDisplay: string[];
    withConfigs: TableConfig[];
  }>;

  lumpSumsAvailable: boolean;
  lumpSumHasValue = false;
  availableLumpSums: ProjectPartnerReportLumpSumDTO[];

  readonly PERIOD_PREPARATION: number = 0;
  readonly PERIOD_CLOSURE: number = 255;

  constructor(public pageStore: PartnerReportExpendituresStore,
              private formBuilder: FormBuilder,
              private formService: FormService,
              private partnerFileManagementStore: PartnerFileManagementStore,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore) {
    this.isReportEditable$ = this.pageStore.isEditable$;
  }

  ngOnInit(): void {
    this.initForm();
    this.tableConfiguration$ = combineLatest([
      this.pageStore.investmentsSummary$,
      this.partnerReportDetailPageStore.reportEditable$,
      this.pageStore.reportLumpSums$,
    ]).pipe(
      map(([investments, editable, lumpSums]) => ({
          columnsToDisplay: this.getColumnsToDisplay(investments, editable, lumpSums.length > 0),
          withConfigs: this.getTableConfig(investments, editable)
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

  onLumpSumChange(change: MatSelectChange, control: FormGroup): void {
    control.patchValue({lumpSumId: change.value});
    this.clearFieldsOnLumpSumSelection(control);
    if (change.value === null) {
      this.lumpSumHasValue = false;
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
    } else {
      this.lumpSumHasValue = true;
      control.patchValue({costCategory: this.CostCategoryEnum.Multiple});
      control.patchValue({numberOfUnits: 1});
      control.patchValue({pricePerUnit: this.availableLumpSums.filter(lumpSum => lumpSum.id === change.value)[0].cost});
      control.patchValue({declaredAmount: this.availableLumpSums.filter(lumpSum => lumpSum.id === change.value)[0].cost});
      control.patchValue({currencyCode: CurrencyCodesEnum.EUR});
      control.patchValue({currencyConversionRate: this.getConversionRateByCode(CurrencyCodesEnum.EUR)});
      control.patchValue({declaredAmountInEur: this.availableLumpSums.filter(lumpSum => lumpSum.id === change.value)[0].cost});
      this.disableLumpSumSelectionRelatedFields(control);
    }
  }

  disableOnReset(control: FormGroup): void {
    if (this.isStaffCostsSelectedForCostCategoryRow(control) ||
      this.isTravelAndAccommodationSelectedForCostCategoryRow(control)) {
      control.get('investmentId')?.disable();
    }
    if (this.isStaffCostsSelectedForCostCategoryRow(control)) {
      control.get('vat')?.disable();
      control.get('contractId')?.disable();
      control.get('invoiceNumber')?.disable();
    }
    if((control?.get(this.constants.FORM_CONTROL_NAMES.lumpSumId) as FormControl)?.value !== null) {
      this.disableLumpSumSelectionRelatedFields(control);
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
    this.items.clear();
    partnerReportExpenditures.forEach(partnerReportExpenditure => this.addExpenditure(partnerReportExpenditure));
    this.tableData = [...this.items.controls];
    this.formService.resetEditable();
    this.items.controls.forEach((formGroup: FormGroup) => (
      this.disableOnReset(formGroup)));
  }

  removeItem(index: number): void {
    this.items.removeAt(index);
    this.tableData = [...this.items.controls];
    this.formService.setDirty(true);
  }

  addNewItem(): void {
    const item = this.formBuilder.group({
      id: null,
      lumpSumId: null,
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
    this.data$ = combineLatest([
      this.pageStore.expendituresCosts$,
      this.pageStore.costCategories$,
      this.pageStore.investmentsSummary$,
      this.pageStore.contractIDs$,
      this.tableConfiguration$,
      this.pageStore.reportLumpSums$
    ]).pipe(
      map(([expendituresCosts, costCategories, investmentsSummary, contractIDs, tableConfiguration, lumpSums]) => ({
          expendituresCosts,
          costCategories,
          investmentsSummary,
          contractIDs,
          ...tableConfiguration,
          lumpSums
        })
      ),
      tap(data => this.resetForm(data.expendituresCosts)),
      tap(data => this.lumpSumsAvailable = data.lumpSums.length > 0),
      tap(data => this.availableLumpSums = data.lumpSums)
    );
  }

  private getColumnsToDisplay(investments: InvestmentSummary[], isEditable: boolean, lumpSumsAvailable: boolean): string[] {
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
    if (lumpSumsAvailable) {
      columnsToDisplay.splice(1, 0, 'lumpSumId');
      columnsToDisplay.splice(12, 0, 'numberOfUnits');
      columnsToDisplay.splice(13, 0, 'pricePerUnit');
    }
    return columnsToDisplay;
  }

  private getTableConfig(investments: InvestmentSummary[], isEditable: boolean): TableConfig[] {
    const tableConfig = [
      {minInRem: 1, maxInRem: 3},   // id
      {minInRem: 11, maxInRem: 16}, // lump sum
      {minInRem: 11, maxInRem: 16}, // cost category
      {minInRem: 8, maxInRem: 8},   // contract id
      {minInRem: 5, maxInRem: 8},   // internal reference
      {minInRem: 5, maxInRem: 8},   // invoice number
      {minInRem: 8, maxInRem: 8},   // invoice date
      {minInRem: 8, maxInRem: 8},   // payment date
      {minInRem: 16},               // description
      {minInRem: 16},               // comment
      {minInRem: 8, maxInRem: 8},   // total invoice value
      {minInRem: 8, maxInRem: 8},   // vat
      {minInRem: 8, maxInRem: 8},   // number of units
      {minInRem: 8, maxInRem: 8},   // price per unit
      {minInRem: 8, maxInRem: 8},   // declared amount
      {minInRem: 5, maxInRem: 5},   // currency
      {minInRem: 5, maxInRem: 5},   // conversion rate
      {minInRem: 8, maxInRem: 8},   // declared amount in EUR
      {minInRem: 13, maxInRem: 16}   //attachment
    ];
    if(isEditable){
      tableConfig.push({minInRem: 3, maxInRem: 3}); //delete
    }
    if (investments.length > 0) {
      tableConfig.splice(2, 0, {minInRem: 6});
    }
    return tableConfig;
  }

  private addExpenditure(reportExpenditureCost?: ProjectPartnerReportExpenditureCostDTO): void {
    const conversionRate = this.getConversionRateByCode(reportExpenditureCost?.currencyCode || '', reportExpenditureCost);
    this.items.push(this.formBuilder.group(
      {
        id: this.formBuilder.control(reportExpenditureCost?.id),
        lumpSumId: this.formBuilder.control(reportExpenditureCost?.lumpSumId),
        costCategory: this.formBuilder.control(reportExpenditureCost?.costCategory),
        investmentId: this.formBuilder.control(reportExpenditureCost?.investmentId),
        contractId: this.formBuilder.control(reportExpenditureCost?.contractId),
        internalReferenceNumber: this.formBuilder.control(reportExpenditureCost?.internalReferenceNumber,
          Validators.maxLength(30)),
        invoiceNumber: this.formBuilder.control(reportExpenditureCost?.invoiceNumber,
          Validators.maxLength(30)),
        invoiceDate: this.formBuilder.control(reportExpenditureCost?.invoiceDate),
        dateOfPayment: this.formBuilder.control(reportExpenditureCost?.dateOfPayment),
        description: this.formBuilder.control(reportExpenditureCost?.description),
        comment: this.formBuilder.control(reportExpenditureCost?.comment),
        totalValueInvoice: this.formBuilder.control(reportExpenditureCost?.totalValueInvoice),
        vat: this.formBuilder.control(reportExpenditureCost?.vat),
        numberOfUnits: this.formBuilder.control(reportExpenditureCost?.numberOfUnits),
        pricePerUnit: this.formBuilder.control(reportExpenditureCost?.pricePerUnit),
        declaredAmount: this.formBuilder.control(reportExpenditureCost?.declaredAmount),
        currencyCode: this.formBuilder.control(reportExpenditureCost?.currencyCode),
        currencyConversionRate: this.formBuilder.control(conversionRate),
        declaredAmountInEur: this.formBuilder.control(this.getAmountInEur(conversionRate, reportExpenditureCost?.declaredAmount || 0)),
        attachment: this.formBuilder.control(reportExpenditureCost?.attachment, []),
      })
    );
  }

  private formToReportExpenditures(): ProjectPartnerReportExpenditureCostDTO[] {
    return this.items.controls.map((formGroup: FormGroup) => ({
      costCategory: [formGroup.value?.costCategory, Validators.required],
      internalReferenceNumber: [formGroup.getRawValue()?.internalReferenceNumber, Validators.maxLength(30)],
      invoiceNumber: [formGroup.getRawValue()?.invoiceNumber, Validators.maxLength(30)],
      ...formGroup.getRawValue()
    }));
  }

  get items(): FormArray {
    return this.reportExpendituresForm.get(this.constants.FORM_CONTROL_NAMES.items) as FormArray;
  }

  attachment(index: number): FormControl {
    return this.items.at(index).get(this.constants.FORM_CONTROL_NAMES.attachment) as FormControl;
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

  onUploadFileToExpenditure(target: any, expenditureId: number, expenditureIndex: number) {
    if (!target || expenditureId === 0) {
      return;
    }
    this.pageStore.uploadFile(target?.files[0], expenditureId)
      .pipe(
        take(1),
        catchError(err => this.formService.setError(err))
      )
      .subscribe(value => this.attachment(expenditureIndex)?.patchValue(value));
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

  isLumpSumSelectedInCurrentFormGroup(index: number): boolean {
    return this.items.at(index).get('lumpSumId')?.value !== null;
  }

  disableLumpSumSelectionRelatedFields(control: FormGroup): void {
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
  }

  clearFieldsOnLumpSumSelection(control: FormGroup): void {
    control.patchValue({costCategory: null});
    control.patchValue({contractId: null});
    control.patchValue({internalReferenceNumber: null});
    control.patchValue({invoiceNumber: null});
    control.patchValue({invoiceDate: null});
    control.patchValue({dateOfPayment: null});
    control.patchValue({totalValueInvoice: null});
    control.patchValue({vat: null});
    control.patchValue({investmentId: null});
    control.patchValue({numberOfUnits: 1});
    control.patchValue({pricePerUnit: 0});
    control.patchValue({declaredAmount: 0});
    control.patchValue({currencyCode: this.currentReport.identification?.currency});
    control.patchValue({currencyConversionRate: this.getConversionRateByCode(this.currentReport.identification?.currency)});
    control.patchValue({declaredAmountInEur: 0});
  }
}
