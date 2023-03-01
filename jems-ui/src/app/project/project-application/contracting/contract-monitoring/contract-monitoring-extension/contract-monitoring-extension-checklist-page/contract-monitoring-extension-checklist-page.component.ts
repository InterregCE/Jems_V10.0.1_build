import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
    ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ChecklistComponentInstanceDTO, ChecklistInstanceDetailDTO, UserRoleCreateDTO} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {map, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {combineLatest, Observable} from 'rxjs';
import {
    ContractingChecklistPageStore
} from '@project/project-application/contracting/contract-monitoring/contract-monitoring-extension/contract-monitoring-extension-checklist-page/contract-monitoring-extension-checklist-page-store.service';
import {PermissionService} from '../../../../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Component({
    selector: 'jems-contracting-checklist-page',
    templateUrl: './contract-monitoring-extension-checklist-page.component.html',
    styleUrls: ['./contract-monitoring-extension-checklist-page.component.scss'],
    providers: [ContractingChecklistPageStore, FormService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContractingChecklistPageComponent {
    Status = ChecklistInstanceDetailDTO.StatusEnum;

    userCanEditContractingChecklists$: Observable<boolean>;

    data$: Observable<{
        checklist: ChecklistInstanceDetailDTO;
        editable: boolean;
        projectId: number;
    }>;

    confirmFinish = {
        title: 'checklists.instance.confirm.finish.title',
        message: 'checklists.instance.confirm.finish.message'
    };

    confirmReturnToInitiator = {
      title: 'checklists.instance.confirm.return.to.initiator.title',
      message: 'checklists.instance.confirm.return.to.initiator'
    };

    constructor(private projectSidenavService: ProjectApplicationFormSidenavService,
                private pageStore: ContractingChecklistPageStore,
                private formService: FormService,
                private routingService: RoutingService,
                private activatedRoute: ActivatedRoute,
                private permissionService: PermissionService) {
        this.data$ = combineLatest([
            this.pageStore.checklist$,
            this.pageStore.checklistEditable$
        ]).pipe(
          map(([checklist, editable]) => ({
            checklist,
            editable,
            projectId: this.pageStore.projectId
          })),
        );
        this.userCanEditContractingChecklists$ = this.permissionService.hasPermission(PermissionsEnum.ProjectSetToContracted);
    }

    save(projectId: number, checklist: ChecklistInstanceDetailDTO): void {
        checklist.components = this.getFormComponents();
        this.pageStore.updateChecklist(projectId, checklist)
            .pipe(
                tap(() => this.formService.setSuccess('checklists.instance.saved.successfully'))
            ).subscribe();
    }

    updateStatus(projectId: number, checklistId: number, status: ChecklistInstanceDetailDTO.StatusEnum) {
        this.pageStore.changeStatus(projectId, checklistId, status)
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
}
