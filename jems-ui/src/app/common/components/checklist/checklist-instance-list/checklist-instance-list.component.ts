import {ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {ChecklistInstanceDTO, ChecklistInstanceSelectionDTO, IdNamePairDTO, ProgrammeChecklistDetailDTO, UserRoleDTO} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {ChecklistInstanceListStore} from '@common/components/checklist/checklist-instance-list/checklist-instance-list-store.service';
import {catchError, filter, finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';
import {FormService} from '@common/components/section/form/form.service';
import {FormArray, FormBuilder, Validators} from '@angular/forms';
import {TableComponent} from '@common/components/table/table.component';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {ChecklistSort} from '@common/components/checklist/checklist-instance-list/checklist-instance-list-custom-sort';
import {ChecklistUtilsComponent} from '@common/components/checklist/checklist-utils/checklist-utils';
import {Alert} from '@common/components/forms/alert';
import {AlertMessage} from '@common/components/file-list/file-list-table/alert-message';
import {v4 as uuid} from 'uuid';
import {DownloadService} from '@common/services/download.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {LanguageStore} from '@common/services/language-store.service';

@Component({
  selector: 'jems-checklist-instance-list',
  templateUrl: './checklist-instance-list.component.html',
  styleUrls: ['./checklist-instance-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ChecklistInstanceListStore, FormService]
})
export class ChecklistInstanceListComponent implements OnInit {
  Alert = Alert;
  Status = ChecklistInstanceDTO.StatusEnum;
  PermissionEnum = UserRoleDTO.PermissionsEnum;
  alerts$ = new BehaviorSubject<AlertMessage[]>([]);

  @Input()
  relatedType: ProgrammeChecklistDetailDTO.TypeEnum;
  @Input()
  relatedId: number;

  form = this.formBuilder.group({
    visibilities: this.formBuilder.array([])
  });

  private checklistInstances$: Observable<ChecklistInstanceDTO[]>;
  checklistInstancesSorted$: Observable<ChecklistInstanceDTO[]>;
  private checklistInstances: ChecklistInstanceDTO[];
  checklistTemplates$: Observable<IdNamePairDTO[]>;
  private selectedChecklists$: Observable<ChecklistInstanceSelectionDTO[]>;
  private selectedChecklists: ChecklistInstanceSelectionDTO[];
  selectedChecklistsSorted$: Observable<ChecklistInstanceSelectionDTO[]>;
  isInstantiationInProgress = false;

  instancesTableConfiguration: TableConfiguration;
  selectionTableConfiguration: TableConfiguration;
  selectedTemplate: IdNamePairDTO;
  checklistUtils: ChecklistUtilsComponent;
  checklistPageStore: ChecklistInstanceListStore;
  editableChecklistId: number | null;

  @ViewChild('consolidateCell', {static: true})
  consolidateCell: TemplateRef<any>;

  @ViewChild('visibleCell', {static: true})
  visibleCell: TemplateRef<any>;

  @ViewChild('actionsCell', {static: true})
  actionsCell: TemplateRef<any>;

  @ViewChild('descriptionCell', {static: true})
  descriptionCell: TemplateRef<any>;

  @ViewChild('tableInstances') tableInstances: TableComponent;
  @ViewChild('tableSelected') tableSelected: TableComponent;

  descriptionForm = this.formBuilder.group({
    id: [null, Validators.required],
    description: ['', Validators.maxLength(150)],
  });

  constructor(public pageStore: ChecklistInstanceListStore,
              private projectStore: ProjectStore,
              private formService: FormService,
              private formBuilder: FormBuilder,
              private routingService: RoutingService,
              private downloadService: DownloadService,
              private activatedRoute: ActivatedRoute,
              private languageStore: LanguageStore,
              private dialog: MatDialog) {
    this.checklistPageStore = pageStore;
    this.checklistUtils = new ChecklistUtilsComponent();
  }

  ngOnInit(): void {
    this.formService.init(this.form, this.pageStore.userCanChangeSelection$);
    this.checklistInstances$ = this.pageStore.checklistInstances(this.relatedType, this.relatedId);
    this.checklistInstancesSorted$ = combineLatest([
      this.checklistInstances$,
      this.pageStore.getInstancesSort$,
    ]).pipe(
      map(([checklists, sort]) => [...checklists].sort(ChecklistSort.customSort(sort))),
      tap(data => this.checklistInstances = data)
    );
    this.checklistTemplates$ = this.pageStore.checklistTemplates(this.relatedType);
    this.selectedChecklists$ = this.pageStore.selectedInstances(this.relatedType, this.relatedId)
      .pipe(
        tap(checklists => this.resetForm(checklists)),
      );
    this.selectedChecklistsSorted$ = combineLatest([
      this.selectedChecklists$,
      this.pageStore.getSelectedSort$,
    ]).pipe(
      map(([checklists, sort]) => [...checklists].sort(ChecklistSort.customSort(sort))),
      tap(data => this.selectedChecklists = data)
    );

    this.instancesTableConfiguration = this.initializeTableConfiguration(false);
    this.selectionTableConfiguration = this.initializeTableConfiguration(true);
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
        switchMap(() => this.pageStore.deleteChecklistInstance(checklist.id)),
        tap(() => {
          this.checklistInstances = this.checklistInstances.filter(c => c.id !== checklist.id);
          this.selectedChecklists = this.selectedChecklists.filter(c => c.id !== checklist.id);
        })
      ).subscribe();
  }

  setDescriptionEditable(checklist: ChecklistInstanceDTO): void {
    this.editableChecklistId = checklist.id;
    this.descriptionForm.patchValue({
      id: checklist.id,
      description: checklist.description,
    });
  }

  private initializeTableConfiguration(selection: boolean): TableConfiguration {
    return new TableConfiguration({
      isTableClickable: true,
      sortable: false,
      routerLink: 'checklist',
      columns: [
        {
          displayedColumn: 'common.id',
          elementProperty: 'id',
          columnWidth: ColumnWidth.IdColumn,
          sortProperty: 'id',
        },
        {
          displayedColumn: 'checklists.instance.consolidated',
          customCellTemplate: this.consolidateCell,
          sortProperty: 'consolidated',
          columnWidth: ColumnWidth.SmallColumn,
        },
        {
          displayedColumn: 'common.status',
          elementTranslationKey: 'checklists.instance.status',
          elementProperty: 'status',
          columnWidth: ColumnWidth.SmallColumn,
          sortProperty: 'status',
        },
        {
          displayedColumn: 'common.name',
          elementProperty: 'name',
          columnWidth: ColumnWidth.WideColumn,
          sortProperty: 'name',
        },
        ...!selection ? [{
          displayedColumn: 'checklists.instance.assessor',
          elementProperty: 'creatorEmail',
          columnWidth: ColumnWidth.WideColumn,
          sortProperty: 'creatorEmail',
        }] : [],
        {
          displayedColumn: 'checklists.instance.finished.date',
          elementProperty: 'finishedDate',
          columnType: ColumnType.DateOnlyColumn,
          columnWidth: ColumnWidth.DateColumn,
          sortProperty: 'finishedDate',
        },
        {
          displayedColumn: 'common.description',
          elementProperty: 'description',
          columnWidth: ColumnWidth.extraWideColumn,
          sortProperty: 'description',
          customCellTemplate: this.descriptionCell,
        },
        ...selection ? [{
          displayedColumn: 'checklists.instance.visible',
          customCellTemplate: this.visibleCell,
          columnWidth: ColumnWidth.DateColumn,
          infoMessage: 'checklists.instance.visible.tooltip',
          clickable: false
        }
        ] : [{
          displayedColumn: 'common.action',
          customCellTemplate: this.actionsCell,
          columnWidth: ColumnWidth.SmallColumn,
          clickable: false
        }]
      ]
    });
  }

  createInstance(): void {
    this.isInstantiationInProgress = true;
    this.pageStore.createInstance(this.relatedType, this.relatedId, this.selectedTemplate.id)
      .pipe(
        tap(instanceId => this.routingService.navigate(
            ['checklist', instanceId],
            {relativeTo: this.activatedRoute}
          )
        ),
        finalize(() => this.isInstantiationInProgress = false)
      ).subscribe();
  }

  save(original: ChecklistInstanceSelectionDTO[]): void {
    const allIds = original.map(ch => ch.id);
    const visibilities = allIds.reduce((resultObject: { [index: number]: any }, id) => {
      resultObject[id] = this.visibilities.value.includes(id);
      return resultObject;
    }, {});
    this.pageStore.setVisibilities(visibilities)
      .pipe(
        tap(() => this.formService.setSuccess('checklists.instances.list.saved.successfully'))
      ).subscribe();
  }

  resetForm(instances: ChecklistInstanceSelectionDTO[]): void {
    this.visibilities.clear();
    instances.filter(instance => instance.visible).forEach(instance => {
      this.visibilities.push(this.formBuilder.control(instance.id));
    });
    this.formService.resetEditable();
  }

  onVisibilityChange(change: MatCheckboxChange, id: number) {
    if (change.checked) {
      this.visibilities.push(this.formBuilder.control(id));
      this.formService.setDirty(true);
    } else {
      const index = this.visibilities.value.indexOf(id);
      if (index > -1) {
        this.visibilities.removeAt(index);
        this.formService.setDirty(true);
      }
    }
  }

  get visibilities(): FormArray {
    return this.form.get('visibilities') as FormArray;
  }

  isEditable(): boolean {
    return this.formService.isEditable();
  }

  resetDescription() {
    this.editableChecklistId = null;
    this.descriptionForm.reset();
  }

  savingDescriptionId$ = new BehaviorSubject<number | null>(null);

  saveDescription() {
    this.savingDescriptionId$.next(this.descriptionForm.value.id);
    this.pageStore.setDescription(this.descriptionForm.value.id, this.descriptionForm.value.description)
      .pipe(
        take(1),
        tap(() => this.showDescriptionUpdateAlert({
          id: uuid(),
          type: Alert.SUCCESS,
          i18nMessage: 'checklists.instances.description.change.message.success',
        } as AlertMessage)),
        tap(() => this.updateChecklistInstancesAfterSave(this.descriptionForm.value.id, this.descriptionForm.value.description)),
        catchError(error => {
          this.showDescriptionUpdateAlert({
            id: uuid(),
            type: Alert.ERROR,
            i18nMessage: 'checklists.instances.description.change.message.fail',
          } as AlertMessage);
          throw error;
        }),
        finalize(() => this.savingDescriptionId$.next(null)),
        tap(() => this.descriptionForm.reset()),
      ).subscribe();
  }

  dismissDescriptionUpdateAlert(id: string) {
    const alerts = this.alerts$.value.filter(that => that.id !== id);
    this.alerts$.next(alerts);
  }

  private showDescriptionUpdateAlert(alert: AlertMessage) {
    this.alerts$.next([...this.alerts$.value, alert]);
    setTimeout(
      () => this.dismissDescriptionUpdateAlert(alert.id),
      alert.type === Alert.SUCCESS ? 5000 : 30000);
  }

  private updateChecklistInstancesAfterSave(checklistId: number, description: string | undefined) {
    this.editableChecklistId = null;
    const checklistInstancesIndex = this.checklistInstances.findIndex(c => c.id === checklistId);
    if (checklistInstancesIndex >= 0) {
      this.checklistInstances[checklistInstancesIndex].description = description ?? '';
      this.checklistInstances$ = of(this.checklistInstances);
    }
    const selectedChecklistIndex = this.selectedChecklists.findIndex(c => c.id === checklistId);
    if (selectedChecklistIndex >= 0) {
      this.selectedChecklists[selectedChecklistIndex].description = description ?? '';
      this.selectedChecklists$ = of(this.selectedChecklists);
    }
  }

  download(checklistId: number) {
    combineLatest([
      this.pageStore.availablePlugins$,
      this.projectStore.projectId$,
      this.languageStore.currentSystemLanguage$,
    ]).pipe(
      take(1),
      map(([plugins, projectId, systemLanguage]) => {
        const plugin = plugins[0];
        if (plugin?.type) {
          const url = `/api/checklist/instance/export/${projectId}/${checklistId}?exportLanguage=${systemLanguage}&pluginKey=${plugin.key}`;
          this.downloadService.download(url, 'checklist-export.pdf').pipe().subscribe();
        }
      })).subscribe();
  }
}
