import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormService } from '@common/components/section/form/form.service';
import { combineLatest, Observable } from 'rxjs';
import {
  AuditControlCorrectionImpactDTO,
} from '@cat/api';
import { catchError, map, take, tap } from 'rxjs/operators';
import {
  AuditControlCorrectionImpactStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-correction-overview/audit-control-correction-detail/audit-control-correction-impact/audit-control-correction-impact.store';

@Component({
  selector: 'jems-audit-control-correction-impact',
  templateUrl: './audit-control-correction-impact.component.html',
  styleUrls: ['./audit-control-correction-impact.component.scss'],
  providers: [FormService],
})
export class AuditControlCorrectionImpactComponent {

  data$: Observable<{
    impact: AuditControlCorrectionImpactDTO;
    canEdit: boolean;
  }>;

  form: FormGroup = this.formBuilder.group({
    action: this.formBuilder.control(null, Validators.required),
    comment: this.formBuilder.control(null, Validators.maxLength(2000)),
  });

  actions = [
    AuditControlCorrectionImpactDTO.ActionEnum.NA,
    AuditControlCorrectionImpactDTO.ActionEnum.RepaymentByProject,
    AuditControlCorrectionImpactDTO.ActionEnum.AdjustmentInNextPayment,
    AuditControlCorrectionImpactDTO.ActionEnum.BudgetReduction,
    AuditControlCorrectionImpactDTO.ActionEnum.RepaymentByNA,
  ];

  constructor(
    private formService: FormService,
    private formBuilder: FormBuilder,
    private impactStore: AuditControlCorrectionImpactStore,
  ) {
    this.formService.init(this.form, impactStore.canEdit$);
    this.data$ = combineLatest([
      impactStore.impact$,
      impactStore.canEdit$,
    ]).pipe(
      map(([impact, canEdit]) => ({
        impact,
        canEdit,
      })),
      tap(data => this.resetForm(data.impact)),
    );
  }

  resetForm(impact: AuditControlCorrectionImpactDTO) {
    this.form.setValue(impact);
  }

  update() {
    this.impactStore.updateImpact(this.form.value).pipe(
      take(1),
      tap(() => this.formService.setSuccess('auditControl.correction.impact.save.success')),
      catchError(err => this.formService.setError(err))
    ).subscribe();
  }

}
