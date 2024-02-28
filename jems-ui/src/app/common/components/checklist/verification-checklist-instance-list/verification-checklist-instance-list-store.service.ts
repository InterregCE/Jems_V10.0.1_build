import {Injectable} from '@angular/core';
import {
  ChecklistInstanceDTO,
  IdNamePairDTO,
  PluginInfoDTO,
  PluginService,
  ProgrammeChecklistDetailDTO,
  ProgrammeChecklistService,
  VerificationChecklistInstanceService
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {SecurityService} from '../../../../security/security.service';
import {MatSort} from '@angular/material/sort';

@Injectable()
export class VerificationChecklistInstanceListStore {

  defaultSort: Partial<MatSort> = {active: 'id', direction: 'desc'};

  currentUserEmail$: Observable<string>;
  availablePlugins$: Observable<PluginInfoDTO[]>;

  private listChanged$ = new Subject();

  private instancesSort$ = new BehaviorSubject<Partial<MatSort>>(this.defaultSort);
  getInstancesSort$ = this.instancesSort$.pipe(
    map(sort => sort?.direction ? sort : this.defaultSort),
  );

  constructor(private verificationChecklistInstanceService: VerificationChecklistInstanceService,
              private programmeChecklistService: ProgrammeChecklistService,
              private permissionService: PermissionService,
              private securityService: SecurityService,
              private pluginService: PluginService) {
    this.currentUserEmail$ = this.currentUserEmail();
    this.availablePlugins$ = this.availablePlugins();
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

  verificationChecklistInstances(projectId: number, reportId: number): Observable<ChecklistInstanceDTO[]> {
    return combineLatest([
      this.listChanged$.pipe(startWith(null)),
    ]).pipe(
      switchMap(() => this.verificationChecklistInstanceService.getAllVerificationChecklistInstances(projectId, reportId)),
      tap(checklists => Log.info('Fetched the verification checklist instances', this, checklists))
    );
  }

  deleteChecklistInstance(projectId: number, reportId: number, id: number): Observable<void> {
    return this.verificationChecklistInstanceService.deleteVerificationChecklistInstance(id, projectId, reportId)
      .pipe(
        take(1),
        tap(() => this.listChanged$.next()),
        tap(() => Log.info(`Verification checklist instance with id ${id} deleted`))
      );
  }


  createInstance(projectId: number, reportId: number, relatedToId: number, programmeChecklistId: number): Observable<number> {
    return this.verificationChecklistInstanceService.createVerificationChecklistInstance(projectId, reportId, {relatedToId, programmeChecklistId})
      .pipe(
        take(1),
        tap(checklistInstance => Log.info('Created a new verification checklist instance', this, checklistInstance)),
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

  updateInstanceDescription(checklistId: number, projectId: number, reportId: number, description: string): Observable<number> {
    return this.verificationChecklistInstanceService.updateVerificationChecklistDescription(checklistId, projectId, reportId, description)
      .pipe(
        take(1),
        tap(checklistInstance => Log.info('Updated verification checklist instance description', this, checklistInstance)),
        map(checklistInstance => checklistInstance.id)
      );
  }

  clone(projectId: number, reportId: number, checklistId: number): Observable<number> {
    return this.verificationChecklistInstanceService.cloneVerificationChecklistInstance(checklistId, projectId, reportId)
     .pipe(
       take(1),
       tap(clonedInstance => Log.info('Created a new verification checklist instance', this, clonedInstance, checklistId)),
       map(clonedInstance => clonedInstance.id)
     );
  }
}
