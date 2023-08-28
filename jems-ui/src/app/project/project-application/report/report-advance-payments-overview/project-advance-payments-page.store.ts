import {Injectable} from '@angular/core';
import {
  PageAdvancePaymentDTO, ProjectAdvancePaymentsService
} from '@cat/api';
import {Observable, combineLatest, Subject} from 'rxjs';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';
import {MatSort} from '@angular/material/sort';
import {UntilDestroy} from '@ngneat/until-destroy';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';

@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class ProjectAdvancePaymentsPageStore {

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  projectAdvancePaymentDTO$: Observable<PageAdvancePaymentDTO>;

  constructor(private projectAdvancePaymentsService: ProjectAdvancePaymentsService,
              private projectStore: ProjectStore) {
    this.projectAdvancePaymentDTO$ = this.advancePayments();
  }

  private advancePayments(): Observable<PageAdvancePaymentDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => (sort?.direction) ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      )
    ])
      .pipe(
        switchMap(([projectId, pageIndex, pageSize, sort]) =>
          this.projectAdvancePaymentsService.getAdvancePayments(projectId, pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the project advance payments:', this, page.content)),
      );
  }
}
