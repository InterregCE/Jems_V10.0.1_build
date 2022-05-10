import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {ChecklistConsolidatorOptionsDTO, ChecklistInstanceService} from '@cat/api';
import {tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable()
export class ChecklistConsolidatorOptionsStore {

  constructor(private checklistInstanceService: ChecklistInstanceService) { }

  saveOptions(checklistId: number, options: ChecklistConsolidatorOptionsDTO): Observable<any> {
    return this.checklistInstanceService.consolidateChecklistInstance(checklistId, options)
      .pipe(
        tap(saved => Log.info(`Checklist consolidator options saved`, this, checklistId, saved))
      );
  }
}
