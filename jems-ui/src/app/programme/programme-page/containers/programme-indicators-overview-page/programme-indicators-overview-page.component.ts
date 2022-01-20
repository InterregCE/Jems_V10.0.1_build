import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {mergeMap, map, startWith, tap} from 'rxjs/operators';
import {Tables} from '../../../../common/utils/tables';
import {Log} from '../../../../common/utils/log';
import {IndicatorsStore} from '../../services/indicators-store.service';
import {ProgrammeIndicatorService, UserRoleDTO} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {ProgrammePageSidenavService} from '../../services/programme-page-sidenav.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'jems-programme-indicators-overview-page',
  templateUrl: './programme-indicators-overview-page.component.html',
  styleUrls: ['./programme-indicators-overview-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeIndicatorsOverviewPageComponent extends BaseComponent {

  PermissionsEnum = PermissionsEnum;
  outputIndicator$ = this.indicatorsStore.outputIndicator();
  resultIndicator$ = this.indicatorsStore.resultIndicator();

  newOutputIndicatorPageSize$ = new Subject<number>();
  newOutputIndicatorPageIndex$ = new Subject<number>();
  newOutputIndicatorSort$ = new Subject<Partial<MatSort>>();

  newResultIndicatorPageSize$ = new Subject<number>();
  newResultIndicatorPageIndex$ = new Subject<number>();
  newResultIndicatorSort$ = new Subject<Partial<MatSort>>();

  currentOutputIndicatorPage$ =
    combineLatest([
      this.newOutputIndicatorPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newOutputIndicatorPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newOutputIndicatorSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      )
    ])
      .pipe(
        mergeMap(([pageIndex, pageSize, sort]) =>
          this.programmeIndicatorService.getOutputIndicatorDetails(pageIndex, pageSize, sort)),
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
      )
    ])
      .pipe(
        mergeMap(([pageIndex, pageSize, sort]) =>
          this.programmeIndicatorService.getResultIndicatorDetails(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the Result Indicators:', this, page.content)),
      );

  constructor(private indicatorsStore: IndicatorsStore,
              private programmeIndicatorService: ProgrammeIndicatorService,
              private programmePageSidenavService: ProgrammePageSidenavService) {
    super();
  }

}
