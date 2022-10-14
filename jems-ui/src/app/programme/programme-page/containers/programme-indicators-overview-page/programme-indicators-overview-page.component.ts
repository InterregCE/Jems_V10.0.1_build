import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Observable, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {map, mergeMap, startWith, tap} from 'rxjs/operators';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';
import {IndicatorsStore} from '../../services/indicators-store.service';
import {ProgrammeIndicatorOutputService, ProgrammeIndicatorResultService, UserRoleDTO} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {ProgrammePageSidenavService} from '../../services/programme-page-sidenav.service';
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'jems-programme-indicators-overview-page',
  templateUrl: './programme-indicators-overview-page.component.html',
  styleUrls: ['./programme-indicators-overview-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeIndicatorsOverviewPageComponent extends BaseComponent {

  PermissionsEnum = PermissionsEnum;

  newOutputIndicatorPageSize$ = new Subject<number>();
  newOutputIndicatorPageIndex$ = new Subject<number>();
  newOutputIndicatorSort$ = new Subject<Partial<MatSort>>();
  outputIndicatorDeleted$ = new Subject<void>();

  newResultIndicatorPageSize$ = new Subject<number>();
  newResultIndicatorPageIndex$ = new Subject<number>();
  newResultIndicatorSort$ = new Subject<Partial<MatSort>>();
  resultIndicatorDeleted$ = new Subject<void>();

  isProgrammeSetupRestricted(): Observable<boolean> {
    return this.programmeEditableStateStore.isProgrammeEditableDependingOnCall$;
  }

  currentOutputIndicatorPage$ =
    combineLatest([
      this.newOutputIndicatorPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newOutputIndicatorPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newOutputIndicatorSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      ),
      this.outputIndicatorDeleted$.pipe(startWith(null))
    ])
      .pipe(
        mergeMap(([pageIndex, pageSize, sort]) =>
          this.programmeIndicatorOutputService.getOutputIndicatorDetails(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the Final Indicators:', this, page.content)),
      );

  currentResultIndicatorPage$ =
    combineLatest([
      this.newResultIndicatorPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newResultIndicatorPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newResultIndicatorSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      ),
      this.resultIndicatorDeleted$.pipe(startWith(null))
    ])
      .pipe(
        mergeMap(([pageIndex, pageSize, sort]) =>
          this.programmeIndicatorResultService.getResultIndicatorDetails(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the Result Indicators:', this, page.content)),
      );

  constructor(private indicatorsStore: IndicatorsStore,
              private programmeIndicatorOutputService: ProgrammeIndicatorOutputService,
              private programmeIndicatorResultService: ProgrammeIndicatorResultService,
              private programmePageSidenavService: ProgrammePageSidenavService,
              private programmeEditableStateStore: ProgrammeEditableStateStore) {
    super();
  }

}
