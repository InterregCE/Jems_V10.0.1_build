import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Alert} from '@common/components/forms/alert';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {
  AuditControlCorrectionDTO, CorrectionAvailableFtlsDTO, CorrectionAvailableFundDTO,
  CorrectionAvailablePartnerDTO,
  CorrectionAvailablePartnerReportDTO, InputTranslation,
  PageCorrectionCostItemDTO,
  ProjectAuditControlCorrectionDTO,
  ProjectCallSettingsDTO,
  ProjectCorrectionIdentificationUpdateDTO,
} from '@cat/api';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, map, startWith, take, tap} from 'rxjs/operators';
import {
  AuditControlCorrectionDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-correction-overview/audit-control-correction-detail/audit-control-correction-detail-page.store';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {TableConfig} from '@common/directives/table-config/TableConfig';

enum LinkedToCostOptionType {
  PR,
  FTLS,
}

@UntilDestroy()
@Component({
  selector: 'jems-audit-control-correction-detail-identity',
  templateUrl: './audit-control-correction-detail-identity.component.html',
  styleUrls: ['./audit-control-correction-detail-identity.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class AuditControlCorrectionDetailIdentityComponent {

  naString = 'N/A';
  Alert = Alert;
  CorrectionFollowUpTypeEnum = ProjectAuditControlCorrectionDTO.CorrectionFollowUpTypeEnum;
  CorrectionTypeEnum = ProjectAuditControlCorrectionDTO.TypeEnum;
  CorrectionStatusEnum = ProjectAuditControlCorrectionDTO.StatusEnum;
  LinkedToCostOptionType = LinkedToCostOptionType;
  error$ = new BehaviorSubject<APIError | null>(null);
  data$: Observable<{
    correction: ProjectAuditControlCorrectionDTO;
    correctionPartnerData: CorrectionAvailablePartnerDTO[];
    canEdit: boolean;
    canClose: boolean;
    pastCorrections: AuditControlCorrectionDTO[];
    availableProcurements: Map<number, string>;
    isMandatoryScopeDefined: boolean;
    isLinkedToInvoice: boolean;
    projectId: number;
    costItemsTableConfig: {
      dataSource: PageCorrectionCostItemDTO;
      columnConfig: TableConfig[];
      availableColumns: string[];
    };
  }>;
  form: FormGroup;
  partnerReports: CorrectionAvailablePartnerReportDTO[] = [];
  funds: {
    id: number;
    abbreviation: InputTranslation[];
    disabled: boolean;
  }[] = [];

  linkedToCostOptionType: LinkedToCostOptionType = LinkedToCostOptionType.PR;
  ftls: CorrectionAvailableFtlsDTO[] = [];

  inputErrorMessages = {
    matDatetimePickerMin: 'common.error.field.to.after.from'
  };

  dateNameArgs = {
    lateRepaymentTo: 'to date'
  };

  readonly statusEnum = ProjectAuditControlCorrectionDTO.StatusEnum;

  isCostItemTableVisible: boolean;
  costItemsPageIndex$: BehaviorSubject<number>;
  costItemsPageSize$: BehaviorSubject<number>;

  costCategories: string[];

  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
    private pageStore: AuditControlCorrectionDetailPageStore,
    private projectStore: ProjectStore
  ) {
    this.data$ = combineLatest([
      pageStore.canEdit$,
      pageStore.canClose$,
      pageStore.correction$,
      pageStore.correctionPartnerData$,
      pageStore.pastCorrections$,
      pageStore.availableProcurements$.pipe(startWith(new Map())),
      pageStore.projectId$,
      pageStore.costItems$.pipe(startWith({content: Array.of()} as PageCorrectionCostItemDTO))
    ]).pipe(
      map(([
             canEdit,
             canClose,
             correction,
             correctionPartnerData,
             pastCorrections,
             availableProcurements,
             projectId,
             costItems
           ]: any) => ({
        correction,
        correctionPartnerData,
        canEdit,
        canClose,
        pastCorrections,
        availableProcurements,

        // TODO: set correctly
        isMandatoryScopeDefined: (!!correction.partnerId && (!!correction.partnerReportId || !!correction.lumpSumOrderNr)),
        isLinkedToInvoice: correction.type === this.CorrectionTypeEnum.LinkedToInvoice,
        projectId,
        costItemsTableConfig: {
          dataSource: costItems as PageCorrectionCostItemDTO,
          columnConfig: this.costItemsTableColumnConfig(correction.status, canEdit),
          availableColumns: this.getCostItemsAvailableColumns(correction.status, canEdit)
        }
      })),
      tap(data => this.resetForm(
        data.correction,
        data.correctionPartnerData,
        data.canEdit
      )),
      tap(data => {
        this.isCostItemTableVisible = data.isLinkedToInvoice && data.isMandatoryScopeDefined;
      })
    );

    this.costItemsPageIndex$ = pageStore.costItemsPageIndex$;
    this.costItemsPageSize$ = pageStore.costItemsPageSize$;

    this.projectStore.projectCallSettings$.pipe(
      tap(data => {
        this.costCategories = this.getCostCategories(data);
      }),
      untilDestroyed(this)
    ).subscribe();

  }

  resetForm(correctionIdentification: ProjectAuditControlCorrectionDTO, correctionPartnerData: CorrectionAvailablePartnerDTO[], editable: boolean) {
    const partner = correctionPartnerData.find((it: CorrectionAvailablePartnerDTO) => it.partnerId === correctionIdentification.partnerId);

    const report = partner?.availableReports.find((it: CorrectionAvailablePartnerReportDTO) => it.id === correctionIdentification.partnerReportId);
    const ftls = partner?.availableFtls.find((it: CorrectionAvailableFtlsDTO) => it.orderNr === correctionIdentification.lumpSumOrderNr);
    this.linkedToCostOptionType = ftls != null ? LinkedToCostOptionType.FTLS : LinkedToCostOptionType.PR;

    this.funds = this.mapFundsForSelector(report?.availableFunds ?? ftls?.availableFunds ?? []);
    const fund = this.funds.find(it => it.id === correctionIdentification.programmeFundId);

    if (partner) {
      this.partnerReports = partner.availableReports;
      this.ftls = partner.availableFtls;
    }
    this.form = this.formBuilder.group({
      followUpOfCorrectionId: [correctionIdentification.followUpOfCorrectionId || 0, Validators.required],
      correctionFollowUp: correctionIdentification.correctionFollowUpType,
      repaymentFrom: correctionIdentification.repaymentFrom,
      lateRepaymentTo: correctionIdentification.lateRepaymentTo,
      partnerId: [partner?.partnerId, Validators.required],
      partnerReportId: [report?.id],
      projectReportNumber: [report?.projectReport?.id ? ('PR.' + report.projectReport.number) : this.naString],
      lumpSumOrderNr: [ftls?.orderNr],
      programmeFundId: [fund?.id ?? this.naString, Validators.required],
      costCategory: correctionIdentification.costCategory ?? this.naString,
      procurementId: correctionIdentification.procurementId ?? this.naString,
      expenditureId: correctionIdentification.expenditureCostItem?.id ?? null
    });
    this.formService.init(this.form, of(editable));
    this.form.controls?.projectReportNumber?.disable();
  }

  getPartner(correctionPartnerData: CorrectionAvailablePartnerDTO[], partnerId: number): CorrectionAvailablePartnerDTO | undefined {
    return correctionPartnerData.find((partner: CorrectionAvailablePartnerDTO) => partner.partnerId === partnerId);
  }

  selectPartner(correctionPartnerData: CorrectionAvailablePartnerDTO[], partnerId: number): void {
    this.partnerReports = this.getPartner(correctionPartnerData, partnerId)?.availableReports ?? [];
    this.ftls = this.getPartner(correctionPartnerData, partnerId)?.availableFtls ?? [];
    this.form.controls?.projectReportNumber?.setValue(this.naString);
    this.form.controls?.lumpSumOrderNr?.setValue(null);
    this.form.controls?.programmeFundId?.setValue(null);
    this.form.updateValueAndValidity();
    this.funds = [];
    this.resetCorrectionScopeOptionalControls();
    this.isCostItemTableVisible = false;
  }

  selectReport(partnerReportId: number): void {
    this.resetCorrectionScopeOptionalControls();
    this.isCostItemTableVisible = false;
    if (partnerReportId) {
      const report = this.partnerReports.find(it => it.id === partnerReportId);
      if (report?.projectReport) {
        this.form.controls?.projectReportNumber?.setValue('PR.' + report?.projectReport.number);
      } else {
        this.form.controls?.projectReportNumber?.setValue(this.naString);
      }
      this.funds = this.mapFundsForSelector(report?.availableFunds ?? []);
      return;
    }
    this.form.controls?.projectReportNumber?.setValue(this.naString);
    this.form.controls?.programmeFundId?.setValue(null);
    this.form.updateValueAndValidity();
    this.funds = [];
  }

  selectFtls(lumpSumOrderNr: number) {
    this.isCostItemTableVisible = false;
    this.form.controls?.partnerReportId.setValue(null);
    this.form.controls?.projectReportNumber.setValue(null);
    this.form.controls?.costCategory?.setValue('LumpSum');
    if (lumpSumOrderNr) {
      const ftls = this.ftls.find(it => it.orderNr === lumpSumOrderNr);
      this.funds = this.mapFundsForSelector(ftls?.availableFunds ?? []);
      return;
    }
    this.form.controls?.programmeFundId?.setValue(null);
    this.form.updateValueAndValidity();
    this.funds = [];
  }

  save(id: number) {
    const data = {
      followUpOfCorrectionId: this.form.controls?.followUpOfCorrectionId.value || null,
      correctionFollowUpType: this.form.controls?.correctionFollowUp.value,
      repaymentFrom: this.form.controls?.repaymentFrom.value,
      lateRepaymentTo: this.form.controls?.lateRepaymentTo.value,
      partnerId: this.form.controls?.partnerId.value,
      partnerReportId: this.form.controls?.partnerReportId.value,
      lumpSumOrderNr: this.form.controls?.lumpSumOrderNr.value,
      programmeFundId: this.form.controls?.programmeFundId.value !== this.naString ? this.form.controls?.programmeFundId.value : null,
      costCategory: this.form.controls?.costCategory.value !== this.naString ? this.form.controls?.costCategory.value : null,
      procurementId: this.form.controls?.procurementId.value !== this.naString ? this.form.controls?.procurementId?.value : null,
      expenditureId: this.form.controls?.expenditureId?.value ?? null
    } as ProjectCorrectionIdentificationUpdateDTO;
    this.pageStore.saveCorrection(id, data)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.reporting.corrections.update.correction.success')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  setCostItemValue(costItemId: number): void {
    this.form.controls?.expenditureId?.setValue(costItemId);
    this.formService.setDirty(true);
  }

  private mapFundsForSelector(funds: CorrectionAvailableFundDTO[]): any[] {
    return funds.map(fund => ({
      ...fund.fund,
      disabled: (fund as any).disabled,
    }));
  }

  private getCostCategories(callSettings: ProjectCallSettingsDTO) {
    return [
      'Staff',
      'Office',
      'Travel',
      'External',
      'Equipment',
      'Infrastructure',
      'Other',
      'LumpSum',
      'UnitCost',
      ...callSettings.callType === ProjectCallSettingsDTO.CallTypeEnum.SPF ? ['SpfCost'] : []
    ];
  }

  private resetCorrectionScopeOptionalControls() {
    this.form.controls?.costCategory?.setValue('N/A');
    this.form.controls?.procurementId?.setValue(null);
    this.form.controls?.expenditureId?.setValue(null);
  }


  private getCostItemsAvailableColumns(status: ProjectAuditControlCorrectionDTO.StatusEnum, canEdit: boolean): string[] {
    return [
      'select',
      'id',
      'unitCostsAndLumpSums',
      'costCategory',
      'investmentNo',
      'procurement',
      'internalReference',
      'invoiceNo',
      'invoiceDate',
      'declaredAmount',
      'currency',
      'declaredAmountEur'
    ];
  }

  private costItemsTableColumnConfig(status: ProjectAuditControlCorrectionDTO.StatusEnum, canEdit: boolean): TableConfig[] {
    return [
      {minInRem: 3, maxInRem: 3}, // select
      {minInRem: 3, maxInRem: 3}, // id

      {minInRem: 11, maxInRem: 16}, // unitCostsAndLumpSums
      {minInRem: 11, maxInRem: 16}, // costCategory
      {minInRem: 5, maxInRem: 8},   // investmentNo
      {minInRem: 8, maxInRem: 8},   // procurement

      {minInRem: 5, maxInRem: 8},   // internalReference
      {minInRem: 5, maxInRem: 5},   // invoiceNo
      {minInRem: 8, maxInRem: 8},   // invoiceDate
      {minInRem: 8, maxInRem: 8},   // declaredAmount
      {minInRem: 4, maxInRem: 4},   // currency
      {minInRem: 7, maxInRem: 8},   // declaredAmountEur
    ];
  }

  clearValues() {
    this.form.controls?.partnerId?.setValue(null);
    this.form.controls?.partnerReportId.setValue(null);
    this.form.controls?.projectReportNumber?.setValue(this.naString);
    this.form.controls?.lumpSumOrderNr.setValue(null);
    this.form.controls?.programmeFundId?.setValue(null);
    this.form.controls?.costCategory?.setValue(null);
    this.form.controls?.procurementId?.setValue(null);
    this.form.controls?.expenditureId?.setValue(null);
  }

  get fundShown(): FundShown | undefined {
    const fundId = this.form.get('programmeFundId')?.value;
    return this.funds.find(x => x.id === fundId);
  }

}

interface FundShown {
  abbreviation: InputTranslation[];
  disabled: boolean;
}
