import {ChangeDetectionStrategy, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {catchError, map, take, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {ProjectPartnerReportDTO, ProjectPartnerReportProcurementDTO} from '@cat/api';
import {
  PartnerReportProcurementsTabConstants
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurements-tab.constants';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {
  PartnerReportProcurementsPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-page-store.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {RoutingService} from '@common/services/routing.service';

@UntilDestroy()
@Component({
  selector: 'jems-partner-procurements-cost',
  templateUrl: './partner-report-procurements-tab.component.html',
  styleUrls: ['./partner-report-procurements-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})


export class PartnerReportProcurementsTabComponent {

  columnsToDisplay = ['createdIn', 'contractID', 'contractType', 'contractAmount', 'currency', 'supplierName', 'commentPreview', 'deleteProcurement', 'expandProcurement'];
  expandedElement: ProjectPartnerReportProcurementDTO | null;

  reportProcurementsForm: FormGroup;
  currentReportNumber: number;
  isReportEditable: boolean;

  tableData: AbstractControl[] = [];
  constants = PartnerReportProcurementsTabConstants;

  data$: Observable<{
    savedProcurements: ProjectPartnerReportProcurementDTO[];
    currentReport: ProjectPartnerReportDTO;
    isReportEditable: boolean;
  }>;

  constructor(public pageStore: PartnerReportProcurementsPageStore,
              private formBuilder: FormBuilder,
              private formService: FormService,
              private routingService: RoutingService,
              private reportDetailPageStore: PartnerReportDetailPageStore) {

    this.reportProcurementsForm = this.formBuilder.group({
      procurements: this.formBuilder.array([])
    });

    this.data$ = combineLatest([
      this.pageStore.procurements$,
      this.reportDetailPageStore.partnerReport$,
      this.reportDetailPageStore.reportEditable$
    ]).pipe(
      map(([savedProcurements, currentReport, isReportEditable]) => ({savedProcurements, currentReport, isReportEditable})),
      tap(data => this.resetForm(data.savedProcurements.reverse())),
      tap(data => this.currentReportNumber = data.currentReport.reportNumber),
      tap(data => this.isReportEditable = data.isReportEditable)
    );

    this.formService.init(this.reportProcurementsForm, this.reportDetailPageStore.reportEditable$);
  }

  get procurements(): FormArray {
    return this.reportProcurementsForm.get(this.constants.PROCUREMENTS.name) as FormArray;
  }

  resetForm(procurements: ProjectPartnerReportProcurementDTO[]): void {
    this.procurements.clear();
    procurements.forEach((procurement) => this.addNewProcurement(procurement));
    this.tableData = [...this.procurements.controls];
    this.formService.resetEditable();
  }

  removeItem(index: number): void {
    this.procurements.removeAt(index);
    this.tableData = [...this.procurements.controls];
    this.formService.setDirty(true);
  }

  addNewProcurement(procurement?: ProjectPartnerReportProcurementDTO): void {
    if (this.procurements.length <= this.constants.MAX_NUMBER_OF_ITEMS) {
      const item = this.formBuilder.group({
        id: procurement?.id || 0,
        reportNumber: procurement?.reportNumber ? procurement?.reportNumber : this.currentReportNumber,
        createdInThisReport: procurement?.createdInThisReport,
        contractId: this.formBuilder.control(procurement?.contractId || '', this.constants.CONTRACT_ID.validators),
        contractType: this.formBuilder.control(procurement?.contractType || [], this.constants.CONTRACT_TYPE.validators),
        contractAmount: this.formBuilder.control(procurement?.contractAmount || 0),
        currency: this.formBuilder.control(''),
        supplierName: this.formBuilder.control(procurement?.supplierName || '', this.constants.SUPPLIER_NAME.validators),
        comment: this.formBuilder.control(procurement?.comment || [], this.constants.COMMENT.validators),
        commentPreview: this.formBuilder.control(procurement?.comment || [], this.constants.COMMENT.validators),
      });
      this.procurements.insert(0, item);
    }
    this.tableData = [...this.procurements.controls];
    this.formService.setDirty(true);
  }

  updateReportProcurements() {
    this.pageStore.saveProcurements(this.procurements.value.filter(
      (procurement: ProjectPartnerReportProcurementDTO) => procurement.reportNumber == this.currentReportNumber)
    ).pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.procurements.tab.saved')),
        catchError(err => this.formService.setError(err)),
        untilDestroyed(this)
      ).subscribe();
  }

  lengthOfForm(formGroup: FormGroup, formControlName: string): number {
    return formGroup.get(formControlName)?.value?.length;
  }

  isProcurementFromCurrentReport(control: AbstractControl): boolean {
    return control.value.reportNumber == this.currentReportNumber;
  }

  isProcurementEditable(control: AbstractControl): boolean {
    return this.isProcurementFromCurrentReport(control) && this.isReportEditable;
  }

  updateCommentPreview(index: number) {
    setTimeout(()=> {
      this.procurements.at(index).get('commentPreview')?.setValue(this.procurements.at(index).get('comment')?.value);
    }, 1);
  }

}



