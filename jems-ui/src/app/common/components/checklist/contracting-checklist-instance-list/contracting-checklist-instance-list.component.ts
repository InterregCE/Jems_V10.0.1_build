import {ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {
    ChecklistInstanceDTO,
    ChecklistInstanceSelectionDTO,
    IdNamePairDTO,
    ProgrammeChecklistDetailDTO,
    UserRoleCreateDTO,
} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {filter, map, switchMap, take, tap} from 'rxjs/operators';
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
import {FormArray, FormBuilder} from '@angular/forms';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {ChecklistUtilsComponent} from '@common/components/checklist/checklist-utils/checklist-utils';
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

    @Input()
    relatedType: ProgrammeChecklistDetailDTO.TypeEnum;
    @Input()
    relatedId: number;

    form = this.formBuilder.group({
        visibilities: this.formBuilder.array([])
    });

    private checklistInstances$: Observable<ChecklistInstanceDTO[]>;
    checklistInstancesSorted$: Observable<ChecklistInstanceDTO[]>;
    checklistTemplates$: Observable<IdNamePairDTO[]>;
    private selectedChecklists$: Observable<ChecklistInstanceSelectionDTO[]>;
    selectedChecklistsSorted$: Observable<ChecklistInstanceSelectionDTO[]>;
    userCanEditContractingChecklists$: Observable<boolean>;

    instancesTableConfiguration: TableConfiguration;
    selectionTableConfiguration: TableConfiguration;
    selectedTemplate: IdNamePairDTO;
    checklistUtils: ChecklistUtilsComponent;
    contractingPageStore: ContractingChecklistInstanceListStore;

    @ViewChild('visibleCell', {static: true})
    visibleCell: TemplateRef<any>;

    @ViewChild('deleteCell', {static: true})
    deleteCell: TemplateRef<any>;

    @ViewChild('tableInstances') tableInstances: TableComponent;
    @ViewChild('tableSelected') tableSelected: TableComponent;

    constructor(public pageStore: ContractingChecklistInstanceListStore,
                private formService: FormService,
                private formBuilder: FormBuilder,
                private routingService: RoutingService,
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
        );
        this.selectedChecklists$ = this.pageStore.selectedInstances(this.relatedType, this.relatedId)
            .pipe(
                tap(checklists => this.resetForm(checklists)),
                );
        this.selectedChecklistsSorted$ = combineLatest([
            this.selectedChecklists$,
            this.pageStore.getSelectedSort$,
        ]).pipe(
            map(([checklists, sort]) => [...checklists].sort(ChecklistSort.customSort(sort))),
        );

        this.checklistTemplates$ = this.pageStore.checklistTemplates(this.relatedType);
        this.instancesTableConfiguration = this.checklistUtils.initializeTableConfiguration(this.deleteCell);
        this.selectionTableConfiguration = this.checklistUtils.initializeTableConfiguration(this.deleteCell);
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
        this.pageStore.createInstance(this.projectId, this.relatedId, this.selectedTemplate.id)
            .pipe(
                tap(instanceId => this.routingService.navigate(
                        ['checklist', instanceId],
                        {relativeTo: this.activatedRoute}
                    )
                )
            ).subscribe();
    }

    isEditable(): boolean {
        return this.formService.isEditable();
    }
}
