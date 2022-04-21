import {Injectable} from '@angular/core';
import {
  ChecklistInstanceDTO,
  ChecklistInstanceService, IdNamePairDTO,
  ProgrammeChecklistDetailDTO, ProgrammeChecklistService
} from '@cat/api';
import {Observable, Subject} from 'rxjs';
import {map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable()
export class ChecklistInstanceListStore {

  private listChanged$ = new Subject();

  constructor(private checklistInstanceService: ChecklistInstanceService,
              private programmeChecklistService: ProgrammeChecklistService) {
  }

  checklistTemplates(relatedType: ProgrammeChecklistDetailDTO.TypeEnum): Observable<IdNamePairDTO[]> {
    return this.programmeChecklistService.getProgrammeChecklistsByType(relatedType).pipe(
      tap(templates => Log.info('Fetched the programme checklist templates', this, templates))
    );
  }

  checklistInstances(relatedType: ProgrammeChecklistDetailDTO.TypeEnum, relatedId: number): Observable<ChecklistInstanceDTO[]> {
    return this.listChanged$
      .pipe(
        startWith(null),
        switchMap(() => this.checklistInstanceService.getChecklistInstances(relatedId, relatedType as string)),
        tap(checklists => Log.info('Fetched the checklist instances', this, checklists))
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
}
