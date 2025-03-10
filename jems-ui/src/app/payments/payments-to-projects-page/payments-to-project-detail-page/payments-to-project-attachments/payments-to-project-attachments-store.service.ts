import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {JemsFileMetadataDTO, PageJemsFileDTO, PaymentAttachmentService, UserRoleDTO} from '@cat/api';
import {catchError, distinctUntilChanged, finalize, map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {v4 as uuid} from 'uuid';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {RoutingService} from '@common/services/routing.service';
import {APIError} from '@common/models/APIError';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {Tables} from '@common/utils/tables';
import {FileListTableConstants} from '@common/components/file-list/file-list-table/file-list-table-constants';
import {MatSort} from '@angular/material/sort';
import {DownloadService} from '@common/services/download.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Injectable({providedIn: 'root'})
export class PaymentAttachmentsStore {
  public static PAYMENT_DETAIL_PATH = '/payments/';

  paymentId$ = new BehaviorSubject<number>(0);
  attachments$: Observable<PageJemsFileDTO>;
  filesChanged$ = new Subject<void>();
  error$ = new Subject<APIError | null>();
  paymentEditable$: Observable<boolean>;

  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(0);
  newSort$ = new BehaviorSubject<Partial<MatSort>>(FileListTableConstants.DEFAULT_SORT);

  constructor(
    private routingService: RoutingService,
    private paymentAttachmentService: PaymentAttachmentService,
    private permissionService: PermissionService,
    private downloadService: DownloadService,
  ) {
    this.attachments$ = this.getAttachments();
    this.paymentEditable$ = this.paymentEditable();
  }

  public getAttachments(): Observable<PageJemsFileDTO> {
    return combineLatest([
      this.routingService.routeParameterChanges(PaymentAttachmentsStore.PAYMENT_DETAIL_PATH, 'paymentId')
        .pipe(map(id => Number(id))),
      this.newPageIndex$,
      this.newPageSize$,
      this.newSort$.pipe(
        map(sort => sort?.direction ? sort : FileListTableConstants.DEFAULT_SORT),
        map(sort => `${sort.active},${sort.direction}`),
        distinctUntilChanged(),
      ),
      this.filesChanged$.pipe(startWith(null)),
    ]).pipe(
      switchMap(([paymentId, pageIndex, pageSize, sort]) =>
        this.paymentAttachmentService.listPaymentAttachments(paymentId, pageIndex, pageSize, sort)
      ),
      tap(page => {
        if (page.totalPages > 0 && page.number >= page.totalPages) {
          this.newPageIndex$.next(page.totalPages - 1);
        }
      }),
      tap(data => Log.info('Fetched payment attachments by id', this, data)),
      catchError(error => {
        this.error$.next(error.error);
        return of({} as PageJemsFileDTO);
      })
    );
  }

  uploadPaymentFile(file: File): Observable<JemsFileMetadataDTO> {
    const serviceId = uuid();
    this.routingService.confirmLeaveSet.add(serviceId);
    return this.routingService.routeParameterChanges(PaymentAttachmentsStore.PAYMENT_DETAIL_PATH, 'paymentId')
      .pipe(
        map(id => Number(id)),
        take(1),
        switchMap(paymentId =>
          this.paymentAttachmentService.uploadAttachmentToPaymentForm(file, paymentId)
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
    return this.paymentAttachmentService.deleteAttachment(file.id)
      .pipe(tap(() => this.filesChanged$.next()));
  }

  private paymentEditable(): Observable<boolean> {
    return this.permissionService.hasPermission(PermissionsEnum.PaymentsUpdate)
      .pipe(
        map(canEdit => canEdit)
      );
  }

  downloadFile(fileId: number): Observable<any> {
    return this.routingService.routeParameterChanges(PaymentAttachmentsStore.PAYMENT_DETAIL_PATH, 'paymentId').pipe(
      map(id => Number(id)),
      take(1),
      switchMap(paymentId => {
        this.downloadService.download(`/api/payments/attachment/byFileId/${fileId}`, 'payment-attachment');
        return of(null);
      }),
    );
  }

}
