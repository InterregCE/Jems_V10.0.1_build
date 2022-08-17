import {ChangeDetectionStrategy, Component} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {ActivatedRoute} from '@angular/router';
import {
  PartnerReportProcurementStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-detail/partner-report-procurement-store.service';
import {AbstractControl, FormArray, FormBuilder, Validators} from '@angular/forms';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {combineLatest, Observable} from 'rxjs';
import {
  CurrencyDTO,
  ProjectPartnerReportProcurementSubcontractDTO,
} from '@cat/api';
import {map, take, tap} from 'rxjs/operators';
import {MatTableDataSource} from '@angular/material/table';
import {Alert} from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-partner-procurement-subcontract',
  templateUrl: './partner-report-procurement-subcontract.component.html',
  styleUrls: ['./partner-report-procurement-subcontract.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PartnerReportProcurementSubcontractComponent {
  Alert = Alert;

  private allColumns = ['contractName', 'referenceNumber', 'contractDate', 'contractAmount', 'currencyCode', 'supplierName', 'vatNumber', 'delete'];
  private readonlyColumns = this.allColumns.filter(col => col !== 'delete');
  displayedColumns: string[] = [];

  form = this.formBuilder.group({
    subcontracts: this.formBuilder.array([]),
  });

  dataSource: MatTableDataSource<AbstractControl> = new MatTableDataSource([]);

  data$: Observable<{
    subcontracts: ProjectPartnerReportProcurementSubcontractDTO[];
    isReportEditable: boolean;
    currencies: CurrencyDTO[];
    procurementCurrency: string;
  }>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private procurementStore: PartnerReportProcurementStore,
    private formBuilder: FormBuilder,
    public formService: FormService,
    private partnerReportDetailPageStore: PartnerReportDetailPageStore,
  ) {
    this.data$ = combineLatest([
      this.procurementStore.subcontracts$,
      this.partnerReportDetailPageStore.reportEditable$,
      this.partnerReportDetailPageStore.partnerReport$,
      this.procurementStore.procurement$,
      this.procurementStore.currencies$,
    ]).pipe(
      tap(([subcontracts, isReportEditable]) => this.resetForm(subcontracts, isReportEditable)),
      tap(([subcontracts, isReportEditable]) => this.prepareVisibleColumns(isReportEditable)),
      map(([subcontracts, isReportEditable, report, procurement, currencies]) => ({
        subcontracts,
        isReportEditable,
        currencies,
        procurementCurrency: procurement.currencyCode || 'EUR',
      })),
    );
    this.formService.init(this.form);
  }

  private resetForm(subcontracts: ProjectPartnerReportProcurementSubcontractDTO[], editable = false) {
    this.subcontracts.clear();

    subcontracts.forEach(s => this.addSubcontract(s));
    this.formService.setEditable(editable);
    this.formService.resetEditable();
    this.dataSource.data = this.subcontracts.controls;
  }

  private prepareVisibleColumns(isEditable: boolean) {
    this.displayedColumns.splice(0);
    (isEditable ? this.allColumns : this.readonlyColumns).forEach(column => {
      this.displayedColumns.push(column);
    });
  }

  addSubcontract(subcontract: ProjectPartnerReportProcurementSubcontractDTO | null = null, defaultCurrency: string | null = null) {
    const createdInThisReport = subcontract ? subcontract.createdInThisReport : true;

    const item = this.formBuilder.group({
      id: this.formBuilder.control(subcontract?.id || 0),
      createdInThisReport: this.formBuilder.control(createdInThisReport),
      contractName: this.formBuilder.control(subcontract?.contractName || '', Validators.maxLength(50)),
      referenceNumber: this.formBuilder.control(subcontract?.referenceNumber || '', Validators.maxLength(30)),
      contractDate: this.formBuilder.control(subcontract?.contractDate || null),
      contractAmount: this.formBuilder.control(subcontract?.contractAmount || 0, Validators.max(999999999.99)),
      currencyCode: this.formBuilder.control(subcontract?.currencyCode || defaultCurrency),
      supplierName: this.formBuilder.control(subcontract?.supplierName || '', Validators.maxLength(50)),
      vatNumber: this.formBuilder.control(subcontract?.vatNumber || '',
        Validators.compose([Validators.maxLength(30), Validators.required])
      ),
    });

    this.subcontracts.push(item);
    this.dataSource.data = this.subcontracts.controls;
    this.formService.setDirty(true);
  }

  deleteSubcontract(index: number) {
    this.subcontracts.removeAt(index);
    this.dataSource.data = this.subcontracts.controls;
    this.formService.setDirty(true);
  }

  saveForm() {
    this.procurementStore.updateSubcontracts(this.subcontracts.value.filter((sub: any) => sub.createdInThisReport))
      .pipe(take(1))
      .subscribe();
  }

  discardChanges(originalData: ProjectPartnerReportProcurementSubcontractDTO[]) {
    this.resetForm(originalData);
  }

  get subcontracts(): FormArray {
    return this.form.get('subcontracts') as FormArray;
  }

}
