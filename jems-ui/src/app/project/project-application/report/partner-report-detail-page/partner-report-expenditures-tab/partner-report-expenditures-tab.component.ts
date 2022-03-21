import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {catchError, map, tap} from 'rxjs/operators';
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
import {ProjectPartnerReportExpenditureCostDTO} from '@cat/api';
import {BudgetCostCategoryEnum} from '@project/model/lump-sums/BudgetCostCategoryEnum';

@UntilDestroy()
@Component({
  selector: 'jems-partner-expenditures-cost',
  templateUrl: './partner-report-expenditures-tab.component.html',
  styleUrls: ['./partner-report-expenditures-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportExpendituresTabComponent implements OnInit {

  reportExpendituresForm: FormGroup;
  tableData: AbstractControl[] = [];
  constants = PartnerReportExpendituresTabConstants;
  columnsToDisplay$: Observable<string[]>;
  withConfigs$: Observable<TableConfig[]>;
  data$: Observable<{
    expendituresCosts: ProjectPartnerReportExpenditureCostDTO[];
    costCategories: string[];
    investmentNumbers: string[];
    contractIDs: string[];
    columnsToDisplay: string[];
    withConfigs: TableConfig[];
  }>;

  constructor(public pageStore: PartnerReportExpendituresStore,
              private formBuilder: FormBuilder,
              private formService: FormService) {
  }

  ngOnInit(): void {
    this.initForm();
    this.columnsToDisplay$ = this.pageStore.investmentNumbers$.pipe(
      map((investmentNumbers: string[]) => this.getColumnsToDisplay(investmentNumbers))
    );
    this.withConfigs$ = this.pageStore.investmentNumbers$.pipe(map((investmentNumbers: string[]) =>
      this.getTableConfig(investmentNumbers)));
    this.dataAsObservable();
  }

  onCostCategoryChange(change: MatSelectChange, control: FormGroup): void {
    control.patchValue({costCategory: change.value});
    if (this.isStaffCostsSelectedForCostCategoryRow(control) ||
      this.isTravelAndAccommodationSelectedForCostCategoryRow(control)) {
      control.patchValue({investmentNumber: ''});
      control.get('investmentNumber')?.disable();
    } else {
      control.get('investmentNumber')?.enable();
    }
    if (this.isStaffCostsSelectedForCostCategoryRow(control)) {
      control.patchValue({contractID: ''});
      control.patchValue({invoiceNumber: ''});
      control.patchValue({vat: ''});
      control.get('vat')?.disable();
      control.get('contractID')?.disable();
      control.get('invoiceNumber')?.disable();
    } else {
      control.get('vat')?.enable();
      control.get('contractID')?.enable();
      control.get('invoiceNumber')?.enable();
    }
  }

  disableOnReset(control: FormGroup): void {
    if (this.isStaffCostsSelectedForCostCategoryRow(control) ||
      this.isTravelAndAccommodationSelectedForCostCategoryRow(control)) {
      control.get('investmentNumber')?.disable();
    }
    if (this.isStaffCostsSelectedForCostCategoryRow(control)) {
      control.get('vat')?.disable();
      control.get('contractID')?.disable();
      control.get('invoiceNumber')?.disable();
    }
  }

  onInvestmentNumberChange(change: MatSelectChange, control: FormGroup): void {
    control.patchValue({investmentNumber: change.value});
  }

  isStaffCostsSelectedForCostCategoryRow(control: FormGroup): boolean {
    return (control?.get(this.constants.FORM_CONTROL_NAMES.costCategory) as FormControl)?.value
      == BudgetCostCategoryEnum.STAFF_COSTS;
  }

  isTravelAndAccommodationSelectedForCostCategoryRow(control: FormGroup): boolean {
    return (control?.get(this.constants.FORM_CONTROL_NAMES.costCategory) as FormControl)?.value
      == BudgetCostCategoryEnum.TRAVEL_AND_ACCOMMODATION_COSTS;
  }

  resetForm(partnerReportExpenditures: ProjectPartnerReportExpenditureCostDTO[]): void {
    this.items.clear();
    partnerReportExpenditures.forEach(partnerReportExpenditure => this.addResult(partnerReportExpenditure));
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
      costCategory: ['', Validators.required],
      investmentNumber: '',
      contractID: '',
      internalReferenceNumber: ['', Validators.maxLength(30)],
      invoiceNumber: ['', Validators.maxLength(30)],
      invoiceDate: '',
      dateOfPayment: '',
      description: [[]],
      comment: [[]],
      totalValueInvoice: '',
      vat: '',
      declaredAmount: ''
    });
    this.items.push(item);
    this.tableData = [...this.items.controls];
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
      this.pageStore.investmentNumbers$,
      this.pageStore.contractIDs$,
      this.columnsToDisplay$,
      this.withConfigs$,
    ]).pipe(
      map(([expendituresCosts, costCategories, investmentNumbers, contractIDs, columnsToDisplay, withConfigs]) => ({
          expendituresCosts,
          costCategories,
          investmentNumbers,
          contractIDs,
          columnsToDisplay,
          withConfigs
        })
      ),
      tap(data => this.resetForm(data.expendituresCosts))
    );
  }

  private getColumnsToDisplay(investmentNumbers: string[]): string[] {
    const columnsToDisplay = [
      'costItemID',
      'costCategory',
      'contractID',
      'internalReferenceNumber',
      'invoiceNumber',
      'invoiceDate',
      'dateOfPayment',
      'description',
      'comment',
      'totalValueInvoice',
      'vat',
      'declaredAmount',
      'actions'
    ];
    if (investmentNumbers.length > 0) {
      columnsToDisplay.splice(2, 0, 'investmentNumber');
    }
    return columnsToDisplay;
  }

  private getTableConfig(investmentNumbers: string[]): TableConfig[] {
    const tableConfig = [
      {minInRem: 1},
      {minInRem: 11},
      {minInRem: 6},
      {minInRem: 9},
      {minInRem: 8},
      {minInRem: 8},
      {minInRem: 8},
      {minInRem: 12},
      {minInRem: 12},
      {minInRem: 8},
      {minInRem: 7},
      {minInRem: 7},
      {minInRem: 3}
    ];

    if (investmentNumbers.length > 0) {
      tableConfig.splice(2, 0, {minInRem: 6});
    }
    return tableConfig;
  }

  private addResult(reportExpenditureCost?: ProjectPartnerReportExpenditureCostDTO): void {
    this.items.push(this.formBuilder.group(
      {
        id: this.formBuilder.control(reportExpenditureCost?.id),
        costCategory: this.formBuilder.control(reportExpenditureCost?.costCategory),
        investmentNumber: this.formBuilder.control(reportExpenditureCost?.investmentNumber),
        contractID: this.formBuilder.control(reportExpenditureCost?.contractId),
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
        declaredAmount: this.formBuilder.control(reportExpenditureCost?.declaredAmount)
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
}
