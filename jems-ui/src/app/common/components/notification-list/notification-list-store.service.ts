import {Injectable} from '@angular/core';
import {combineLatest, Observable, Subject} from 'rxjs';
import {NotificationService, PageNotificationDTO} from '@cat/api';
import {MatSort} from '@angular/material/sort';
import {PermissionService} from '../../../security/permissions/permission.service';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';

@Injectable()
export class NotificationListStoreService {

  static DEFAULT_SORT: Partial<MatSort> = {active: 'created', direction: 'desc'};

  notificationPage$: Observable<PageNotificationDTO>;
  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(private notificationService: NotificationService,
              private permissionService: PermissionService) {
    this.notificationPage$ = this.notificationPage();
  }

  private notificationPage(): Observable<PageNotificationDTO> {
    const defaultPageSize = Tables.DEFAULT_PAGE_OPTIONS.find(el => el === 10) ?? Tables.DEFAULT_INITIAL_PAGE_SIZE;

    return combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(defaultPageSize)),
      this.newSort$.pipe(
        startWith(NotificationListStoreService.DEFAULT_SORT),
        map(sort => sort?.direction ? sort : NotificationListStoreService.DEFAULT_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      )
    ])
      .pipe(
        switchMap(([pageIndex, pageSize, sort]) =>
          this.notificationService.getMyNotifications(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched notifications:', this, page.content)),
      );
  }
}
