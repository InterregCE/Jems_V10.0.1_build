import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProgrammePriorityService} from '@cat/api'
import {combineLatest, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {flatMap, map, startWith, tap} from 'rxjs/operators';
import {Tables} from '../../../../common/utils/tables';
import {Log} from '../../../../common/utils/log';
import {Permission} from '../../../../security/permissions/permission';
import {BaseComponent} from '@common/components/base-component';
import {ProgrammeNavigationStateManagementService} from '../../services/programme-navigation-state-management.service';

@Component({
  selector: 'app-programme-priorities',
  templateUrl: './programme-priorities.component.html',
  styleUrls: ['./programme-priorities.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammePrioritiesComponent extends BaseComponent {
  Permission = Permission;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  currentPage$ =
    combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith({active: 'code', direction: 'asc'}),
        map(sort => sort?.direction ? sort : {active: 'code', direction: 'asc'}),
        map(sort => `${sort.active},${sort.direction}`)
      ),
      this.programmeNavigationStateManagementService.getTab()
        .pipe(startWith(0)),
    ])
      .pipe(
        flatMap(([pageIndex, pageSize, sort]) =>
          this.programmePriorityService.get(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the priorities:', this, page.content)),
      );

  constructor(private programmePriorityService: ProgrammePriorityService,
              private programmeNavigationStateManagementService: ProgrammeNavigationStateManagementService) {
    super();
  }
}
