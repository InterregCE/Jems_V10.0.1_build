import {Injectable} from '@angular/core';
import {
  ChecklistInstanceDTO,
  ChecklistInstanceSelectionDTO,
  ChecklistInstanceService,
  ContractingChecklistInstanceService,
  IdNamePairDTO,
  PluginInfoDTO,
  PluginService,
  ProgrammeChecklistDetailDTO,
  ProgrammeChecklistService,
  UserRoleDTO
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {SecurityService} from '../../../../security/security.service';
import {MatSort} from '@angular/material/sort';

@Injectable()
export class ContractingChecklistInstanceListStore {

  defaultSort: Partial<MatSort> = {active: 'id', direction: 'desc'};

  currentUserEmail$: Observable<string>;
  userCanChangeSelection$: Observable<boolean>;
  userCanClone$: Observable<boolean>;
  availablePlugins$: Observable<PluginInfoDTO[]>;

  private listChanged$ = new Subject();

  private instancesSort$ = new BehaviorSubject<Partial<MatSort>>(this.defaultSort);
  getInstancesSort$ = this.instancesSort$.pipe(
    map(sort => sort?.direction ? sort : this.defaultSort),
  );

  private selectedSort$ = new BehaviorSubject<Partial<MatSort>>(this.defaultSort);
  getSelectedSort$ = this.selectedSort$.pipe(
    map(sort => sort?.direction ? sort : this.defaultSort),
  );

  constructor(private contractingChecklistInstanceService: ContractingChecklistInstanceService,
              private checklistInstanceService: ChecklistInstanceService,
              private programmeChecklistService: ProgrammeChecklistService,
              private permissionService: PermissionService,
              private securityService: SecurityService,
              private pluginService: PluginService) {
    this.currentUserEmail$ = this.currentUserEmail();
    this.userCanChangeSelection$ = this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.ProjectSetToContracted);
    this.userCanClone$ = this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.ProjectSetToContracted);
    this.availablePlugins$ = this.availablePlugins();
  }

  selectedInstances(relatedType: ProgrammeChecklistDetailDTO.TypeEnum, relatedId: number): Observable<ChecklistInstanceSelectionDTO[]> {
    return this.checklistInstanceService.getChecklistInstancesForSelection(relatedId, relatedType as string)
      .pipe(
        tap(checklists => Log.info('Fetched the checklist selection instances', this, checklists))
      );
  }

  setInstancesSort(sort: Partial<MatSort>) {
    this.instancesSort$.next(sort);
  }

  checklistTemplates(relatedType: ProgrammeChecklistDetailDTO.TypeEnum, projectId?: number): Observable<IdNamePairDTO[]> {
    return this.programmeChecklistService.getProgrammeChecklistsByType(relatedType, projectId).pipe(
      map(templates => [...templates].sort((a, b) => b.id - a.id)),
      tap(templates => Log.info('Fetched the programme checklist templates', this, templates))
    );
  }

  contractingChecklistInstances(projectId: number): Observable<ChecklistInstanceDTO[]> {
    return combineLatest([
      this.listChanged$.pipe(startWith(null)),
    ]).pipe(
      switchMap(() => this.contractingChecklistInstanceService.getAllContractingChecklistInstances(projectId)),
      tap(checklists => Log.info('Fetched the contracting checklist instances', this, checklists))
    );
  }

  deleteChecklistInstance(projectId: number, id: number): Observable<void> {
    return this.contractingChecklistInstanceService.deleteContractingChecklistInstance(id, projectId)
      .pipe(
        take(1),
        tap(() => this.listChanged$.next()),
        tap(() => Log.info(`Contracting checklist instance with id ${id} deleted`))
      );
  }

  createInstance(projectId: number, relatedToId: number, programmeChecklistId: number): Observable<number> {
    return this.contractingChecklistInstanceService.createContractingChecklistInstance(projectId, {relatedToId, programmeChecklistId})
      .pipe(
        take(1),
        tap(checklistInstance => Log.info('Created a new contracting checklist instance', this, checklistInstance)),
        map(checklistInstance => checklistInstance.id)
      );
  }

  private currentUserEmail(): Observable<string> {
    return this.securityService.currentUser
      .pipe(
        map(user => user?.name || '')
      );
  }

  private availablePlugins(): Observable<PluginInfoDTO[]> {
    return this.pluginService.getAvailablePluginList(PluginInfoDTO.TypeEnum.CHECKLISTEXPORT);
  }

  updateInstanceDescription(checklistId: number,projectId: number, description: string): Observable<number> {
    return this.contractingChecklistInstanceService.updateContractingChecklistDescription(checklistId, projectId, description)
      .pipe(
        take(1),
        tap(checklistInstance => Log.info('Updated contracting checklist instance description', this, checklistInstance)),
        map(checklistInstance => checklistInstance.id)
      );
  }

  clone(projectId: number, checklistId: number): Observable<number> {
    return this.contractingChecklistInstanceService.cloneContractingChecklistInstance(checklistId, projectId)
      .pipe(
        take(1),
        tap(clonedInstance => Log.info('Cloned contracting checklist instance', this, clonedInstance, checklistId)),
        map(clonedInstance => clonedInstance.id)
      );
  }
}
