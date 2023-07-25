import {ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {
  ChecklistInstanceDTO,
  IdNamePairDTO,
  ProgrammeChecklistDetailDTO, ProjectReportDTO,
  UserDTO, UserRoleDTO
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {FormBuilder, Validators} from '@angular/forms';
import {AlertMessage} from '@common/components/file-list/file-list-table/alert-message';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ChecklistUtilsComponent} from '@common/components/checklist/checklist-utils/checklist-utils';
import {TableComponent} from '@common/components/table/table.component';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {FormService} from '@common/components/section/form/form.service';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {SecurityService} from '../../../../security/security.service';
import {LanguageStore} from '@common/services/language-store.service';
import {DownloadService} from '@common/services/download.service';
import {catchError, filter, finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {MatSort} from '@angular/material/sort';
import {ReportUtil} from '@project/common/report-util';
import {ChecklistItem} from '@common/components/checklist/checklist-item';
import {ChecklistSort} from '@common/components/checklist/checklist-instance-list/checklist-instance-list-custom-sort';
import {Forms} from '@common/utils/forms';
import {Alert} from '@common/components/forms/alert';
import {
  VerificationChecklistInstanceListStore
} from '@common/components/checklist/verification-checklist-instance-list/verification-checklist-instance-list-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';

@Component({
  selector: 'jems-verification-checklist-instance-list',
  templateUrl: './verification-checklist-instance-list.component.html',
  styleUrls: ['./verification-checklist-instance-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [VerificationChecklistInstanceListStore, FormService]
})
export class VerificationChecklistInstanceListComponent implements OnInit {
  Status = ChecklistInstanceDTO.StatusEnum;
  currentUser: UserDTO | null;
  isInstantiationInProgress = false;

  @Input()
  relatedType: ProgrammeChecklistDetailDTO.TypeEnum;
  @Input()
  relatedId: number;
  @Input()
  verificationReportVerificationFinalizedDate: Date | null;

  projectId = Number(this.routingService.getParameter(this.activatedRoute, 'projectId'));
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
  userCanEditVerificationChecklists$: Observable<boolean>;
  alerts$ = new BehaviorSubject<AlertMessage[]>([]);

  instancesTableConfiguration: TableConfiguration;
  selectedTemplate: IdNamePairDTO;
  checklistUtils: ChecklistUtilsComponent;

  @ViewChild('actionsCell', {static: true})
  actionsCell: TemplateRef<any>;

  @ViewChild('descriptionCell', {static: true})
  descriptionCell: TemplateRef<any>;

  @ViewChild('tableInstances') tableInstances: TableComponent;

  constructor(public pageStore: VerificationChecklistInstanceListStore,
              private projectStore: ProjectStore,
              private formService: FormService,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private projectReportDetailPageStore: ProjectReportDetailPageStore,
              private projectReportStore: ProjectReportPageStore,
              private permissionService: PermissionService,
              private securityService: SecurityService,
              private languageStore: LanguageStore,
              private downloadService: DownloadService) {
    this.checklistUtils = new ChecklistUtilsComponent();
    this.userCanEditVerificationChecklists$ = this.userCanEditVerificationChecklists();
    this.securityService.currentUserDetails.subscribe(
      currentUser => this.currentUser = currentUser
    );
    this.data$ = combineLatest([
      this.projectReportDetailPageStore.projectReport$,
    ]).pipe(
      map(([report]) => ({reportId: report.reportNumber})),
    );
  }

  onInstancesSortChange(sort: Partial<MatSort>) {
    const order = sort.direction;
    this.pageStore.setInstancesSort({...sort, direction: order === 'desc' ? 'desc' : 'asc'});
  }

  private userCanEditVerificationChecklists(): Observable<boolean> {
    return combineLatest([
      this.projectReportDetailPageStore.reportStatus$,
      this.projectReportStore.userCanEditVerification$,
    ])
      .pipe(
        map(([reportStatus, canEditVerification]) =>
          (canEditVerification && ReportUtil.isVerificationReportOpen(reportStatus))
          ||
          (canEditVerification && reportStatus === ProjectReportDTO.StatusEnum.Finalized)
        )
      );
  }

  currentUserIsCreator(checkList: ChecklistItem) {
    return checkList?.creatorEmail === this.currentUser?.email;
  }

  ngOnInit(): void {
    this.checklistInstances$ = this.pageStore.verificationChecklistInstances(this.projectId, this.reportId);
    this.checklistInstancesSorted$ = combineLatest([
      this.checklistInstances$,
      this.pageStore.getInstancesSort$,
    ]).pipe(
      map(([checklists, sort]) => [...checklists].sort(ChecklistSort.customSort(sort))),
      tap(data => this.checklistInstances = data)
    );
    this.checklistTemplates$ = this.pageStore.checklistTemplates(this.relatedType);
    this.instancesTableConfiguration = this.checklistUtils.initializeTableConfiguration(this.actionsCell, this.descriptionCell);
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
        switchMap(() => this.pageStore.deleteChecklistInstance(this.projectId, this.relatedId, checklist.id)),
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
    this.pageStore.createInstance(this.projectId, this.reportId, this.relatedId, this.selectedTemplate.id)
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
    this.pageStore.updateInstanceDescription(this.descriptionForm.value.id, this.projectId, this.reportId, this.descriptionForm.value.description)
      .pipe(
        take(1),
        tap(() => this.savingDescriptionId$.next(null)),
        tap(() => this.showAlert(ChecklistUtilsComponent.successAlert(
          'verification.checklists.description.change.message.success'
        ))),
        tap(() => this.updateChecklistInstancesAfterDescriptionSave(this.descriptionForm.value.id, this.descriptionForm.value.description)),
        catchError(error => {
          this.showAlert(ChecklistUtilsComponent.errorAlert(
            'verification.checklists.description.change.message.failure'
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

  isAfterVerificationChecklist(createdAt: Date): boolean {
    if (this.verificationReportVerificationFinalizedDate === null) {
      return true;
    }
    return createdAt > this.verificationReportVerificationFinalizedDate;
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
          const url = `/api/verificationChecklist/byProjectId/${this.projectId}/byReportId/${this.relatedId}/export/${checklistId}?exportLanguage=${systemLanguage}&pluginKey=${plugin.key}`;
          this.downloadService.download(url, 'checklist-export.pdf').subscribe();
        }
      })).subscribe();
  }
}
