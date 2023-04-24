import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {
  PartnerControlReportControlChecklistPageStore
} from '@project/project-application/report/partner-control-report/partner-control-report-control-checklists-tab/partner-control-report-control-checklist-page/partner-control-report-control-checklist-page-store.service';
import {
    ChecklistComponentInstanceDTO,
    ChecklistInstanceDetailDTO,
    ControllerInstitutionsApiService,
    ProjectPartnerReportSummaryDTO, UserRoleDTO
} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {map, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {combineLatest, Observable} from 'rxjs';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {PermissionService} from "../../../../../../security/permissions/permission.service";
import {
    PartnerControlReportStore
} from "@project/project-application/report/partner-control-report/partner-control-report-store.service";

@Component({
  selector: 'jems-partner-control-report-control-checklist-page',
  templateUrl: './partner-control-report-control-checklist-page.component.html',
  styleUrls: ['./partner-control-report-control-checklist-page.component.scss'],
  providers: [PartnerControlReportControlChecklistPageStore, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerControlReportControlChecklistPageComponent {
  Status = ChecklistInstanceDetailDTO.StatusEnum;

  data$: Observable<{
    checklist: ChecklistInstanceDetailDTO;
    editable: boolean;
    reportEditable: boolean;
    isAfterControlChecklist: boolean;
  }>;

  confirmFinish = {
    title: 'checklists.instance.confirm.finish.title',
    message: 'checklists.instance.confirm.finish.message'
  };

  confirmReturnToInitiator = {
    title: 'checklists.instance.confirm.return.to.initiator.title',
    message: 'checklists.instance.confirm.return.to.initiator'
  };

  userCanEditControlChecklists$: Observable<boolean>;

  partnerId = Number(this.routingService.getParameter(this.activatedRoute, 'partnerId'));
  reportId = Number(this.routingService.getParameter(this.activatedRoute, 'reportId'));

  constructor(private projectSidenavService: ProjectApplicationFormSidenavService,
              private pageStore: PartnerControlReportControlChecklistPageStore,
              private formService: FormService,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              private controllerInstitutionService: ControllerInstitutionsApiService,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore,
              private partnerControlReportStore: PartnerControlReportStore,
              private permissionService: PermissionService) {
    this.data$ = combineLatest([
      this.pageStore.checklist$,
      this.pageStore.checklistEditable$,
      this.pageStore.reportEditable$,
      this.partnerControlReportStore.partnerControlReport$
    ]).pipe(
      map(([checklist, editable, reportEditable, controlReport]) => ({
          checklist,
          editable,
          reportEditable,
          isAfterControlChecklist: this.isAfterControlChecklist(checklist.createdAt, controlReport.reportControlEnd)
      })),
    );
    this.userCanEditControlChecklists$ = this.userCanEditControlChecklists();
  }

  private userCanEditControlChecklists(): Observable<boolean> {
      return combineLatest([
          this.institutionUserControlReportLevel(),
          this.partnerReportDetailPageStore.reportStatus$,
          this.permissionService.hasPermission(PermissionsEnum.ProjectReportingChecklistAfterControl),
          this.permissionService.hasPermission(PermissionsEnum.ProjectReportingView)
      ])
          .pipe(
              map(([level, reportStatus, canEditChecklistsAfterControl, canViewReport]) =>
                  (level === 'Edit' && reportStatus === ProjectPartnerReportSummaryDTO.StatusEnum.InControl)
                  ||
                  ((level === 'Edit' || level === 'View' || canViewReport)
                      && reportStatus === ProjectPartnerReportSummaryDTO.StatusEnum.Certified
                      && canEditChecklistsAfterControl)
              )
          );
  }

  private institutionUserControlReportLevel(): Observable<string> {
    return this.controllerInstitutionService.getControllerUserAccessLevelForPartner(this.partnerId);
  }

  save(checklist: ChecklistInstanceDetailDTO): void {
    checklist.components = this.getFormComponents();
    this.pageStore.updateChecklist(this.partnerId, this.reportId, checklist)
      .pipe(
        tap(() => this.formService.setSuccess('checklists.instance.saved.successfully'))
      ).subscribe();
  }

  updateStatus(checklistId: number, status: ChecklistInstanceDetailDTO.StatusEnum) {
    this.pageStore.changeStatus(this.partnerId, this.reportId, checklistId, status)
      .pipe(
        tap(() => this.formService.setDirty(false)),
        tap(() => this.routingService.navigate(['../..'], {relativeTo: this.activatedRoute}))
      ).subscribe();
  }

  private getFormComponents(): ChecklistComponentInstanceDTO[] {
    return this.formService.form.get('formComponents')?.value;
  }

  saveDiscardMenuIsActive(): boolean {
    return this.formService.form.dirty;
  }

  private isAfterControlChecklist(createdAt: Date, controlReportControlFinalizedDate: Date): boolean {
      if (controlReportControlFinalizedDate === null) {
          return true;
      }
      return createdAt > controlReportControlFinalizedDate;
  }
}
