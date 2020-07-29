import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {ProgrammePriorityService} from '@cat/api'
import {combineLatest, Observable, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {flatMap, map, startWith, takeUntil, tap} from 'rxjs/operators';
import {Tables} from '../../../../common/utils/tables';
import {Log} from '../../../../common/utils/log';
import {Permission} from '../../../../security/permissions/permission';
import {BaseComponent} from '@common/components/base-component';

@Component({
  selector: 'app-programme-priorities',
  templateUrl: './programme-priorities.component.html',
  styleUrls: ['./programme-priorities.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammePrioritiesComponent extends BaseComponent implements OnInit {
  Permission = Permission;

  @Input()
  refreshPage$: Observable<void>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  refresh$ = new Subject<void>();

  currentPage$ =
    combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      ),
      this.refresh$.pipe(startWith(null))
    ])
      .pipe(
        flatMap(([pageIndex, pageSize, sort]) =>
          this.programmePriorityService.get(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the priorities:', this, page.content)),
      );

  constructor(private programmePriorityService: ProgrammePriorityService) {
    super();
  }

  ngOnInit(): void {
    this.refreshPage$.pipe(
      takeUntil(this.destroyed$)
    ).subscribe(() => {
      this.refresh$.next();
    })
  }
}
