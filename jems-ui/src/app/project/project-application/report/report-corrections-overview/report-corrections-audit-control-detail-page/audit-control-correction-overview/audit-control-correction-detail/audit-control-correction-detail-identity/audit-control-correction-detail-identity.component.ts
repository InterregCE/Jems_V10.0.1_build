import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Alert} from '@common/components/forms/alert';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {
  CorrectionAvailablePartnerDTO,
  CorrectionAvailablePartnerReportDTO,
  ProgrammeFundDTO, ProjectAuditControlCorrectionDTO,
  ProjectCorrectionIdentificationDTO, ProjectCorrectionIdentificationUpdateDTO,
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
  @Input()
  auditControlNumber: number;

  @Input()
  correctionPartnerData: CorrectionAvailablePartnerDTO[];

  Alert = Alert;
  CorrectionFollowUpTypeEnum = ProjectCorrectionIdentificationDTO.CorrectionFollowUpTypeEnum;
  error$ = new BehaviorSubject<APIError | null>(null);
  data$: Observable<{
    projectId: number;
    auditControlId: number;
    correctionId: number;
    correctionIdentification: ProjectCorrectionIdentificationDTO;
    canEdit: boolean;
    canClose: boolean;
    pastCorrections: ProjectAuditControlCorrectionDTO[];
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
      pageStore.projectId$,
      pageStore.auditControlId$,
      pageStore.correctionId$,
      pageStore.canEdit$,
      pageStore.canClose$,
      pageStore.correctionIdentification$,
      pageStore.pastCorrections$
    ]).pipe(
      map(([
             projectId,
             auditControlId,
             correctionId,
             canEdit,
             canClose,
             correctionIdentification,
             pastCorrections
           ]: any) => ({
        projectId,
        auditControlId: Number(auditControlId),
        correctionId: Number(correctionId),
        canEdit,
        canClose,
        correctionIdentification,
        pastCorrections
      })),
      tap(data => this.resetForm(
        data.correctionIdentification,
        data.canEdit
      )),
    );
  }

  resetForm(
    correctionIdentification: ProjectCorrectionIdentificationDTO,
    editable: boolean
  ) {
    const partner = this.correctionPartnerData.filter(partnerToFilter => partnerToFilter.partnerId === correctionIdentification.partnerId)[0];
    const report = partner?.availableReports.filter(reportToFilter => reportToFilter.id === correctionIdentification.partnerReportId)[0];
    const fund = report?.availableReportFunds.filter(fundToFilter => fundToFilter.id === correctionIdentification.programmeFundId)[0];
    if (partner)
    {
      this.partnerReports = partner.availableReports;
    }
    this.form = this.formBuilder.group({
      followUpOfCorrectionId: [correctionIdentification.followUpOfCorrectionId ? correctionIdentification.followUpOfCorrectionId : null],
      correctionFollowUp: correctionIdentification.correctionFollowUpType,
      repaymentFrom: correctionIdentification.repaymentFrom,
      lateRepaymentTo: correctionIdentification.lateRepaymentTo,
      partnerId: [partner ? partner.partnerId : null, Validators.required],
      partnerReportId: [report ? report.id : null, Validators.required],
      projectReportNumber: [report?.projectReport?.id ? ('PR.' + report.projectReport.number) : 'N/A'],
      programmeFundId: [fund ? fund.id : 'N/A', Validators.required],
    });
    this.formService.init(this.form, of(editable));
    this.form.controls?.projectReportNumber?.disable();
    if (report && report?.availableReportFunds && report?.availableReportFunds?.length > 0) {
      this.funds = report?.availableReportFunds;
    } else {
      this.funds = [];
    }
  }

  selectPartner($event: any): void {
    if ($event !== null) {
      this.partnerReports = this.correctionPartnerData.filter(partner => partner.partnerId === $event)[0]?.availableReports;
      return;
    }
    this.partnerReports = [];
  }

  selectReport($event: any): void {
    if ($event !== null) {
      const report = this.partnerReports.filter(reportToFilter => reportToFilter.id === $event)[0];
      if (report.projectReport) {
        this.form.controls?.projectReportNumber?.setValue('PR.' + report.projectReport.number);
      } else {
        this.form.controls?.projectReportNumber?.setValue('N/A');
      }
      this.funds = report?.availableReportFunds;
      return;
    }
    this.form.controls?.projectReportNumber?.setValue('N/A');
    this.form.controls?.programmeFundId?.setValue(null);
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
      programmeFundId: this.form.controls?.programmeFundId.value !== 'N/A' ? this.form.controls?.programmeFundId.value : null
    } as ProjectCorrectionIdentificationUpdateDTO;
    this.pageStore.saveCorrection(id, data)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.reporting.corrections.update.correction.success')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  getPartnerForIdValue(): CorrectionAvailablePartnerDTO {
    return this.correctionPartnerData.filter(partner => partner.partnerId === this.form.controls.partnerId.value)[0];
  }
}
