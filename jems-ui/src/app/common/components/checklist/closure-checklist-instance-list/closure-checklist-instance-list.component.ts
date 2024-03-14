import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {ChecklistInstanceDTO, IdNamePairDTO, ProgrammeChecklistDetailDTO, UserDTO} from '@cat/api';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {SecurityService} from '../../../../security/security.service';
import {LanguageStore} from '@common/services/language-store.service';
import {DownloadService} from '@common/services/download.service';
import {ChecklistUtilsComponent} from '@common/components/checklist/checklist-utils/checklist-utils';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {catchError, filter, finalize, map, shareReplay, switchMap, take, tap} from 'rxjs/operators';
import {AlertMessage} from '@common/components/file-list/file-list-table/alert-message';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {TableComponent} from '@common/components/table/table.component';
import {MatSort} from '@angular/material/sort';
import {ReportUtil} from '@project/common/report-util';
import {ChecklistSort} from '@common/components/checklist/checklist-instance-list/checklist-instance-list-custom-sort';
import {Forms} from '@common/utils/forms';
import {Alert} from '@common/components/forms/alert';
import {ChecklistItem} from '@common/components/checklist/checklist-item';
import {
  ClosureChecklistInstanceListStore
} from '@common/components/checklist/closure-checklist-instance-list/closure-checklist-instance-list.store.service';

