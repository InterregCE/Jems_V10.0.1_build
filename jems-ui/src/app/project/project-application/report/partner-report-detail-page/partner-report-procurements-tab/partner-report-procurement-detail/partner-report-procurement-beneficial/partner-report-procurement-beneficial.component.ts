import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
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
  ProjectPartnerReportProcurementBeneficialDTO,
} from '@cat/api';
import {catchError, map, take, tap} from 'rxjs/operators';
import {MatTableDataSource} from '@angular/material/table';
import {Alert} from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-partner-procurement-beneficial',
  templateUrl: './partner-report-procurement-beneficial.component.html',
  styleUrls: ['./partner-report-procurement-beneficial.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PartnerReportProcurementBeneficialComponent {
  Alert = Alert;

  private allColumns = ['firstName', 'lastName', 'birth', 'vatNumber', 'delete'];
  private readonlyColumns = this.allColumns.filter(col => col !== 'delete');
  displayedColumns: string[] = [];

  form = this.formBuilder.group({
    beneficialOwners: this.formBuilder.array([]),
  });

  dataSource: MatTableDataSource<AbstractControl> = new MatTableDataSource([]);

  data$: Observable<{
    beneficials: ProjectPartnerReportProcurementBeneficialDTO[];
    isReportEditable: boolean;
    reportNumber: number;
  }>;

  @Input()
  private procurementId: number;

  constructor(
    private activatedRoute: ActivatedRoute,
    private procurementStore: PartnerReportProcurementStore,
    private formBuilder: FormBuilder,
    public formService: FormService,
    private partnerReportDetailPageStore: PartnerReportDetailPageStore,
  ) {
    this.data$ = combineLatest([
      this.procurementStore.beneficials$,
      this.partnerReportDetailPageStore.reportEditable$,
      this.partnerReportDetailPageStore.partnerReport$,
    ]).pipe(
      tap(([beneficials, isReportEditable]) => this.resetForm(beneficials, isReportEditable)),
      tap(([beneficials, isReportEditable]) => this.prepareVisibleColumns(isReportEditable)),
      map(([beneficials, isReportEditable, report]) => ({ beneficials, isReportEditable, reportNumber: report.reportNumber })),
    );
    this.formService.init(this.form);
  }

  private resetForm(beneficials: ProjectPartnerReportProcurementBeneficialDTO[], editable = false) {
    this.beneficials.clear();

    beneficials.forEach(b => this.addBeneficial(b));
    this.formService.setEditable(editable);
    this.formService.resetEditable();
    this.dataSource.data = this.beneficials.controls;
  }

  private prepareVisibleColumns(isEditable: boolean) {
    this.displayedColumns.splice(0);
    (isEditable ? this.allColumns : this.readonlyColumns).forEach(column => {
      this.displayedColumns.push(column);
    });
  }

  addBeneficial(beneficial: ProjectPartnerReportProcurementBeneficialDTO | null = null) {
    const createdInThisReport = beneficial ? beneficial.createdInThisReport : true;

    const item = this.formBuilder.group({
      id: this.formBuilder.control(beneficial?.id || 0),
      createdInThisReport: this.formBuilder.control(createdInThisReport),
      firstName: this.formBuilder.control(beneficial?.firstName || '', Validators.maxLength(50)),
      lastName: this.formBuilder.control(beneficial?.lastName || '', Validators.maxLength(50)),
      birth: this.formBuilder.control(beneficial?.birth || null),
      vatNumber: this.formBuilder.control(beneficial?.vatNumber || '',
        Validators.compose([Validators.maxLength(30), Validators.required])
      ),
    });

    this.beneficials.push(item);
    this.dataSource.data = this.beneficials.controls;
    this.formService.setDirty(true);
  }

  deleteBeneficial(index: number) {
    this.beneficials.removeAt(index);
    this.dataSource.data = this.beneficials.controls;
    this.formService.setDirty(true);
  }

  saveForm() {
    this.procurementStore.updateBeneficials(this.procurementId, this.beneficials.value.filter((ben: any) => ben.createdInThisReport))
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.partner.report.procurements.beneficial.save.success')),
        catchError(error => this.formService.setError(error)),
      )
      .subscribe();
  }

  discardChanges(originalData: ProjectPartnerReportProcurementBeneficialDTO[]) {
    this.resetForm(originalData);
  }

  get beneficials(): FormArray {
    return this.form.get('beneficialOwners') as FormArray;
  }

}
