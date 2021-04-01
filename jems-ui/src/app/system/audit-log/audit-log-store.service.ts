import {Injectable} from '@angular/core';
import {combineLatest, Subject} from 'rxjs';
import {
  AuditSearchRequestDTO,
  AuditService,
  PageAuditDTO,
} from '@cat/api';
import {startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../common/utils/log';
import {Tables} from '../../common/utils/tables';

@Injectable()
export class AuditLogStore {

  auditPageSize$ = new Subject<number>();
  auditPageIndex$ = new Subject<number>();
  auditPageFilter$ = new Subject<AuditSearchRequestDTO>();

  auditPage$ =
    combineLatest([
      this.auditPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.auditPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.auditPageFilter$.pipe(startWith({} as AuditSearchRequestDTO)),
    ])
      .pipe(
        switchMap(([pageIndex, pageSize, pageFilter]) =>
          this.auditService.getAudits(pageFilter, pageIndex, pageSize)),
        tap((page: PageAuditDTO) => Log.info('Fetched the Audits:', this, page.content)),
      );


  constructor(private auditService: AuditService) {
  }
}
