import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {CallService, InputCallCreate, InputCallUpdate, OutputCall} from '@cat/api'
import {BaseComponent} from '@common/components/base-component';
import {catchError, flatMap, map, startWith, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {ActivatedRoute, Router} from '@angular/router';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';
import {CallStore} from '../../services/call-store.service';
import {Permission} from '../../../security/permissions/permission';
import {PermissionService} from '../../../security/permissions/permission.service';
import {TreeNode} from '../../components/call-priority-tree/call-priority-tree.component';
import {MatSort} from '@angular/material/sort';
import {Tables} from '../../../common/utils/tables';
import {ProgrammePriorityService, PageOutputProgrammePriority, OutputProgrammePriority, OutputProgrammePriorityPolicy} from '@cat/api';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-call-configuration',
  templateUrl: './call-configuration.component.html',
  styleUrls: ['./call-configuration.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallConfigurationComponent extends BaseComponent implements OnInit {

  callId = this.activatedRoute?.snapshot?.params?.callId;
  callSaveError$ = new Subject<I18nValidationError | null>();
  saveCall$ = new Subject<InputCallUpdate>();
  publishCall$ = new Subject<number>();
  datasource = new Subject<TreeNode[]>();

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  currentPage$ =
    combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith({ active: 'code', direction: 'asc' }),
        map(sort => sort?.direction ? sort : { active: 'code', direction: 'asc' }),
        map(sort => `${sort.active},${sort.direction}`)
      ),
    ])
      .pipe(
        flatMap(([pageIndex, pageSize, sort]) =>
          this.programmePriorityService.get(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the priorities:', this, page.content)),
      );

  private callById$ = this.callId
    ? this.callService.getCallById(this.callId)
      .pipe(
        tap(call => Log.info('Fetched call:', this, call))
      )
    : of({});

  private savedCall$ = this.saveCall$
    .pipe(
      flatMap(callUpdate => this.callService.updateCall(callUpdate)),
      tap(() => this.callSaveError$.next(null)),
      tap(() => this.redirectToCallOverview()),
      tap(saved => Log.info('Updated call:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.callSaveError$.next(error.error);
        throw error;
      })
    );

  private publishedCall$ = this.publishCall$
    .pipe(
      flatMap(callUpdate => this.callService.publishCall(callUpdate)),
      tap(() => this.callSaveError$.next(null)),
      tap(() => this.redirectToCallOverview()),
      tap(published => this.callStore.callPublished(published)),
      tap(saved => Log.info('Published call:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.callSaveError$.next(error.error);
        throw error;
      })
    );

  details$ = combineLatest([
    merge(this.callById$, this.savedCall$, this.publishedCall$),
    this.permissionService.permissionsChanged()
  ])
    .pipe(
      map(([call, permissions]) => ({
        call,
        applicantCanAccessCall: permissions[0] !== Permission.APPLICANT_USER
          || (call as OutputCall).status !== OutputCall.StatusEnum.PUBLISHED
      })),
      tap(details => {
        // applicant user cannot see published calls
        if (!details.applicantCanAccessCall) {
          this.redirectToCallOverview();
        }
      })
    );

  constructor(private callService: CallService,
              private callStore: CallStore,
              private activatedRoute: ActivatedRoute,
              private permissionService: PermissionService,
              private router: Router,
              private programmePriorityService: ProgrammePriorityService,
              private translateService: TranslateService) {
    super();
  }

  ngOnInit(): void {
    this.currentPage$.pipe(
      take(1),
      takeUntil(this.destroyed$),
    ).subscribe((priorities: PageOutputProgrammePriority) => {
      this.buildPriorityDatasource(priorities.content);
    })
  }

  createCall(call: InputCallCreate): void {
    this.callService.createCall(call)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.callSaveError$.next(null)),
        tap(() => this.redirectToCallOverview()),
        tap(saved => Log.info('Created call:', this, saved)),
        catchError((error: HttpErrorResponse) => {
          this.callSaveError$.next(error.error);
          throw error;
        })
      )
      .subscribe();
  }

  redirectToCallOverview() {
    this.router.navigate(['/calls'])
  }

  private buildPriorityDatasource(priorities: OutputProgrammePriority[]) {
    const dataTree: TreeNode[] = [];
    priorities.forEach((priority: OutputProgrammePriority) => {
      const childDataTree: TreeNode[] = [];
      priority.programmePriorityPolicies.forEach((policy: OutputProgrammePriorityPolicy) => {
        childDataTree.push({
          children: [],
          code: policy.code,
          isPriority: false,
          isPolicy: true,
          item: policy.code + ' - ' + this.translateService.instant('programme.policy.' + policy.programmeObjectivePolicy),
        } as TreeNode)
      });
      dataTree.push({
        children: childDataTree,
        code: priority.code,
        isPriority: true,
        isPolicy: false,
        item:  priority.code + ' - ' + priority.title,
      } as TreeNode)
    });
    this.datasource.next(dataTree);
  }
}
