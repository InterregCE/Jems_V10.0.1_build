import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Permission} from '../../../../../security/permissions/permission';
import {ProgrammeChecklistDetailDTO, ProjectDetailDTO, ProjectStatusDTO, UserRoleDTO} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {TranslateService} from '@ngx-translate/core';
import {map} from 'rxjs/operators';
import {Alert} from '@common/components/forms/alert';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {FormService} from "@common/components/section/form/form.service";

@Component({
    selector: 'jems-control-checklist-instance-list',
    templateUrl: './partner-control-report-control-checklists-tab.component.html',
    styleUrls: ['./partner-control-report-control-checklists-tab.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [FormService]
})
export class ControlChecklistInstanceListComponent {

    Alert = Alert;
    Permission = Permission;
    PermissionsEnum = UserRoleDTO.PermissionsEnum;
    ChecklistType = ProgrammeChecklistDetailDTO.TypeEnum;

    @Input()
    relatedType: ProgrammeChecklistDetailDTO.TypeEnum;
    @Input()
    relatedId: number;

    data$: Observable<{
        currentVersionOfProject: ProjectDetailDTO;
        currentVersionOfProjectTitle: string;
        currentVersionOfProjectStatus: ProjectStatusDTO.StatusEnum;
        projectId: number;
    }>;

    // TODO: create a component
    error$ = new BehaviorSubject<APIError | null>(null);
    actionPending = false;

    constructor(public translate: TranslateService,
                private projectStore: ProjectStore) {
        this.data$ = combineLatest([
            this.projectStore.currentVersionOfProject$,
            this.projectStore.currentVersionOfProjectTitle$
        ]).pipe(
            map(([currentVersionOfProject, currentVersionOfProjectTitle]) => ({
                currentVersionOfProject,
                currentVersionOfProjectTitle,
                currentVersionOfProjectStatus: currentVersionOfProject.projectStatus.status,
                projectId: currentVersionOfProject.id,
            }))
        );
    }
}