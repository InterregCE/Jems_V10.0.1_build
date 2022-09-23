import {Injectable} from '@angular/core';
import {
  ChecklistInstanceDTO,
  ControlChecklistInstanceService,
  IdNamePairDTO,
  ProgrammeChecklistDetailDTO,
  ProgrammeChecklistService
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {SecurityService} from '../../../../security/security.service';
import {MatSort} from '@angular/material/sort';

@Injectable()
export class ControlChecklistInstanceListStore {

  defaultSort: Partial<MatSort> = {active: 'id', direction: 'desc'};

  currentUserEmail$: Observable<string>;

  private listChanged$ = new Subject();

  private instancesSort$ = new BehaviorSubject<Partial<MatSort>>(this.defaultSort);
  getInstancesSort$ = this.instancesSort$.pipe(
    map(sort => sort?.direction ? sort : this.defaultSort),
  );

  constructor(private controlChecklistInstanceService: ControlChecklistInstanceService,
              private programmeChecklistService: ProgrammeChecklistService,
              private permissionService: PermissionService,
              private securityService: SecurityService) {
    this.currentUserEmail$ = this.currentUserEmail();
  }

  setInstancesSort(sort: Partial<MatSort>) {
    this.instancesSort$.next(sort);
  }

  checklistTemplates(relatedType: ProgrammeChecklistDetailDTO.TypeEnum): Observable<IdNamePairDTO[]> {
    return this.programmeChecklistService.getProgrammeChecklistsByType(relatedType).pipe(
      map(templates => [...templates].sort((a, b) => b.id - a.id)),
      tap(templates => Log.info('Fetched the programme checklist templates', this, templates))
    );
  }

  controlChecklistInstances(partnerId: number, reportId: number): Observable<ChecklistInstanceDTO[]> {
    return combineLatest([
      this.listChanged$.pipe(startWith(null)),
    ]).pipe(
      switchMap(() => this.controlChecklistInstanceService.getAllControlChecklistInstances(partnerId, reportId)),
      tap(checklists => Log.info('Fetched the control checklist instances', this, checklists))
    );
  }

  deleteChecklistInstance(partnerId: number, reportId: number, id: number): Observable<void> {
    return this.controlChecklistInstanceService.deleteControlChecklistInstance(id, partnerId, reportId)
      .pipe(
        take(1),
        tap(() => this.listChanged$.next()),
        tap(() => Log.info(`Control checklist instance with id ${id} deleted`))
      );
  }


  createInstance(partnerId: number, reportId: number, relatedToId: number, programmeChecklistId: number): Observable<number> {
    return this.controlChecklistInstanceService.createControlChecklistInstance(partnerId, reportId, {relatedToId, programmeChecklistId})
      .pipe(
        take(1),
        tap(checklistInstance => Log.info('Created a new control checklist instance', this, checklistInstance)),
        map(checklistInstance => checklistInstance.id)
      );
  }

  currentUserEmail(): Observable<string> {
    return this.securityService.currentUser
      .pipe(
        map(user => user?.name || '')
      );
  }
}
