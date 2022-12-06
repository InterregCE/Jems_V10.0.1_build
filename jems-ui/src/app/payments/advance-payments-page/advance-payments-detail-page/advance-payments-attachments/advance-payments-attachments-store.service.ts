import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {
  PageProjectReportFileDTO,
  PaymentAdvanceAttachmentService,
  ProjectReportFileMetadataDTO,
  UserRoleDTO
} from '@cat/api';
import {catchError, distinctUntilChanged, map, startWith, switchMap, take, tap} from 'rxjs/operators';
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
export class AdvancePaymentAttachmentsStore {

  public static ADVANCE_PAYMENT_DETAIL_PATH = '/advancePayments/';

  paymentId$ = new BehaviorSubject<number>(0);
  attachments$: Observable<PageProjectReportFileDTO>;
  filesChanged$ = new Subject<void>();
  error$ = new Subject<APIError | null>();
  paymentEditable$: Observable<boolean>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new BehaviorSubject<Partial<MatSort>>(FileListTableConstants.DEFAULT_SORT);

  constructor(
    private routingService: RoutingService,
    private paymentAttachmentService: PaymentAdvanceAttachmentService,
    private permissionService: PermissionService,
    private downloadService: DownloadService,
  ) {
    this.attachments$ = this.getAttachments();
    this.paymentEditable$ = this.paymentEditable();
  }

  public getAttachments(): Observable<PageProjectReportFileDTO> {
    return combineLatest([
      this.routingService.routeParameterChanges(AdvancePaymentAttachmentsStore.ADVANCE_PAYMENT_DETAIL_PATH, 'advancePaymentId')
        .pipe(map(id => Number(id))),
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        map(sort => sort?.direction ? sort : FileListTableConstants.DEFAULT_SORT),
        map(sort => `${sort.active},${sort.direction}`),
        distinctUntilChanged(),
      ),
      this.filesChanged$.pipe(startWith(null)),
    ]).pipe(
      switchMap(([advancePaymentId, pageIndex, pageSize, sort]) =>
        this.paymentAttachmentService.listPaymentAttachments(advancePaymentId, pageIndex, pageSize, sort)
      ),
      tap(data => Log.info('Fetched payment attachments by id', this, data)),
      catchError(error => {
        this.error$.next(error.error);
        return of({} as PageProjectReportFileDTO);
      })
    );
  }

  uploadPaymentFile(file: File): Observable<ProjectReportFileMetadataDTO> {
    const serviceId = uuid();
    this.routingService.confirmLeaveMap.set(serviceId, true);
    return this.routingService.routeParameterChanges(AdvancePaymentAttachmentsStore.ADVANCE_PAYMENT_DETAIL_PATH, 'advancePaymentId')
      .pipe(
          map(id => Number(id)),
          take(1),
          switchMap(paymentId =>
            this.paymentAttachmentService.uploadAttachmentToPaymentForm(file, paymentId)
          ),
          tap(() => this.filesChanged$.next()),
          tap(() => this.error$.next(null)),
          tap(() => this.routingService.confirmLeaveMap.delete(serviceId)),
          catchError(error => {
            this.error$.next(error.error);
            return of({} as ProjectReportFileMetadataDTO);
          }),
      );
  }

  deletePaymentFile(file: FileListItem): Observable<void> {
    return this.paymentAttachmentService.deleteAttachment(file.id)
      .pipe(tap(() => this.filesChanged$.next()));
  }

  private paymentEditable(): Observable<boolean> {
    return this.permissionService.hasPermission(PermissionsEnum.AdvancePaymentsUpdate)
      .pipe(
        map(canEdit => canEdit)
      );
  }

  downloadFile(fileId: number): Observable<any> {
    return this.routingService.routeParameterChanges(AdvancePaymentAttachmentsStore.ADVANCE_PAYMENT_DETAIL_PATH, 'advancePaymentId').pipe(
      map(id => Number(id)),
      take(1),
      switchMap(advancePaymentId => {
        this.downloadService.download(`/api/advancepayment/attachment/byFileId/${fileId}`, 'payment-attachment');
        return of(null);
      }),
    );
  }

}
