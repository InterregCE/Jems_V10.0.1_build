import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  OnInit,
  QueryList,
  ViewChildren
} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {catchError, finalize, map, take, tap} from 'rxjs/operators';
import {HttpErrorResponse} from '@angular/common/http';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable, of} from 'rxjs';
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
  ProjectPartnerReportParkedExpenditureDTO,
  ProjectPartnerReportUnitCostDTO,
  UserRoleDTO,
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
import {MatSelect} from '@angular/material/select';
import {MatDatepicker} from '@angular/material/datepicker';
import {CustomTranslatePipe} from '@common/pipe/custom-translate-pipe';
import {TranslateByInputLanguagePipe} from '@common/pipe/translate-by-input-language.pipe';
import {SecurityService} from 'src/app/security/security.service';
import {PrivilegesPageStore} from '@project/project-application/privileges-page/privileges-page-store.service';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@UntilDestroy()
@Component({
  selector: 'jems-partner-expenditures-cost',
  templateUrl: './partner-report-expenditures-tab.component.html',
  styleUrls: ['./partner-report-expenditures-tab.component.scss'],
  providers: [FormService, PrivilegesPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportExpendituresTabComponent implements OnInit {
  expenditureFormIndex: number | null;
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
    parkedExpenditures: ProjectPartnerReportParkedExpenditureDTO[];
    isGDPRCompliant: boolean;
    canEdit: boolean;
    isMonitorUser: boolean;
    isReportEditable: boolean;
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
  descriptionInputPressed = false;
  commentInputPressed = false;

  availableLumpSums: ProjectPartnerReportLumpSumDTO[];
  availableUnitCosts: ProjectPartnerReportUnitCostDTO[];
  availableCurrenciesPerRow: CurrencyDTO[][] = [];
  contractIDs: IdNamePairDTO[] = [];
  investmentsSummary: InvestmentSummary[] = [];
  isUploadDone = false;

  readonly PERIOD_PREPARATION: number = 0;
  readonly PERIOD_CLOSURE: number = 255;

  constructor(public pageStore: PartnerReportExpendituresStore,
              protected changeDetectorRef: ChangeDetectorRef,
              private formBuilder: FormBuilder,
              public formService: FormService,
              private partnerFileManagementStore: PartnerFileManagementStore,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore,
              private router: RoutingService,
              private customTranslatePipe: CustomTranslatePipe,
              private translateByInputLanguagePipe: TranslateByInputLanguagePipe,
              public securityService: SecurityService,
              public privilegesPageStore: PrivilegesPageStore,
              public permissionService: PermissionService,
              private partnerReportPageStore: PartnerReportPageStore) {
    this.isReportEditable$ = this.pageStore.isEditable$;
  }

  @ViewChildren('costOptionsSelect') private costOptionsSelect: QueryList<MatSelect>;
  @ViewChildren('costCategorySelect') private costCategorySelect: QueryList<MatSelect>;
  @ViewChildren('costGDPR') private costGDPR: QueryList<MatSelect>;
  @ViewChildren('investmentNumber') private investmentNumberSelect: QueryList<MatSelect>;
  @ViewChildren('contractId') private contractIdSelect: QueryList<MatSelect>;
  @ViewChildren('internalReferenceNumber') private internalReferenceNumberTextField: QueryList<ElementRef>;
  @ViewChildren('invoiceNumberInput') private invoiceNumberTextField: QueryList<ElementRef>;
  @ViewChildren('invoiceDateInput') private invoiceDateFormDateInput: QueryList<ElementRef>;
  @ViewChildren('invoiceDatePicker') private invoiceDateFormDatePicker: QueryList<MatDatepicker<Date>>;
  @ViewChildren('dateOfPaymentInput') private dateOfPaymentDateInput: QueryList<ElementRef>;
  @ViewChildren('dateOfPaymentPicker') private dateOfPaymentDatePicker: QueryList<MatDatepicker<Date>>;
  @ViewChildren('descriptionInput') private descriptionInput: QueryList<ElementRef>;
  @ViewChildren('totalValueInvoiceInput') private totalValueInvoiceInput: QueryList<ElementRef>;
  @ViewChildren('vatInput') private vatInput: QueryList<ElementRef>;
  @ViewChildren('numberOfUnitsInput') private numberOfUnitsInput: QueryList<ElementRef>;
  @ViewChildren('pricePerUnitInput') private pricePerUnitInput: QueryList<ElementRef>;
  @ViewChildren('declaredAmountInput') private declaredAmountInput: QueryList<ElementRef>;
  @ViewChildren('currencyCodeSelect') private currencyCodeSelect: QueryList<MatSelect>;

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

  onCostCategoryChange(change: MatSelectChange, control: FormGroup, index: number): void {
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
        control.get('numberOfUnits')?.enable();
        control.get('currencyCode')?.enable();
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
      case 'unitCost': return this.currencies.filter((currency) => currency.code === CurrencyCodesEnum.EUR
        || currency.code === this.availableUnitCosts.filter(el => (el.id === unitCost?.value?.id || el.id === unitCost?.id) )[0].foreignCurrencyCode);
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

  disableOnReset(control: FormGroup, index: number, isGDPRCompliant: boolean, isMonitorUser: boolean): void {
    if (!isMonitorUser && (control.get('costGDPR')?.value === true && !isGDPRCompliant)) {
      control.disable();
    }
    else {
      if (this.isStaffCostsSelectedForCostCategoryRow(control) ||
          this.isTravelAndAccommodationSelectedForCostCategoryRow(control)) {
        control.get('investmentId')?.disable();
      }
      if (this.isStaffCostsSelectedForCostCategoryRow(control)) {
        control.get('vat')?.disable();
        control.get('contractId')?.disable();
        control.get('invoiceNumber')?.disable();
      }
      if ((control?.get(this.constants.FORM_CONTROL_NAMES.costOptions) as FormControl)?.value !== null) {
        this.disableCostOptionSelectionRelatedFields(control, control.value?.costOptions?.type, index);
      }
      if (control.get('reportOfOriginNumber')?.value) {
        control.get('currencyCode')?.disable();
      }
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

  resetForm(partnerReportExpenditures: ProjectPartnerReportExpenditureCostDTO[], isGDPRCompliant: boolean , isMonitorUser: boolean): void {
    this.availableCurrenciesPerRow = [];
    this.items.clear();
    partnerReportExpenditures.forEach(partnerReportExpenditure => this.addExpenditure(partnerReportExpenditure));
    this.tableData = [...this.items.controls];
    this.formService.resetEditable();
    this.items.controls.forEach((formGroup: FormGroup, index) => (
      this.disableOnReset(formGroup, index, isGDPRCompliant, isMonitorUser)));

    setTimeout(() => this.changeDetectorRef.detectChanges());
  }

  removeItem(indexToBeRemoved: number): void {
    this.items.removeAt(indexToBeRemoved);
    this.availableCurrenciesPerRow.splice(indexToBeRemoved, 1);
    this.items.controls.forEach((formGroup: FormGroup, index) => (
      formGroup.controls.number.setValue(index + 1)
    ));
    this.tableData = [...this.items.controls];
    this.formService.setDirty(true);
  }

  addNewItem(): void {
    const item = this.formBuilder.group({
      id: null,
      number: this.tableData.length + 1,
      reportOfOriginNumber: null,
      originalExpenditureNumber: null,
      costOptions: null,
      costCategory: ['', Validators.required],
      costGDPR: '',
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
      currencyCode: [this.currentReport.identification?.currency, Validators.required],
      currencyConversionRate: this.getConversionRateByCode(this.currentReport.identification?.currency),
      declaredAmountInEur: 0,
      attachment: [],
    });
    this.items.push(item);
    this.clearRowSelections();
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
      tap(() => this.clearRowSelections()),
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
      this.reportCosts$,
      this.pageStore.parkedExpenditures$,
      this.privilegesPageStore.isCurrentUserGDPRCompliant$,
      this.partnerReportPageStore.userCanEditReport$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingView),
      this.isReportEditable$
    ]).pipe(
      map(([expendituresCosts, costCategories, investmentsSummary, contractIDs, tableConfiguration, reportCosts, parkedExpenditures, isCurrentUserGDPRCompliant, canEdit, isMonitorUser, isReportEditable]: any) => ({
          expendituresCosts,
          costCategories,
          investmentsSummary,
          contractIDs,
          ...tableConfiguration,
          ...reportCosts,
          parkedExpenditures: parkedExpenditures.map((exp: ProjectPartnerReportParkedExpenditureDTO) => ({
            ...exp,
            canBeReIncluded: (exp.lumpSum ? exp.lumpSum.entityStillAvailable : true)
              && (exp.unitCost ? exp.unitCost.entityStillAvailable : true)
              && (exp.investment ? exp.investment.entityStillAvailable : true),
            contractName: (exp.expenditure.contractId ? contractIDs.find((c: IdNamePairDTO) => c.id === exp.expenditure.contractId)?.name : undefined)
          })),
        isGDPRCompliant: isCurrentUserGDPRCompliant,
        canEdit,
        isMonitorUser,
        isReportEditable
        })
      ),
      tap(data => this.resetForm(data.expendituresCosts, data.isGDPRCompliant, data.isMonitorUser)),
      tap(data => this.contractIDs = data.contractIDs),
      tap(data => this.investmentsSummary = data.investmentsSummary),
    );

  }

  private getColumnsToDisplay(investments: InvestmentSummary[], isEditable: boolean, isCostOptionsAvailable: boolean): string[] {
    const columnsToDisplay = [
      'costItemID',
      'costGDPR',
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
      columnsToDisplay.splice(3, 0, 'investmentId');
    }
    if (isCostOptionsAvailable) {
      columnsToDisplay.splice(2, 0, 'costOptions');
      columnsToDisplay.splice(12, 0, 'numberOfUnits');
      columnsToDisplay.splice(13, 0, 'pricePerUnit');
    }
    return columnsToDisplay;
  }

  private getTableConfig(investments: InvestmentSummary[], isEditable: boolean, isCostOptionsAvailable: boolean): TableConfig[] {
    const tableConfig: TableConfig[] = [{minInRem: 3, maxInRem: 3}]; // id

    tableConfig.push({minInRem: 1, maxInRem: 1}); // cost GDPR

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
      tableConfig.splice(4, 0, {minInRem: 11});
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
      const isParked = !!reportExpenditureCost.parkingMetadata;
      const conversionRate = isParked
        ? reportExpenditureCost.currencyConversionRate
        : this.getConversionRateByCode(reportExpenditureCost.currencyCode || '', reportExpenditureCost);
      const costOption = this.getUnitCostOrLumpSumObject(reportExpenditureCost);
      this.availableCurrenciesPerRow.push(this.getAvailableCurrenciesByType(this.getUnitCostType(reportExpenditureCost), costOption));

      this.items.push(this.formBuilder.group(
        {
          id: this.formBuilder.control(reportExpenditureCost.id),
          number: this.formBuilder.control(reportExpenditureCost.number),
          reportOfOriginNumber: this.formBuilder.control(reportExpenditureCost.parkingMetadata?.reportOfOriginNumber),
          originalExpenditureNumber: this.formBuilder.control(reportExpenditureCost.parkingMetadata?.originalExpenditureNumber),
          costOptions: this.formBuilder.control(costOption),
          costCategory: this.formBuilder.control(reportExpenditureCost.costCategory),
          costGDPR: this.formBuilder.control(reportExpenditureCost.gdpr),
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
      gdpr: formGroup.value?.costGDPR
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
    this.isUploadDone = false;
    const serviceId = uuid();
    this.router.confirmLeaveMap.set(serviceId, true);
    this.pageStore.uploadFile(target?.files[0], expenditureId)
      .pipe(
        take(1),
        catchError(err => this.formService.setError(err)),
        finalize(() => this.isUploadDone = true)
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
    this.clearRowSelections();
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
    control.get('declaredAmount')?.disable();
    control.get('pricePerUnit')?.disable();
    control.get('currencyConversionRate')?.disable();
    control.get('declaredAmountInEur')?.disable();
    control.get('investmentId')?.disable();

    if (selectionType === 'lumpSum') {
      control.get('numberOfUnits')?.disable();
      control.get('currencyCode')?.disable();
    }

    if (selectionType === 'unitCost' && !this.isUnitCostForeignCurrencyAvailable(index)) {
      control.get('currencyCode')?.disable();
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
    return this.currentReport.identification.currency === CurrencyCodesEnum.EUR;
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
        postfix = ` - ${this.customTranslatePipe.transform('project.application.form.section.part.e.period.preparation')}`;
      } else if (costOption.period && costOption.period === this.PERIOD_CLOSURE) {
        postfix = ` - ${this.customTranslatePipe.transform('project.application.form.section.part.e.period.preparation')}`;
      } else if(costOption.period) {
        postfix = ` - ${this.customTranslatePipe.transform('project.partner.budget.table.period')} ${costOption.period}`;
      }
      return this.translateByInputLanguagePipe.transform(costOption.name).pipe(map(n => n + postfix));
    }
  }

  getLimitedTextInputTooltip(content: string | null, limit: number): string {
    return content ? `${content} (${content.length}/${limit})` : '';
  }

  getAdditionalRowClass(index: number, controlName: string) {
    if(this.items.at(index).get(controlName)?.disabled
      || (controlName === PartnerReportExpendituresTabConstants.FORM_CONTROL_NAMES.currencyCode
        && this.hasPartnerCurrencySetToEur())
    ) {
      return controlName === PartnerReportExpendituresTabConstants.FORM_CONTROL_NAMES.contractId ? 'border-with-dotted disabled-text' : 'border-with-dotted';
    } else if (index % 2 === 0) {
      return 'blue-background';
    } else {
      return 'grey-background';
    }
  }

  getContractIdValue(contractId: number): string {
    const value = this.contractIDs.find(c => c.id === contractId)?.name;
    return value ?? '';
  }

  getInvestmentIdValue(investmentId: number): string {
    const summary = this.investmentsSummary.find(s => s.id === investmentId);
    return summary?.toString() ?? '';
  }
  getInvestmentInactive(investmentId: number): boolean {
    const summary = this.investmentsSummary.find(s => s.id === investmentId);
    return summary?.deactivated || false;
  }

  clearRowSelections() {
    this.descriptionInputPressed = false;
    this.commentInputPressed = false;
    this.costCategorySelect.forEach(e => e.close());
    this.costOptionsSelect.forEach(e => e.close());
    this.investmentNumberSelect.forEach(e => e.close());
    this.contractIdSelect.forEach(e => e.close());
    this.internalReferenceNumberTextField.forEach(e => e.nativeElement.blur());
    this.invoiceNumberTextField.forEach(e => e.nativeElement.blur());
    this.invoiceDateFormDateInput.forEach(e => e.nativeElement.blur());
    this.invoiceDateFormDatePicker.forEach(e => e.close());
    this.dateOfPaymentDateInput.forEach(e => e.nativeElement.blur());
    this.dateOfPaymentDatePicker.forEach(e => e.close());
    this.totalValueInvoiceInput.forEach(e => e.nativeElement.blur());
    this.vatInput.forEach(e => e.nativeElement.blur());
    this.numberOfUnitsInput.forEach(e => e.nativeElement.blur());
    this.pricePerUnitInput.forEach(e => e.nativeElement.blur());
    this.declaredAmountInput.forEach(e => e.nativeElement.blur());
    this.currencyCodeSelect.forEach(e => e.close());
  }

  selectRow(index: number, column: string) {
    this.expenditureFormIndex = index;
    this.descriptionInputPressed = false;
    this.commentInputPressed = false;
    if (column === 'costCategory') {
      setTimeout(() => {
        this.costCategorySelect.first.open();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    } else if (column === 'costOptions') {
      setTimeout(() => {
        this.costOptionsSelect.first.open();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    } else if (column === 'investmentId') {
      setTimeout(() => {
        this.investmentNumberSelect.first.open();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    } else if (column === 'contractId') {
      setTimeout(() => {
        this.contractIdSelect.first.open();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    } else if (column === 'internalReferenceNumber') {
      setTimeout(() => {
        this.internalReferenceNumberTextField.first.nativeElement.focus();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    } else if (column === 'invoiceNumber') {
      setTimeout(() => {
        this.invoiceNumberTextField.first.nativeElement.focus();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    } else if (column === 'invoiceDate') {
      setTimeout(() => {
        this.invoiceDateFormDateInput.first.nativeElement.focus();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    } else if (column === 'invoiceDatePicker') {
      setTimeout(() => {
        this.invoiceDateFormDatePicker.first.open();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    }  else if (column === 'dateOfPayment') {
      setTimeout(() => {
        this.dateOfPaymentDateInput.first.nativeElement.focus();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    } else if (column === 'dateOfPaymentDatePicker') {
      setTimeout(() => {
        this.dateOfPaymentDatePicker.first.open();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    } else if (column === 'description') {
      this.descriptionInputPressed = true;
    } else if (column === 'comment') {
      this.commentInputPressed = true;
    } else if (column === 'totalValueInvoice') {
      setTimeout(() => {
        this.totalValueInvoiceInput.first.nativeElement.focus();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    } else if (column === 'vat') {
      setTimeout(() => {
        this.vatInput.first.nativeElement.focus();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    } else if (column === 'numberOfUnits') {
      setTimeout(() => {
        this.numberOfUnitsInput.first.nativeElement.focus();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    } else if (column === 'pricePerUnit') {
      setTimeout(() => {
        this.pricePerUnitInput.first.nativeElement.focus();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    } else if (column === 'declaredAmount') {
      setTimeout(() => {
        this.declaredAmountInput.first.nativeElement.focus();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    } else if (column === 'currencyCode') {
      setTimeout(() => {
        this.currencyCodeSelect.first.open();
      }, PartnerReportExpendituresTabConstants.FOCUS_TIMEOUT);
    }
  }

  editAllowed(valueGDPR: boolean, isGDPRCompliant: boolean, canEdit: boolean, isMonitorUser: boolean): boolean {
     return isMonitorUser || (!valueGDPR && canEdit) || (valueGDPR && isGDPRCompliant);
  }

  toggleGDPR(index: number,  control: FormGroup, value: boolean): void {
    this.items.at(index).get('costGDPR')?.setValue(value);
    control.patchValue({costGDPR: value});

    this.formChanged();
  }

  formChanged(): void {
    this.formService.setDirty(true);
  }
}
