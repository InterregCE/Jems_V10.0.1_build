import {Component} from '@angular/core';
import {
  AuditControlCorrectionDetailMeasureConstants
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-correction-overview/audit-control-correction-detail/audit-control-correction-detail-measure/audit-control-correction-detail-measure.constants';
import {FormBuilder, FormGroup} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {
  CorrectionAvailablePartnerDTO, CorrectionAvailablePartnerReportDTO, CorrectionAvailablePaymentDTO,
  PaymentApplicationToEcDTO,
  ProjectCorrectionIdentificationDTO,
  ProjectCorrectionProgrammeMeasureDTO,
  ProjectCorrectionProgrammeMeasureUpdateDTO
} from '@cat/api';
import {
  AuditControlCorrectionDetailMeasureStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-correction-overview/audit-control-correction-detail/audit-control-correction-detail-measure/audit-control-correction-detail-measure.store';
import {catchError, map, take, tap} from 'rxjs/operators';

@Component({
  selector: 'jems-audit-control-correction-detail-measure',
  templateUrl: './audit-control-correction-detail-measure.component.html',
  styleUrls: ['./audit-control-correction-detail-measure.component.scss'],
  providers: [FormService],
})
export class AuditControlCorrectionDetailMeasureComponent {

  constants = AuditControlCorrectionDetailMeasureConstants;

  data$: Observable<{
    partnerData: CorrectionAvailablePartnerDTO[];
    identification: ProjectCorrectionIdentificationDTO;
    programmeMeasure: ProjectCorrectionProgrammeMeasureDTO;
    canEdit: boolean;
  }>;

  form: FormGroup = this.formBuilder.group({
    correctionId: this.formBuilder.control(null),
    declaredToEc: this.formBuilder.control({value: false, disabled: true}),
    paymentToEcNumber: this.formBuilder.control({value: '', disabled: true}),
    paymentAccountingYear: this.formBuilder.control({
      value: {
        id: 0,
        year: 2021,
        startDate: new Date() as unknown as string,
        endDate: new Date() as unknown as string
      }, disabled: true
    }),
    scenario: this.formBuilder.control(null),
    comment: this.formBuilder.control(null),
    includedInAccountingYear: this.formBuilder.control({value: null, disabled: true}),
  });

  scenarios = [
    ProjectCorrectionProgrammeMeasureDTO.ScenarioEnum.NA,
    ProjectCorrectionProgrammeMeasureDTO.ScenarioEnum.SCENARIO1,
    ProjectCorrectionProgrammeMeasureDTO.ScenarioEnum.SCENARIO2,
    ProjectCorrectionProgrammeMeasureDTO.ScenarioEnum.SCENARIO3,
    ProjectCorrectionProgrammeMeasureDTO.ScenarioEnum.SCENARIO4,
    ProjectCorrectionProgrammeMeasureDTO.ScenarioEnum.SCENARIO5,
  ];

  constructor(
    private formService: FormService,
    private formBuilder: FormBuilder,
    private programmeMeasureStore: AuditControlCorrectionDetailMeasureStore,
  ) {
    this.formService.init(this.form, programmeMeasureStore.canEdit$);
    this.data$ = combineLatest([
      programmeMeasureStore.correctionPartnerData$,
      programmeMeasureStore.correctionIdentity$,
      programmeMeasureStore.programmeMeasure$,
      programmeMeasureStore.canEdit$,
    ]).pipe(
      map(([partnerData, identification, programmeMeasure, canEdit]) => ({
        partnerData, identification, programmeMeasure, canEdit
      })),
      tap(data => this.resetForm(data.partnerData, data.identification, data.programmeMeasure)),
    );
  }

  scenarioChanged(scenario: ProjectCorrectionProgrammeMeasureDTO.ScenarioEnum) {
    this.form.get(this.constants.FORM_CONTROLS.scenario)?.patchValue(scenario);
    this.form.get(this.constants.FORM_CONTROLS.scenario)?.markAsTouched();
    this.form.get(this.constants.FORM_CONTROLS.scenario)?.markAsDirty();
    this.form.get(this.constants.FORM_CONTROLS.scenario)?.updateValueAndValidity();
  }

  resetForm(partnerData: CorrectionAvailablePartnerDTO[], identification: ProjectCorrectionIdentificationDTO, programmeMeasure: ProjectCorrectionProgrammeMeasureDTO) {
    const ecPayment: PaymentApplicationToEcDTO | undefined = this.filterEcPayment(partnerData, identification);

    this.form.setValue({
      ...programmeMeasure,
      declaredToEc: !!ecPayment,
      paymentToEcNumber: ecPayment?.id ?? null,
      paymentAccountingYear: ecPayment?.accountingYear ?? null
    });
    this.form.get(this.constants.FORM_CONTROLS.declaredToEc)?.disable();
    this.form.get(this.constants.FORM_CONTROLS.paymentToEcNumber)?.disable();
    this.form.get(this.constants.FORM_CONTROLS.paymentAccountingYear)?.disable();
    this.form.get(this.constants.FORM_CONTROLS.includedInAccountingYear)?.disable();
  }

  update() {
    const data = {
      scenario: this.form.get(this.constants.FORM_CONTROLS.scenario)?.value,
      comment: this.form.get(this.constants.FORM_CONTROLS.comment)?.value,
    } as ProjectCorrectionProgrammeMeasureUpdateDTO;
    this.programmeMeasureStore.updateProgrammeMeasure(data).pipe(
      take(1),
      tap(() => this.formService.setSuccess('project.application.reporting.corrections.programme.measure.save.success')),
      catchError(err => this.formService.setError(err))
    ).subscribe();
  }

  private filterEcPayment(partnerData: CorrectionAvailablePartnerDTO[], identification: ProjectCorrectionIdentificationDTO): PaymentApplicationToEcDTO | undefined {
    const partner = partnerData.find((it: CorrectionAvailablePartnerDTO) => it.partnerId === identification.partnerId);
    const report = partner?.availableReports.find((it: CorrectionAvailablePartnerReportDTO) => it.id === identification.partnerReportId);
    const fund = report?.availablePayments.find((it: CorrectionAvailablePaymentDTO) => it.fund.id === identification.programmeFundId);

    return fund?.ecPayment;
  }

}
