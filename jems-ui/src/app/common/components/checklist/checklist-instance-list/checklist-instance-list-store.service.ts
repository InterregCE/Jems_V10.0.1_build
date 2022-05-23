import {Injectable} from '@angular/core';
import {
  ChecklistInstanceDTO, ChecklistInstanceSelectionDTO,
  ChecklistInstanceService, IdNamePairDTO,
  ProgrammeChecklistDetailDTO, ProgrammeChecklistService, UserRoleDTO
} from '@cat/api';
import {combineLatest, Observable, of, Subject} from 'rxjs';
import {map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {SecurityService} from '../../../../security/security.service';

@Injectable()
export class ChecklistInstanceListStore {

  currentUserEmail$: Observable<string>;
  userCanChangeSelection$: Observable<boolean>;

  private userCanConsolidate$: Observable<boolean>;
  private listChanged$ = new Subject();

  constructor(private checklistInstanceService: ChecklistInstanceService,
              private programmeChecklistService: ProgrammeChecklistService,
              private permissionService: PermissionService,
              private securityService: SecurityService) {
    this.userCanConsolidate$ = this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.ProjectAssessmentChecklistConsolidate);
    this.currentUserEmail$ = this.currentUserEmail();
    this.userCanChangeSelection$ = this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.ProjectAssessmentChecklistSelectedUpdate);
  }

  checklistTemplates(relatedType: ProgrammeChecklistDetailDTO.TypeEnum): Observable<IdNamePairDTO[]> {
    return this.programmeChecklistService.getProgrammeChecklistsByType(relatedType).pipe(
      tap(templates => Log.info('Fetched the programme checklist templates', this, templates))
    );
  }

  checklistInstances(relatedType: ProgrammeChecklistDetailDTO.TypeEnum, relatedId: number): Observable<ChecklistInstanceDTO[]> {
    return combineLatest([
      this.userCanConsolidate$,
      this.listChanged$.pipe(startWith(null)),
    ]).pipe(
      switchMap(([canConsolidate]) => canConsolidate
        ? this.checklistInstanceService.getAllChecklistInstances(relatedId, relatedType as string)
        : this.checklistInstanceService.getMyChecklistInstances(relatedId, relatedType as string)),
      tap(checklists => Log.info('Fetched the checklist instances', this, checklists))
    );
  }

  selectedInstances(relatedType: ProgrammeChecklistDetailDTO.TypeEnum, relatedId: number): Observable<ChecklistInstanceSelectionDTO[]> {
    return this.checklistInstanceService.getChecklistInstancesForSelection(relatedId, relatedType as string)
      .pipe(
        tap(checklists => Log.info('Fetched the checklist selection instances', this, checklists))
      );
  }

  deleteChecklistInstance(id: number): Observable<void> {
    return this.checklistInstanceService.deleteChecklistInstance(id)
      .pipe(
        take(1),
        tap(() => this.listChanged$.next()),
        tap(() => Log.info(`Checklist instance with id ${id} deleted`))
      );
  }


  createInstance(relatedType: ProgrammeChecklistDetailDTO.TypeEnum, relatedToId: number, programmeChecklistId: number): Observable<number> {
    return this.checklistInstanceService.createChecklistInstance({relatedToId, programmeChecklistId})
      .pipe(
        take(1),
        tap(checklistInstance => Log.info('Created a new checklist instance', this, checklistInstance)),
        map(checklistInstance => checklistInstance.id)
      );
  }

  setVisibilities(instances: { [key: string]: boolean }): Observable<ChecklistInstanceDTO[]> {
    return this.checklistInstanceService.updateChecklistInstanceSelection(instances)
      .pipe(
        take(1),
        tap(() => Log.info('Updated the checklist instance selection', this, instances))
      );
  }

  currentUserEmail(): Observable<string> {
    return this.securityService.currentUser
      .pipe(
        map(user => user?.name || '')
      );
  }
}
