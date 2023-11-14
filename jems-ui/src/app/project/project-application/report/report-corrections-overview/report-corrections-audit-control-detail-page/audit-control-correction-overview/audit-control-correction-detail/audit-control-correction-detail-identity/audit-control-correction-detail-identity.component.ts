import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Alert} from '@common/components/forms/alert';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {
  AuditControlCorrectionDTO,
  CorrectionAvailablePartnerDTO,
  CorrectionAvailablePartnerReportDTO,
  ProgrammeFundDTO,
  ProjectAuditControlCorrectionDTO, ProjectCorrectionIdentificationUpdateDTO,
} from '@cat/api';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, map, take, tap} from 'rxjs/operators';
import {
  AuditControlCorrectionDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-correction-overview/audit-control-correction-detail/audit-control-correction-detail-page.store';

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
  error$ = new BehaviorSubject<APIError | null>(null);
  data$: Observable<{
    correction: ProjectAuditControlCorrectionDTO;
    correctionPartnerData: CorrectionAvailablePartnerDTO[];
    canEdit: boolean;
    canClose: boolean;
    pastCorrections: AuditControlCorrectionDTO[];
  }>;
  form: FormGroup;
  partnerReports: CorrectionAvailablePartnerReportDTO[] = [];
  funds: ProgrammeFundDTO[] = [];

  inputErrorMessages = {
    matDatetimePickerMin: 'common.error.field.to.after.from'
  };

  dateNameArgs = {
    lateRepaymentTo: 'to date'
  };

  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
    private pageStore: AuditControlCorrectionDetailPageStore,
  ) {
    this.data$ = combineLatest([
      pageStore.canEdit$,
      pageStore.canClose$,
      pageStore.correction$,
      pageStore.correctionPartnerData$,
      pageStore.pastCorrections$
    ]).pipe(
      map(([
             canEdit,
             canClose,
             correction,
             correctionPartnerData,
             pastCorrections
           ]) => ({
        canEdit,
        canClose,
        correction,
        correctionPartnerData,
        pastCorrections
      })),
      tap(data => this.resetForm(
        data.correction,
        data.correctionPartnerData,
        data.canEdit
      )),
    );
  }

  resetForm(correctionIdentification: ProjectAuditControlCorrectionDTO, correctionPartnerData: CorrectionAvailablePartnerDTO[], editable: boolean) {
    const partner = correctionPartnerData.find((it: CorrectionAvailablePartnerDTO) => it.partnerId === correctionIdentification.partnerId);
    const report = partner?.availableReports.find((it: CorrectionAvailablePartnerReportDTO) => it.id === correctionIdentification.partnerReportId);
    const fund = report?.availableReportFunds.find((it: ProgrammeFundDTO) => it.id === correctionIdentification.programmeFundId);
    if (partner) {
      this.partnerReports = partner.availableReports;
    }
    this.form = this.formBuilder.group({
      followUpOfCorrectionId: [correctionIdentification.followUpOfCorrectionId],
      correctionFollowUp: correctionIdentification.correctionFollowUpType,
      repaymentFrom: correctionIdentification.repaymentFrom,
      lateRepaymentTo: correctionIdentification.lateRepaymentTo,
      partnerId: [partner?.partnerId, Validators.required],
      partnerReportId: [report?.id, Validators.required],
      projectReportNumber: [report?.projectReport?.id ? ('PR.' + report.projectReport.number) : this.naString],
      programmeFundId: [fund?.id ?? this.naString, Validators.required],
    });
    this.formService.init(this.form, of(editable));
    this.form.controls?.projectReportNumber?.disable();
    if (report && report?.availableReportFunds && report?.availableReportFunds?.length > 0) {
      this.funds = report?.availableReportFunds;
    } else {
      this.funds = [];
    }
  }

  getPartner(correctionPartnerData: CorrectionAvailablePartnerDTO[], partnerId: number): CorrectionAvailablePartnerDTO | undefined {
    return correctionPartnerData.find((partner: CorrectionAvailablePartnerDTO) => partner.partnerId === partnerId);
  }

  selectPartner(correctionPartnerData: CorrectionAvailablePartnerDTO[], partnerId: number): void {
    this.partnerReports = this.getPartner(correctionPartnerData, partnerId)?.availableReports ?? [];
    this.form.controls?.projectReportNumber?.setValue(this.naString);
    this.form.controls?.programmeFundId?.setValue(null);
    this.form.updateValueAndValidity();
    this.funds = [];
  }

  selectReport(partnerReportId: number): void {
    if (partnerReportId) {
      const report = this.partnerReports.find(it => it.id === partnerReportId);
      if (report?.projectReport) {
        this.form.controls?.projectReportNumber?.setValue('PR.' + report?.projectReport.number);
      } else {
        this.form.controls?.projectReportNumber?.setValue(this.naString);
      }
      this.funds = report?.availableReportFunds ?? [];
      return;
    }
    this.form.controls?.projectReportNumber?.setValue(this.naString);
    this.form.controls?.programmeFundId?.setValue(null);
    this.form.updateValueAndValidity();
    this.funds = [];
  }

  save(id: number) {
    const data = {
      followUpOfCorrectionId: this.form.controls?.followUpOfCorrectionId.value,
      correctionFollowUpType: this.form.controls?.correctionFollowUp.value,
      repaymentFrom: this.form.controls?.repaymentFrom.value,
      lateRepaymentTo: this.form.controls?.lateRepaymentTo.value,
      partnerId: this.form.controls?.partnerId.value,
      partnerReportId: this.form.controls?.partnerReportId.value,
      programmeFundId: this.form.controls?.programmeFundId.value !== this.naString ? this.form.controls?.programmeFundId.value : null
    } as ProjectCorrectionIdentificationUpdateDTO;
    this.pageStore.saveCorrection(id, data)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.reporting.corrections.update.correction.success')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

}
