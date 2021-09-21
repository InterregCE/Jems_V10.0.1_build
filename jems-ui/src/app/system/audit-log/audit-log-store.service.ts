import {EventEmitter, Injectable} from '@angular/core';
import {combineLatest, Subject} from 'rxjs';
import {
  AuditSearchRequestDTO,
  AuditService,
  PageAuditDTO,
} from '@cat/api';
import {debounceTime, map, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../common/utils/log';
import {Tables} from '../../common/utils/tables';
import {MatSort} from '@angular/material/sort';

@Injectable()
export class AuditLogStore {

  auditPageSize$ = new Subject<number>();
  auditPageIndex$ = new Subject<number>();
  auditPageFilter$ = new Subject<AuditSearchRequestDTO>();
  auditPageSort$ = new EventEmitter<Partial<MatSort>>();

  auditPage$ =
    combineLatest([
      this.auditPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.auditPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.auditPageSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      ),
      this.auditPageFilter$.pipe(startWith({} as AuditSearchRequestDTO)),
    ])
      .pipe(
        debounceTime(50),
        switchMap(([pageIndex, pageSize, sort, pageFilter]) =>
          this.auditService.getAudits(pageFilter, pageIndex, pageSize, sort)),
        tap((page: PageAuditDTO) => Log.info('Fetched the Audits:', this, page.content)),
      );


  constructor(private auditService: AuditService) {
  }
}
