import {Component} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {
  AuditControlDTO,
  PageProjectAuditControlCorrectionLineDTO, ProjectAuditControlCorrectionLineDTO,
  ProjectAuditControlUpdateDTO,
  UserRoleCreateDTO,
  UserRoleDTO
} from '@cat/api';
import {
  ReportCorrectionsAuditControlDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/report-corrections-audit-control-detail-page.store';
import {catchError, finalize, map, take, tap} from 'rxjs/operators';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {
  AuditControlConstants
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-identity/audit-control-identity.constants';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {Alert} from '@common/components/forms/alert';
import {APIError} from '@common/models/APIError';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {
  AuditControlCorrectionStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-correction-overview/audit-control-correction-store.service';

@Component({
  selector: 'jems-audit-control-identity',
  templateUrl: './audit-control-identity.component.html',
  styleUrls: ['./audit-control-identity.component.scss'],
  providers: [FormService]
})
export class AuditControlIdentityComponent {

  constants = AuditControlConstants;
  Alert = Alert;
  PermissionsEnum = PermissionsEnum;
  StatusEnum = AuditControlDTO.StatusEnum;

  pendingAction$ = new BehaviorSubject(false);
  error$ = new BehaviorSubject<APIError | null>(null);
  data$: Observable<{
    projectId: number;
    auditControl: AuditControlDTO;
    canEdit: boolean;
    isControllerDisabled: boolean;
    isClosingAllowed: boolean;
    isReopeningAllowed: boolean;
  }>;

  inputErrorMessages = {
    matDatetimePickerMin: 'common.error.field.start.before.end',
    matDatetimePickerMax: 'common.error.field.end.after.start'
  };

  dateNameArgs = {
    startDate: 'start date',
    endDate: 'end date'
  };

  form: FormGroup;
  isCreate: boolean;

  constructor(
    private pageStore: ReportCorrectionsAuditControlDetailPageStore,
    private formBuilder: FormBuilder,
    private formService: FormService,
    public router: RoutingService,
    private activatedRoute: ActivatedRoute,
    private correctionsOverviewStore: AuditControlCorrectionStore,
  ) {
    this.data$ = combineLatest([
      pageStore.projectId$,
      pageStore.auditControl$,
      pageStore.canEdit$,
      pageStore.canClose$,
      pageStore.canReopen$,
    ]).pipe(
      map(([projectId, auditControl, canEdit, canClose, canReopen]) => ({
        projectId,
        auditControl,
        canEdit,
        canClose,
        isControllerDisabled: auditControl.existsClosed,
        isClosingAllowed: !auditControl.existsOngoing,
        isReopeningAllowed: canReopen
      })),
      tap(data => this.resetForm(data.auditControl, data.canEdit)),
      tap(data => {
        this.isCreate = !data.auditControl.id;
        this.formService.setCreation(this.isCreate);
      }),
    );
  }

  resetForm(auditControl: AuditControlDTO, editable: boolean) {
    this.form = this.formBuilder.group({
      controllingBody: [auditControl.controllingBody, Validators.required],
      controlType: [auditControl.controlType, Validators.required],
      startDate: auditControl.startDate,
      endDate: auditControl.endDate,
      finalReportDate: auditControl.finalReportDate,
      totalControlledAmount: auditControl.totalControlledAmount ?? 0,
      totalCorrectionsAmount: auditControl.totalCorrectionsAmount ?? 0,
      comment: [auditControl.comment, Validators.maxLength(2000)],
    });
    this.formService.init(this.form, of(editable));

    // TODO: delete in next feature, after Corrections are added
    this.form.controls?.totalCorrectionsAmount?.disable();
  }

  save(id: number | undefined) {
    const data = {...this.form.value} as ProjectAuditControlUpdateDTO;
    this.pageStore.saveAuditControl(id, data)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.reporting.corrections.create.audit.success')),
        tap((auditControl: AuditControlDTO) => !id && this.redirectToDetails(auditControl.id)),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  closeAuditControl(projectId: number, auditControlId: number) {
    this.pendingAction$.next(true);
    this.pageStore.closeAuditControl(projectId, auditControlId).pipe(
      take(1),
      tap(()=>this.redirectToCorrections(projectId)),
      catchError(error => this.showErrorMessage(error.error$)),
      finalize(() => this.pendingAction$.next(false)),
    ).subscribe();
  }

  reopenAuditControl(projectId: number, auditControlId: number) {
    this.pageStore.reopenAuditControl(projectId, auditControlId).pipe(
      take(1),
      tap(() => this.formService.setEditable(true)),
      catchError(error => this.showErrorMessage(error.error$)),
    ).subscribe();
  }

  redirectToCorrections(projectId: number) {
    this.router.navigate([`/app/project/detail/${projectId}/corrections`]);
  }

  redirectToDetails(id: number) {
    this.router.navigate([`../auditControl/${id}`], {relativeTo: this.activatedRoute});
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      if (this.error$.value?.id === error.id) {
        this.error$.next(null);
      }
    }, 10000);

    return of(null);
  }
}
