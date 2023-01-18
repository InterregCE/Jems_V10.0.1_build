import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnInit,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {
  ChecklistInstanceDTO,
  ChecklistInstanceSelectionDTO,
  IdNamePairDTO,
  ProgrammeChecklistDetailDTO,
  UserRoleCreateDTO,
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {catchError, filter, finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';
import {FormService} from '@common/components/section/form/form.service';
import {TableComponent} from '@common/components/table/table.component';
import {
  ContractingChecklistInstanceListStore
} from '@common/components/checklist/contracting-checklist-instance-list/contracting-checklist-instance-list-store.service';
import {ChecklistSort} from '@common/components/checklist/checklist-instance-list/checklist-instance-list-custom-sort';
import {FormArray, FormBuilder, Validators} from '@angular/forms';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {ChecklistUtilsComponent} from '@common/components/checklist/checklist-utils/checklist-utils';
import {AlertMessage} from '@common/components/file-list/file-list-table/alert-message';
import {Alert} from '@common/components/forms/alert';
import {ChecklistItem} from '@common/components/checklist/checklist-item';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Component({
  selector: 'jems-contracting-checklist-instance-list',
  templateUrl: './contracting-checklist-instance-list.component.html',
  styleUrls: ['./contracting-checklist-instance-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ContractingChecklistInstanceListStore, FormService]
})
export class ContractingChecklistInstanceListComponent implements OnInit {
  Status = ChecklistInstanceDTO.StatusEnum;
  projectId: number;
  isInstantiationInProgress = false;

  @Input()
  relatedType: ProgrammeChecklistDetailDTO.TypeEnum;
  @Input()
  relatedId: number;

  form = this.formBuilder.group({
    visibilities: this.formBuilder.array([])
  });

  private checklistInstances$: Observable<ChecklistInstanceDTO[]>;
  private checklistInstances: ChecklistInstanceDTO[];
  checklistInstancesSorted$: Observable<ChecklistInstanceDTO[]>;
  checklistTemplates$: Observable<IdNamePairDTO[]>;
  userCanEditContractingChecklists$: Observable<boolean>;

  instancesTableConfiguration: TableConfiguration;
  selectedTemplate: IdNamePairDTO;
  checklistUtils: ChecklistUtilsComponent;
  contractingPageStore: ContractingChecklistInstanceListStore;
  contractingChecklistAlerts$ = new BehaviorSubject<AlertMessage[]>([]);

  contractingDescriptionForm = this.formBuilder.group({
    id: [null, Validators.required],
    editable: [false],
    description: ['', Validators.maxLength(150)],
  });

  @ViewChild('actionsCell', {static: true})
  actionsCell: TemplateRef<any>;

  @ViewChild('descriptionCell', {static: true})
  descriptionCell: TemplateRef<any>;

  @ViewChild('tableInstances') tableInstances: TableComponent;
  @ViewChild('tableSelected') tableSelected: TableComponent;

  constructor(public pageStore: ContractingChecklistInstanceListStore,
              private formService: FormService,
              private formBuilder: FormBuilder,
              public routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              private dialog: MatDialog,
              private permissionService: PermissionService) {
    this.contractingPageStore = pageStore;
    this.checklistUtils = new ChecklistUtilsComponent();
    this.projectId = this.activatedRoute.snapshot.params.projectId;
    this.userCanEditContractingChecklists$ = this.permissionService.hasPermission(PermissionsEnum.ProjectSetToContracted);
  }

  ngOnInit(): void {
    this.formService.init(this.form, this.pageStore.userCanChangeSelection$);
    this.checklistInstances$ = this.pageStore.contractingChecklistInstances(this.projectId);
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

  resetForm(instances: ChecklistInstanceSelectionDTO[]): void {
    this.visibilities.clear();
    instances.filter(instance => instance.visible).forEach(instance => {
      this.visibilities.push(this.formBuilder.control(instance.id));
    });
    this.formService.resetEditable();
  }

  get visibilities(): FormArray {
    return this.form.get('visibilities') as FormArray;
  }

  delete(projectId: number, checklist: ChecklistInstanceDTO): void {
    Forms.confirm(
      this.dialog, {
        title: checklist.name,
        message: {i18nKey: 'checklists.instance.delete.confirm', i18nArguments: {name: checklist.name}}
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.pageStore.deleteChecklistInstance(this.projectId, checklist.id)),
      ).subscribe();
  }

  createInstance(): void {
    this.isInstantiationInProgress = true;
    this.pageStore.createInstance(this.projectId, this.relatedId, this.selectedTemplate.id)
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

  savingContractingDescriptionId$ = new BehaviorSubject<number | null>(null);

  saveContractingDescription() {
    this.savingContractingDescriptionId$.next(this.contractingDescriptionForm.value.id);
    this.pageStore.updateInstanceDescription(this.contractingDescriptionForm.value.id, this.projectId, this.contractingDescriptionForm.value.description)
      .pipe(
        take(1),
        tap(() => this.savingContractingDescriptionId$.next(null)),
        tap(() => this.showContractingChecklistAlert(ChecklistUtilsComponent.successAlert(
          'contracting.monitoring.checklists.description.change.message.success'
        ))),
        tap(() => this.updateChecklistInstancesAfterDescriptionSave(this.contractingDescriptionForm.value.id, this.contractingDescriptionForm.value.description)),
        catchError(error => {
          this.showContractingChecklistAlert(ChecklistUtilsComponent.errorAlert(
            'contracting.monitoring.checklists.description.change.message.failure'
          ));
          throw error;
        }),
        finalize(() => this.savingContractingDescriptionId$.next(null)),
        tap(() => this.contractingDescriptionForm.reset()),
      ).subscribe();

    this.contractingDescriptionForm.value.editable = false;
  }

  editContractingDescription(checklist: ChecklistItem) {
    this.contractingDescriptionForm.patchValue({
      id: checklist.id,
      editable: true,
      description: checklist.description,
    });
  }

  resetContractingChecklistDescription() {
    this.contractingDescriptionForm.value.editable = false;
    this.contractingDescriptionForm.reset();
  }

  private showContractingChecklistAlert(alert: AlertMessage) {
    this.contractingChecklistAlerts$.next([...this.contractingChecklistAlerts$.value, alert]);
    setTimeout(
      () => this.dismissContractingChecklistAlert(alert.id),
      alert.type === Alert.SUCCESS ? 5000 : 30000);
  }

  dismissContractingChecklistAlert(id: string) {
    const alerts = this.contractingChecklistAlerts$.value.filter(that => that.id !== id);
    this.contractingChecklistAlerts$.next(alerts);
  }

  private updateChecklistInstancesAfterDescriptionSave(checklistId: number, description: string | undefined) {
    const checklistInstancesIndex = this.checklistInstances.findIndex(c => c.id === checklistId);
    if (checklistInstancesIndex >= 0) {
      this.checklistInstances[checklistInstancesIndex].description = description ?? '';
      this.checklistInstances$ = of(this.checklistInstances);
    }
  }
}
