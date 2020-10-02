import {ChangeDetectionStrategy, Component, TemplateRef, ViewChild} from '@angular/core';
import {combineLatest, Subject} from 'rxjs';
import {flatMap, map, startWith, tap} from 'rxjs/operators';
import {Tables} from '../../../common/utils/tables';
import {Log} from '../../../common/utils/log';
import {MatSort} from '@angular/material/sort';
import {CallService, ProjectService} from '@cat/api';
import {CallStore} from '../../services/call-store.service';
import {Permission} from '../../../security/permissions/permission';

@Component({
  selector: 'app-call-page',
  templateUrl: './call-page.component.html',
  styleUrls: ['./call-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallPageComponent {

  Permission = Permission
  publishedCall$ = this.callStore.publishedCall();

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  currentPage$ =
    combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      )
    ])
      .pipe(
        flatMap(([pageIndex, pageSize, sort]) =>
          this.callService.getCalls(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the Calls:', this, page.content)),
      );

  newApplicationsPageSize$ = new Subject<number>();
  newApplicationsPageIndex$ = new Subject<number>();
  newApplicationsSort$ = new Subject<Partial<MatSort>>();

  currentApplicationPage$ =
    combineLatest([
      this.newApplicationsPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newApplicationsPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newApplicationsSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      )
    ])
      .pipe(
        flatMap(([pageIndex, pageSize, sort]) =>
          this.projectService.getProjects(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the projects:', this, page.content)),
      );


  constructor(private callService: CallService,
              private projectService: ProjectService,
              private callStore: CallStore) {
    console.log('constructing call page component');
  }
}
