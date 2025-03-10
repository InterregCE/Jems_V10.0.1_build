import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {
  AdvancePaymentDetailDTO,
  AdvancePaymentsService, AdvancePaymentUpdateDTO,
  OutputProjectSimple, ProjectPartnerPaymentSummaryDTO, ProjectPartnerService,
  ProjectService, ProjectVersionDTO,
  UserRoleCreateDTO,
} from '@cat/api';
import {PermissionService} from '../../../security/permissions/permission.service';
import {RoutingService} from '@common/services/routing.service';
import {map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {MatSort} from '@angular/material/sort';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class AdvancePaymentsDetailPageStoreStore {
  public static ADVANCE_PAYMENT_PATH = '/app/payments/advancePayments/';

  advancePaymentDetail$: Observable<AdvancePaymentDetailDTO>;
  savedAdvancePaymentDetail$ = new Subject<AdvancePaymentDetailDTO>();
  searchProjectsByName$ = new Subject<string>();
  getProjectPartnersByProjectId$ = new Subject<number>();
  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  userCanEdit$: Observable<boolean>;

  constructor(private advancePaymentsService: AdvancePaymentsService,
              private permissionService: PermissionService,
              private routingService: RoutingService,
              private projectService: ProjectService,
              private projectPartnerService: ProjectPartnerService,) {
    this.advancePaymentDetail$ = this.paymentDetail();
    this.userCanEdit$ = this.userCanEdit();
  }


  private paymentDetail(): Observable<AdvancePaymentDetailDTO> {
    const initialPaymentDetail$ = this.routingService.routeParameterChanges(AdvancePaymentsDetailPageStoreStore.ADVANCE_PAYMENT_PATH, 'advancePaymentId')
      .pipe(
        switchMap((paymentId: number) => paymentId ? this.advancePaymentsService.getAdvancePaymentDetail(paymentId) : of({}) as Observable<AdvancePaymentDetailDTO>),
        tap(data => Log.info('Fetched advance payment detail', this, data)),
        shareReplay(1),
      );

    return merge(initialPaymentDetail$, this.savedAdvancePaymentDetail$);
  }

  getContractedProjects(): Observable<OutputProjectSimple[]> {
    return this.searchProjectsByName$.pipe(
      startWith(' '),
      switchMap((acronym) =>
        this.projectService.getContractedProjects(acronym)),
      map(page => page.content),
      tap(page => Log.info('Fetched filtered contracted projects:', this, page)),
      untilDestroyed(this),
    );
  }

  private userCanEdit(): Observable<boolean> {
    return  this.permissionService.hasPermission(PermissionsEnum.AdvancePaymentsUpdate)
      .pipe(
        map((canUpdate) => canUpdate)
      );
  }

  updateAdvancePayment(advancePaymentData: AdvancePaymentUpdateDTO): Observable<AdvancePaymentDetailDTO> {
    return this.advancePaymentsService.updateAdvancePayment(advancePaymentData).pipe(
      tap(saved => Log.info('Advance payment details updated!', saved)),
      tap(data => this.savedAdvancePaymentDetail$.next(data))
    );
  }

  getPartnerData(): Observable<ProjectPartnerPaymentSummaryDTO[]> {
    return combineLatest([
      this.getProjectPartnersByProjectId$,
      this.paymentDetail()
    ]).pipe(
      switchMap(([projectId, paymentDetail]) => this.projectPartnerService.getProjectPartnersAndContributions(projectId, paymentDetail.projectVersion)),
      tap(partnerList => Log.info('Fetched filtered partners for project:', this, partnerList)),
      untilDestroyed(this),
      shareReplay(1)
    );
  }

  getLastApprovedProjectVersion(projectId: number): Observable<ProjectVersionDTO | undefined> {
    return this.projectService.getProjectVersions(projectId).pipe(
      map(versions => versions.find(v => v.status === ProjectVersionDTO.StatusEnum.APPROVED || v.status === ProjectVersionDTO.StatusEnum.CONTRACTED || v.status === ProjectVersionDTO.StatusEnum.CLOSED))
    );
  }
}
