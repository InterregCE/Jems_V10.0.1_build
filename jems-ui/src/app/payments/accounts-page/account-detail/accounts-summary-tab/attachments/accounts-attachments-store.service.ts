import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {JemsFileMetadataDTO, PageJemsFileDTO, PaymentAccountAttachmentService, UserRoleDTO} from '@cat/api';
import {APIError} from '@common/models/APIError';
import {Tables} from '@common/utils/tables';
import {MatSort} from '@angular/material/sort';
import {FileListTableConstants} from '@common/components/file-list/file-list-table/file-list-table-constants';
import {RoutingService} from '@common/services/routing.service';
import {DownloadService} from '@common/services/download.service';
import {catchError, distinctUntilChanged, finalize, map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {v4 as uuid} from 'uuid';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {AccountsPageStore} from '../../../accounts-page.store';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';

@Injectable({providedIn: 'root'})
export class AccountsAttachmentsStore {
  paymentAccountId$: Observable<number>;
  attachments$: Observable<PageJemsFileDTO>;
  filesChanged$ = new Subject<void>();
  error$ = new Subject<APIError | null>();
  paymentAccountEditable$: Observable<boolean>;

  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(0);
  newSort$ = new BehaviorSubject<Partial<MatSort>>(FileListTableConstants.DEFAULT_SORT);

  constructor(
    private routingService: RoutingService,
    private paymentAccountAttachmentService: PaymentAccountAttachmentService,
    private accountsPageStore: AccountsPageStore,
    private permissionService: PermissionService,
    private downloadService: DownloadService,
  ) {
    this.paymentAccountId$ = this.accountsPageStore.accountId$;
    this.attachments$ = this.getAttachments();
    this.paymentAccountEditable$ = this.paymentEditable();
  }

  public getAttachments(): Observable<PageJemsFileDTO> {
    return combineLatest([
      this.paymentAccountId$,
      this.newPageIndex$,
      this.newPageSize$,
      this.newSort$.pipe(
        map(sort => sort?.direction ? sort : FileListTableConstants.DEFAULT_SORT),
        map(sort => `${sort.active},${sort.direction}`),
        distinctUntilChanged(),
      ),
      this.filesChanged$.pipe(startWith(null)),
    ]).pipe(
      switchMap(([paymentAccountId, pageIndex, pageSize, sort]) =>
        this.paymentAccountAttachmentService.listPaymentAccountAttachments(paymentAccountId, pageIndex, pageSize, sort)
      ),
      tap(page => {
        if (page.totalPages > 0 && page.number >= page.totalPages) {
          this.newPageIndex$.next(page.totalPages - 1);
        }
      }),
      tap(data => Log.info('Fetched payment account attachments by id', this, data)),
      catchError(error => {
        this.error$.next(error.error);
        return of({} as PageJemsFileDTO);
      })
    );
  }

  uploadPaymentFile(file: File): Observable<JemsFileMetadataDTO> {
    const serviceId = uuid();
    this.routingService.confirmLeaveSet.add(serviceId);
    return this.paymentAccountId$
      .pipe(
        take(1),
        switchMap(paymentAccountId =>
          this.paymentAccountAttachmentService.uploadAttachmentToPaymentAccountForm(file, paymentAccountId)
        ),
        tap(() => this.filesChanged$.next()),
        tap(() => this.error$.next(null)),
        finalize(() => this.routingService.confirmLeaveSet.delete(serviceId)),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as JemsFileMetadataDTO);
        }),
      );
  }

  deletePaymentFile(file: FileListItem): Observable<void> {
    return this.paymentAccountAttachmentService.deleteAttachment(file.id)
      .pipe(tap(() => this.filesChanged$.next()));
  }

  private paymentEditable(): Observable<boolean> {
    return this.permissionService.hasPermission(PermissionsEnum.PaymentsAccountUpdate)
      .pipe(
        map(canEdit => canEdit)
      );
  }

  setDescription(data: FileDescriptionChange): Observable<any> {
    return this.paymentAccountAttachmentService.updateAttachmentDescription(data.id, data.description);
  }
  downloadFile(fileId: number): Observable<any> {
    return this.downloadService.download(`/api/payments/accounts/attachment/byFileId/${fileId}`, 'payment-account-attachment');
  }
}
