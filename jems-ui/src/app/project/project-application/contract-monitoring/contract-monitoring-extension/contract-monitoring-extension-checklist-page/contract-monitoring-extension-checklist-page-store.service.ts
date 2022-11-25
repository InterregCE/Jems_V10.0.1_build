import {Injectable} from '@angular/core';
import {RoutingService} from '@common/services/routing.service';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {
    ChecklistInstanceDetailDTO,
    ChecklistInstanceDTO,
    ContractingChecklistInstanceService,
    UserRoleDTO
} from '@cat/api';
import {map, shareReplay, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ActivatedRoute} from '@angular/router';
import {SecurityService} from '../../../../../security/security.service';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Injectable()
export class ContractingChecklistPageStore {
    static CHECKLIST_DETAIL_PATH = `/contractMonitoring/checklist/`;
    projectId: number;

    checklist$: Observable<ChecklistInstanceDetailDTO>;
    checklistEditable$: Observable<boolean>;

    private updatedChecklist$ = new Subject<ChecklistInstanceDetailDTO>();

    constructor(private routingService: RoutingService,
                private checklistInstanceService: ContractingChecklistInstanceService,
                private securityService: SecurityService,
                private activatedRoute: ActivatedRoute,
                private permissionService: PermissionService) {
        this.projectId = this.activatedRoute.snapshot.params.projectId;
        this.checklist$ = this.checklist();
        this.checklistEditable$ = this.checklistEditable();
    }

    updateChecklist(projectId: number, checklist: ChecklistInstanceDetailDTO): Observable<ChecklistInstanceDetailDTO> {
        return this.checklistInstanceService.updateContractingChecklistInstance(projectId, checklist)
            .pipe(
                take(1),
                tap(() => this.updatedChecklist$.next(checklist)),
                tap(updated => Log.info('Updated contracting checklist instance', this, updated))
            );
    }

    changeStatus(projectId: number, checklistId: number, status: ChecklistInstanceDTO.StatusEnum): Observable<ChecklistInstanceDTO> {
        return this.checklistInstanceService.changeContractingChecklistStatus(checklistId, projectId, status)
            .pipe(
                take(1),
                tap(updated => Log.info('Changed contracting checklist status', this, updated))
            );
    }

    private checklist(): Observable<ChecklistInstanceDetailDTO> {
        const initialChecklist$ = combineLatest([
                this.routingService.routeParameterChanges(ContractingChecklistPageStore.CHECKLIST_DETAIL_PATH, 'checklistId')
            ]
        ).pipe(
            switchMap(([checklistId]) => {
                return this.checklistInstanceService.getContractingChecklistInstanceDetail(checklistId as number, this.projectId);
            }),
            tap(checklist => Log.info('Fetched the contracting checklist instance', this, checklist))
        );

        return merge(initialChecklist$, this.updatedChecklist$)
            .pipe(
                tap(checklist => checklist.components.sort((a, b) => a.position - b.position)),
                shareReplay()
            );
    }

    private checklistEditable(): Observable<boolean> {
        return combineLatest([
            this.checklist$,
            this.securityService.currentUserDetails,
            this.permissionService.hasPermission(PermissionsEnum.ProjectSetToContracted)])
            .pipe(
                map(([checklist, user, canEdit]) =>
                    checklist.status === ChecklistInstanceDetailDTO.StatusEnum.DRAFT &&
                    user?.email === checklist.creatorEmail &&
                    canEdit)
            );
    }
}
