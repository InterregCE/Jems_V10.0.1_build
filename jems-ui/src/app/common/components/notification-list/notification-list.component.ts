import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {NotificationListStoreService} from '@common/components/notification-list/notification-list-store.service';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {NotificationDTO, PageNotificationDTO} from '@cat/api';
import {animate, state, style, transition, trigger} from '@angular/animations';

@Component({
  selector: 'jems-notification-list',
  templateUrl: './notification-list.component.html',
  styleUrls: ['./notification-list.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
  providers: [NotificationListStoreService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
@UntilDestroy()
export class NotificationListComponent implements OnInit{

  type = NotificationDTO.TypeEnum;

  displayedColumns = ['created', 'call', 'project', 'acronym', 'partner', 'subject'];
  displayedColumnsWithExpanded = [...this.displayedColumns, 'expand'];
  expandedElement: NotificationDTO | null;
  data$: Observable<{
    page: PageNotificationDTO;
  }>;
  constructor(public listStore: NotificationListStoreService) {
  }

  ngOnInit() {
    this.data$ = combineLatest([
       this.listStore.notificationPage$,
    ])
      .pipe(
        map(([page]) => ({page}))
      );
  }


}