@Component({
  selector: 'jems-closure-checklist-instance-list',
  templateUrl: './closure-checklist-instance-list.component.html',
  styleUrls: ['./closure-checklist-instance-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ClosureChecklistInstanceListStore, FormService]
})
export class ClosureChecklistInstanceListComponent implements OnInit {
  Status = ChecklistInstanceDTO.StatusEnum;
  currentUser: UserDTO | null;
  isInstantiationInProgress = false;
  relatedType = ProgrammeChecklistDetailDTO.TypeEnum.CLOSURE;
  projectId = Number(this.routingService.getParameter(this.activatedRoute, 'projectId'));
  reportId = Number(this.routingService.getParameter(this.activatedRoute, 'reportId'));

  descriptionForm = this.formBuilder.group({
    id: [null, Validators.required],
    editable: [false],
    description: ['', Validators.maxLength(150)],
  });

  private checklistInstances$ = new BehaviorSubject<ChecklistInstanceDTO[]>([]);
  private checklistInstances: ChecklistInstanceDTO[];
  checklistInstancesSorted$: Observable<ChecklistInstanceDTO[]>;
  checklistTemplates$: Observable<IdNamePairDTO[]>;
  userCanEditClosureChecklists$: Observable<boolean>;
  alerts$ = new BehaviorSubject<AlertMessage[]>([]);

  instancesTableConfiguration: TableConfiguration;
  selectedTemplate: IdNamePairDTO;
  checklistUtils: ChecklistUtilsComponent;

  @ViewChild('actionsCell', {static: true})
  actionsCell: TemplateRef<any>;

  @ViewChild('descriptionCell', {static: true})
  descriptionCell: TemplateRef<any>;

  @ViewChild('tableInstances') tableInstances: TableComponent;

  constructor(public pageStore: ClosureChecklistInstanceListStore,
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
    this.userCanEditClosureChecklists$ = this.userCanEditClosureChecklists();
    this.securityService.currentUserDetails.subscribe(
      currentUser => this.currentUser = currentUser
    );
    this.checklistInstancesSorted$ = combineLatest([
      this.checklistInstances$,
      this.pageStore.getInstancesSort$,
    ]).pipe(
      map(([checklists, sort]) => [...checklists].sort(ChecklistSort.customSort(sort))),
      tap(data => this.checklistInstances = data)
    );
  }

  onInstancesSortChange(sort: Partial<MatSort>) {
    const order = sort.direction;
    this.pageStore.setInstancesSort({...sort, direction: order === 'desc' ? 'desc' : 'asc'});
  }

  private userCanEditClosureChecklists(): Observable<boolean> {
    return combineLatest([
      this.projectReportDetailPageStore.reportStatus$,
      this.projectReportStore.userCanEditReport$,
    ])
      .pipe(
        map(([reportStatus, canEditReport]) =>
          canEditReport && ReportUtil.isProjectReportOpen(reportStatus)
        ),
        shareReplay(1)
      );
  }

  ngOnInit(): void {
    this.pageStore.closureChecklistInstances(this.projectId, this.reportId).pipe(
      map(data => this.checklistInstances$.next(data))
    ).subscribe();
    this.checklistTemplates$ = this.pageStore.checklistTemplates(this.relatedType, this.projectId);
    this.instancesTableConfiguration = this.checklistUtils.initializeTableConfiguration(this.actionsCell, this.descriptionCell);
  }

  isEditable(): boolean {
    return this.formService.isEditable();
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
        switchMap(() => this.pageStore.deleteChecklistInstance(this.projectId, this.reportId, checklist.id)),
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
    this.pageStore.createInstance(this.projectId, this.reportId, this.selectedTemplate.id)
      .pipe(
        tap(instanceId => this.routingService.navigate(
            ['checklist', instanceId],
            {relativeTo: this.activatedRoute}
          )
        ),
        finalize(() => this.isInstantiationInProgress = false)
      ).subscribe();
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
          'checklists.instances.description.change.message.success'
        ))),
        tap(() => this.updateChecklistInstancesAfterDescriptionSave(this.descriptionForm.value.id, this.descriptionForm.value.description)),
        catchError(error => {
          this.showAlert(ChecklistUtilsComponent.errorAlert(
            'checklists.instances.description.change.message.fail'
          ));
          throw error;
        }),
        finalize(() => this.savingDescriptionId$.next(null)),
        tap(() => this.descriptionForm.reset()),
      ).subscribe();

    this.descriptionForm.value.editable = false;
  }

  resetDescription() {
    this.descriptionForm.value.editable = false;
    this.descriptionForm.reset();
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
          const url = `/api/closureReportChecklist/byProjectId/${this.projectId}/byReportId/${this.reportId}/export/${checklistId}?exportLanguage=${systemLanguage}&pluginKey=${plugin.key}`;
          this.downloadService.download(url, 'checklist-export.pdf').subscribe();
        }
      })).subscribe();
  }

  clone(checklistId: number): void {
    this.pageStore.clone(this.projectId, this.reportId, checklistId)
      .pipe(
        tap(instanceId => this.routingService.navigate(
            ['checklist', instanceId],
            {relativeTo: this.activatedRoute}
          )
        )
      ).subscribe();
  }

  private updateChecklistInstancesAfterDescriptionSave(checklistId: number, description: string | undefined) {
    const checklistInstancesIndex = this.checklistInstances.findIndex(c => c.id === checklistId);
    if (checklistInstancesIndex >= 0) {
      this.checklistInstances[checklistInstancesIndex].description = description ?? '';
      this.checklistInstances$.next(this.checklistInstances);
    }
  }

  private showAlert(alert: AlertMessage) {
    this.alerts$.next([...this.alerts$.value, alert]);
    setTimeout(
      () => this.dismissAlert(alert.id),
      alert.type === Alert.SUCCESS ? 5000 : 30000);
  }

  dismissAlert(id: string) {
    const alerts = this.alerts$.value.filter(that => that.id !== id);
    this.alerts$.next(alerts);
  }

  isChecklistDeletionDisabled(checklist: ChecklistInstanceDTO): Observable<boolean> {
    const isDraft = checklist.status === this.Status.DRAFT;
    const isCreator = checklist.creatorEmail === this.currentUser?.email;

    return this.userCanEditClosureChecklists$.pipe(
      map(userCanEditChecklists => !isDraft || !isCreator || !userCanEditChecklists)
    );
  }

}
