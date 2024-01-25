import {ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {
  ChecklistInstanceDTO,
  ControllerInstitutionsApiService,
  IdNamePairDTO,
  ProgrammeChecklistDetailDTO,
  ProjectPartnerReportSummaryDTO,
  UserDTO,
  UserRoleDTO,
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {ControlChecklistInstanceListStore} from '@common/components/checklist/control-checklist-instance-list/control-checklist-instance-list-store.service';
import {catchError, filter, finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';
import {FormService} from '@common/components/section/form/form.service';
import {TableComponent} from '@common/components/table/table.component';
import {MatSort} from '@angular/material/sort';
import {PartnerReportDetailPageStore} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {ChecklistUtilsComponent} from '@common/components/checklist/checklist-utils/checklist-utils';
import {ChecklistSort} from '@common/components/checklist/checklist-instance-list/checklist-instance-list-custom-sort';
import {FormBuilder, Validators} from '@angular/forms';
import {ChecklistItem} from '@common/components/checklist/checklist-item';
import {AlertMessage} from '@common/components/file-list/file-list-table/alert-message';
import {Alert} from '@common/components/forms/alert';
import {SecurityService} from '../../../../security/security.service';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {ReportUtil} from '@project/common/report-util';
import {LanguageStore} from '@common/services/language-store.service';
import {DownloadService} from '@common/services/download.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'jems-control-checklist-instance-list',
  templateUrl: './control-checklist-instance-list.component.html',
  styleUrls: ['./control-checklist-instance-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ControlChecklistInstanceListStore, FormService]
})
export class ControlChecklistInstanceListComponent implements OnInit {
  Status = ChecklistInstanceDTO.StatusEnum;
  currentUser: UserDTO | null;
  isInstantiationInProgress = false;

  @Input()
  relatedType: ProgrammeChecklistDetailDTO.TypeEnum;
  @Input()
  relatedId: number;
  @Input()
  controlReportControlFinalizedDate: Date;

  partnerId = Number(this.routingService.getParameter(this.activatedRoute, 'partnerId'));
  reportId = Number(this.routingService.getParameter(this.activatedRoute, 'reportId'));

  data$: Observable<{ reportId: number }>;

  descriptionForm = this.formBuilder.group({
    id: [null, Validators.required],
    editable: [false],
    description: ['', Validators.maxLength(150)],
  });

  private checklistInstances$: Observable<ChecklistInstanceDTO[]>;
  private checklistInstances: ChecklistInstanceDTO[];
  checklistInstancesSorted$: Observable<ChecklistInstanceDTO[]>;
  checklistTemplates$: Observable<IdNamePairDTO[]>;
  userCanEditControlChecklists$: Observable<boolean>;
  alerts$ = new BehaviorSubject<AlertMessage[]>([]);

  instancesTableConfiguration: TableConfiguration;
  selectedTemplate: IdNamePairDTO;
  checklistUtils: ChecklistUtilsComponent;

  @ViewChild('actionsCell', {static: true})
  actionsCell: TemplateRef<any>;

  @ViewChild('descriptionCell', {static: true})
  descriptionCell: TemplateRef<any>;

  @ViewChild('lockCell', {static: true})
  lockCell: TemplateRef<any>;

  @ViewChild('tableInstances') tableInstances: TableComponent;

  constructor(public pageStore: ControlChecklistInstanceListStore,
              private projectStore: ProjectStore,
              private formService: FormService,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private controllerInstitutionService: ControllerInstitutionsApiService,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore,
              private permissionService: PermissionService,
              private securityService: SecurityService,
              private languageStore: LanguageStore,
              private downloadService: DownloadService) {
    this.checklistUtils = new ChecklistUtilsComponent();
    this.userCanEditControlChecklists$ = this.userCanEditControlChecklists();
    this.securityService.currentUserDetails.subscribe(
      currentUser => this.currentUser = currentUser
    );
    this.data$ = combineLatest([
      this.partnerReportDetailPageStore.partnerReport$,
    ]).pipe(
      map(([report]) => ({reportId: report.reportNumber})),
    );
  }

  onInstancesSortChange(sort: Partial<MatSort>) {
    const order = sort.direction;
    this.pageStore.setInstancesSort({...sort, direction: order === 'desc' ? 'desc' : 'asc'});
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
            (level === 'Edit' && ReportUtil.isControlReportOpen(reportStatus) || ReportUtil.isControlCertifiedReOpened(reportStatus))
            ||
            ((level === 'Edit' || level === 'View' || canViewReport)
                && reportStatus === ProjectPartnerReportSummaryDTO.StatusEnum.Certified
                && canEditChecklistsAfterControl)
        )
      );
  }

  currentUserIsCreator(checkList: ChecklistItem) {
    return checkList?.creatorEmail === this.currentUser?.email;
  }

  private institutionUserControlReportLevel(): Observable<string> {
    return this.controllerInstitutionService.getControllerUserAccessLevelForPartner(this.partnerId);
  }

  ngOnInit(): void {
    this.checklistInstances$ = this.pageStore.controlChecklistInstances(this.partnerId, this.reportId);
    this.checklistInstancesSorted$ = combineLatest([
      this.checklistInstances$,
      this.pageStore.getInstancesSort$,
    ]).pipe(
      map(([checklists, sort]) => [...checklists].sort(ChecklistSort.customSort(sort))),
      tap(data => this.checklistInstances = data)
    );
    this.checklistTemplates$ = this.pageStore.checklistTemplates(this.relatedType);
    this.instancesTableConfiguration = this.checklistUtils.initializeTableConfigurationWithLock(this.actionsCell, this.descriptionCell, this.lockCell);
  }

  delete(checklist: ChecklistInstanceDTO): void {
    Forms.confirm(
      this.dialog, {
        title: checklist.name,
        message: {i18nKey: 'checklists.instance.delete.confirm', i18nArguments: {name: checklist.name}}
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.pageStore.deleteChecklistInstance(this.partnerId, this.relatedId, checklist.id)),
        catchError(error => {
          this.showAlert(ChecklistUtilsComponent.errorAlert(
            'use.case.delete.checklist.instance.failed'
          ));
          throw error;
        }),
        ).subscribe();
  }

  createInstance(): void {
    this.isInstantiationInProgress = true;
    this.pageStore.createInstance(this.partnerId, this.reportId, this.relatedId, this.selectedTemplate.id)
      .pipe(
        tap(instanceId => this.routingService.navigate(
            ['checklist', instanceId],
            {relativeTo: this.activatedRoute}
          )
        ),
        finalize(() => this.isInstantiationInProgress = false)
      ).subscribe();
  }

  isEditable(): boolean {
    return this.formService.isEditable();
  }

  editDescription(checklist: ChecklistItem) {
    this.descriptionForm.patchValue({
      id: checklist.id,
      editable: true,
      description: checklist.description,
    });
  }

  savingDescriptionId$ = new BehaviorSubject<number | null>(null);

  saveDescription() {
    this.savingDescriptionId$.next(this.descriptionForm.value.id);
    this.pageStore.updateInstanceDescription(this.descriptionForm.value.id, this.partnerId, this.reportId, this.descriptionForm.value.description)
      .pipe(
        take(1),
        tap(() => this.savingDescriptionId$.next(null)),
        tap(() => this.showAlert(ChecklistUtilsComponent.successAlert(
          'contracting.monitoring.checklists.description.change.message.success'
        ))),
        tap(() => this.updateChecklistInstancesAfterDescriptionSave(this.descriptionForm.value.id, this.descriptionForm.value.description)),
        catchError(error => {
          this.showAlert(ChecklistUtilsComponent.errorAlert(
            'contracting.monitoring.checklists.description.change.message.failure'
          ));
          throw error;
        }),
        finalize(() => this.savingDescriptionId$.next(null)),
        tap(() => this.descriptionForm.reset()),
      ).subscribe();

    this.descriptionForm.value.editable = false;
  }

  dismissAlert(id: string) {
    const alerts = this.alerts$.value.filter(that => that.id !== id);
    this.alerts$.next(alerts);
  }

  private showAlert(alert: AlertMessage) {
    this.alerts$.next([...this.alerts$.value, alert]);
    setTimeout(
      () => this.dismissAlert(alert.id),
      alert.type === Alert.SUCCESS ? 5000 : 30000);
  }


  private updateChecklistInstancesAfterDescriptionSave(checklistId: number, description: string | undefined) {
    const checklistInstancesIndex = this.checklistInstances.findIndex(c => c.id === checklistId);
    if (checklistInstancesIndex >= 0) {
      this.checklistInstances[checklistInstancesIndex].description = description ?? '';
      this.checklistInstances$ = of(this.checklistInstances);
    }
  }

  resetDescription() {
    this.descriptionForm.value.editable = false;
    this.descriptionForm.reset();
  }

  isAfterControlChecklist(createdAt: Date): boolean {
      if (this.controlReportControlFinalizedDate === null) {
          return true;
      }
      return createdAt > this.controlReportControlFinalizedDate;
  }

  download(checklistId: number) {
    combineLatest([
      this.pageStore.availablePlugins$,
      this.languageStore.currentSystemLanguage$,
    ]).pipe(
      take(1),
      map(([plugins, systemLanguage]) => {
        const plugin = plugins[0];
        if (plugin?.type) {
          const url = `/api/controlChecklist/byPartnerId/${this.partnerId}/byReportId/${this.relatedId}/export/${checklistId}?exportLanguage=${systemLanguage}&pluginKey=${plugin.key}`;
          this.downloadService.download(url, 'checklist-export.pdf').subscribe();
        }
      })).subscribe();
  }

  clone(checklistId: number) {
    this.pageStore.clone(this.partnerId, this.reportId, checklistId)
        .pipe(
            tap(clonedInstanceId => this.routingService.navigate(
                    ['checklist', clonedInstanceId],
                    {relativeTo: this.activatedRoute}
                )
            )
        ).subscribe();
  }
}
