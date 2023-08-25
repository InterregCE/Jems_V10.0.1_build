import {Injectable} from "@angular/core";
import {BehaviorSubject, combineLatest, Observable, of, Subject} from "rxjs";
import {
  JemsFileMetadataDTO,
  PageJemsFileDTO,
  PaymentApplicationToEcAttachmentService,
  UserRoleDTO
} from "@cat/api";
import {APIError} from "@common/models/APIError";
import {Tables} from "@common/utils/tables";
import {MatSort} from "@angular/material/sort";
import {FileListTableConstants} from "@common/components/file-list/file-list-table/file-list-table-constants";
import {RoutingService} from "@common/services/routing.service";
import {PermissionService} from "../../../../../security/permissions/permission.service";
import {DownloadService} from "@common/services/download.service";
import {catchError, distinctUntilChanged, finalize, map, startWith, switchMap, take, tap} from "rxjs/operators";
import {Log} from "@common/utils/log";
import {v4 as uuid} from "uuid";
import {FileListItem} from "@common/components/file-list/file-list-item";
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {PaymentsToEcDetailPageStore} from "../../payments-to-ec-detail-page-store.service";

@Injectable({providedIn: 'root'})
export class PaymentToEcAttachmentsStore {

  paymentToEcId$: Observable<number>;
  attachments$: Observable<PageJemsFileDTO>;
  filesChanged$ = new Subject<void>();
  error$ = new Subject<APIError | null>();
  paymentToEcEditable$: Observable<boolean>;

  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(0);
  newSort$ = new BehaviorSubject<Partial<MatSort>>(FileListTableConstants.DEFAULT_SORT);

  constructor(
    private routingService: RoutingService,
    private paymentToEcAttachmentService: PaymentApplicationToEcAttachmentService,
    private paymentsToEcDetailPageStore: PaymentsToEcDetailPageStore,
    private permissionService: PermissionService,
    private downloadService: DownloadService,
  ) {
    this.paymentToEcId$ = this.paymentsToEcDetailPageStore.paymentToEcId$;
    this.attachments$ = this.getAttachments();
    this.paymentToEcEditable$ = this.paymentEditable();
  }

  public getAttachments(): Observable<PageJemsFileDTO> {
    return combineLatest([
      this.paymentToEcId$,
      this.newPageIndex$,
      this.newPageSize$,
      this.newSort$.pipe(
        map(sort => sort?.direction ? sort : FileListTableConstants.DEFAULT_SORT),
        map(sort => `${sort.active},${sort.direction}`),
        distinctUntilChanged(),
      ),
      this.filesChanged$.pipe(startWith(null)),
    ]).pipe(
      switchMap(([paymentToEcId, pageIndex, pageSize, sort]) =>
        this.paymentToEcAttachmentService.listPaymentAttachments(paymentToEcId, pageIndex, pageSize, sort)
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
    this.routingService.confirmLeaveMap.set(serviceId, true);
    return this.paymentToEcId$
      .pipe(
        take(1),
        switchMap(paymentToEcId =>
          this.paymentToEcAttachmentService.uploadAttachmentToPaymentForm(file, paymentToEcId)
        ),
        tap(() => this.filesChanged$.next()),
        tap(() => this.error$.next(null)),
        finalize(() => this.routingService.confirmLeaveMap.delete(serviceId)),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as JemsFileMetadataDTO);
        }),
      );
  }

  deletePaymentFile(file: FileListItem): Observable<void> {
    return this.paymentToEcAttachmentService.deleteAttachment(file.id)
      .pipe(tap(() => this.filesChanged$.next()));
  }

  private paymentEditable(): Observable<boolean> {
    return this.permissionService.hasPermission(PermissionsEnum.PaymentsToEcUpdate)
      .pipe(
        map(canEdit => canEdit)
      );
  }

  downloadFile(fileId: number): Observable<any> {
    return this.paymentToEcId$.pipe(
      take(1),
      switchMap(paymentToEcId => {
        this.downloadService.download(`/api/paymentApplicationsToEc/attachment/byFileId/${fileId}`, 'payment-attachment');
        return of(null);
      }),
    );
  }

}
