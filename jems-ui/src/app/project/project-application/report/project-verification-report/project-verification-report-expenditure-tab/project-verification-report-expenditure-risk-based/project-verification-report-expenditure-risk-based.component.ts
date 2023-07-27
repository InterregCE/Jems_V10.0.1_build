import {Component, Input, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {
  ProjectVerificationReportExpenditureConstants
} from '@project/project-application/report/project-verification-report/project-verification-report-expenditure-tab/project-verification-report-expenditure.constants';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ProjectReportVerificationRiskBasedDTO} from '@cat/api';
import {
  ProjectVerificationReportExpenditureStore
} from '@project/project-application/report/project-verification-report/project-verification-report-expenditure-tab/project-verification-report-expenditure.store';
import {catchError, take, tap} from 'rxjs/operators';

@Component({
  selector: 'jems-project-verification-report-expenditure-risk-based',
  templateUrl: './project-verification-report-expenditure-risk-based.component.html',
  styleUrls: ['./project-verification-report-expenditure-risk-based.component.scss'],
  providers: [FormService],
})
export class ProjectVerificationReportExpenditureRiskBasedComponent implements OnInit {

  FORM_CONTROL = ProjectVerificationReportExpenditureConstants.RISK_BASED_FORM_CONTROL_NAMES;

  @Input()
  riskBasedVerification: ProjectReportVerificationRiskBasedDTO;

  form: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
    private expenditureVerificationStore: ProjectVerificationReportExpenditureStore,
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.resetForm(this.riskBasedVerification);
  }

  initForm() {
    this.form = this.formBuilder.group({
      riskBasedVerification: this.formBuilder.control(false),
      riskBasedVerificationDescription: this.formBuilder.control(''),
    });

    this.formService.init(this.form, this.expenditureVerificationStore.isEditable$);
  }

  resetForm(riskBasedVerification: ProjectReportVerificationRiskBasedDTO) {
    this.form.get(this.FORM_CONTROL.riskBasedVerification)?.patchValue(riskBasedVerification.riskBasedVerification);
    this.form.get(this.FORM_CONTROL.riskBasedVerificationDescription)?.patchValue(riskBasedVerification.riskBasedVerificationDescription);
  }

  private riskBasedValue(control: string) {
    return this.form?.get(control)?.value;
  }

  save() {
    const updateRiskBasedVerificationDTO = this.getRiskBasedVerification();
    this.expenditureVerificationStore.updateRiskBasedVerification(updateRiskBasedVerificationDTO)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.project.verification.work.tab.expenditure.risk.form.save.success')),
        catchError(err => this.formService.setError(err)),
      ).subscribe();
  }

  private getRiskBasedVerification(): ProjectReportVerificationRiskBasedDTO {
    return {
      riskBasedVerification: this.riskBasedValue(this.FORM_CONTROL.riskBasedVerification),
      riskBasedVerificationDescription: this.riskBasedValue(this.FORM_CONTROL.riskBasedVerificationDescription),
    } as ProjectReportVerificationRiskBasedDTO;
  }

}
