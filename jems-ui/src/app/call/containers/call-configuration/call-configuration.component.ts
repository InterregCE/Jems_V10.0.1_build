import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  CallService,
  InputCallCreate,
  InputCallUpdate,
  OutputCall,
  OutputProgrammeStrategy,
  OutputProgrammeFund,
  ProgrammePriorityService,
  ProgrammeStrategyService,
  ProgrammeFundService
} from '@cat/api'
import {BaseComponent} from '@common/components/base-component';
import {catchError, mergeMap, map, startWith, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {ActivatedRoute} from '@angular/router';
import {combineLatest, merge, Subject} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {CallStore} from '../../services/call-store.service';
import {Permission} from '../../../security/permissions/permission';
import {PermissionService} from '../../../security/permissions/permission.service';
import {Tables} from '../../../common/utils/tables';
import {CallPriorityCheckbox} from '../model/call-priority-checkbox';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {CallPageComponent} from '../call-page/call-page.component';
import {EventBusService} from '../../../common/services/event-bus/event-bus.service';

@Component({
  selector: 'app-call-configuration',
  templateUrl: './call-configuration.component.html',
  styleUrls: ['./call-configuration.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallConfigurationComponent extends BaseComponent {

  callId = this.activatedRoute?.snapshot?.params?.callId;
  publishCall$ = new Subject<number>();
  canceledEdit$ = new Subject<void>();

  private allPriorities$ = this.programmePriorityService
    .get(Tables.DEFAULT_INITIAL_PAGE_INDEX, 100, 'code,asc')
    .pipe(
      tap(page => Log.info('Fetched the priorities:', this, page.content)),
      map(page => page.content),
      map(priorities => priorities.map(priority => CallPriorityCheckbox.fromPriority(priority)))
    );

  private allActiveStrategies$ = this.programmeStrategyService.getProgrammeStrategies()
    .pipe(
      tap(programmeStrategies => Log.info('Fetched programme strategies:', this, programmeStrategies))
    );

  private allFunds$ = this.programmeFundService.getProgrammeFundList()
    .pipe(
      tap(programmeFunds => Log.info('Fetched programme funds:', this, programmeFunds))
    );

  private publishedCall$ = this.publishCall$
    .pipe(
      mergeMap(callUpdate => this.callService.publishCall(callUpdate)),
      tap(() => this.redirectToCallOverview()),
      tap(published => this.eventBusService.newSuccessMessage(
        CallPageComponent.name,
        {i18nKey: 'call.detail.publish.success', i18nArguments: {name: published.name}}
        )
      ),
      tap(saved => Log.info('Published call:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.eventBusService.newErrorMessage(CallPageComponent.name, error.error);
        throw error;
      })
    );

  details$ = combineLatest([
    merge(this.callStore.getCall(), this.publishedCall$),
    this.permissionService.permissionsChanged(),
    this.allActiveStrategies$,
    this.allPriorities$,
    this.allFunds$,
    this.canceledEdit$.pipe(startWith(null))
  ])
    .pipe(
      map(([call, permissions, allActiveStrategies, allPriorities, allFunds]) => ({
        call,
        applicantCanAccessCall: permissions[0] !== Permission.APPLICANT_USER
          || (call as OutputCall).status !== OutputCall.StatusEnum.PUBLISHED,
        strategies: this.getStrategies(allActiveStrategies, call),
        priorities: this.getPriorities(allPriorities, call),
        funds: this.getFunds(allFunds, call)
      })),
      tap(details => {
        // applicant user cannot see published calls
        if (!details.applicantCanAccessCall) {
          this.redirectToCallOverview();
        }
      }),
    );

  constructor(private callService: CallService,
              public callStore: CallStore,
              private activatedRoute: ActivatedRoute,
              private permissionService: PermissionService,
              private sideNavService: SideNavService,
              private eventBusService: EventBusService,
              private programmePriorityService: ProgrammePriorityService,
              private programmeStrategyService: ProgrammeStrategyService,
              private programmeFundService: ProgrammeFundService,) {
    super();
    this.callStore.init(this.callId);
    this.sideNavService.setHeadlines(this.destroyed$, [
      {
        headline: {i18nKey: 'call.detail.title'},
        scrollToTop: true,
        bullets: [
          {
            headline: {i18nKey: 'call.section.basic.data'},
            scrollRoute: 'callTitle'
          },
          {
            headline: {i18nKey: 'call.programme.priorities.title'},
            scrollRoute: 'callPriorities'
          },
          {
            headline: {i18nKey: 'call.strategy.title'},
            scrollRoute: 'callStrategies'
          },
          {
            headline: {i18nKey: 'call.funds.title'},
            scrollRoute: 'callFunds'
          }
        ]
      }
    ]);
  }

  createCall(call: InputCallCreate): void {
    this.callService.createCall(call)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.redirectToCallOverview()),
        tap(saved => Log.info('Created call:', this, saved)),
        tap(created => this.eventBusService.newSuccessMessage(
          CallPageComponent.name,
          {i18nKey: 'call.detail.created.success', i18nArguments: {name: created.name}}
          )
        ),
        catchError((error: HttpErrorResponse) => {
          this.eventBusService.newErrorMessage(CallPageComponent.name, error.error);
          throw error;
        })
      )
      .subscribe();
  }

  cancel(): void {
    if (this.callId) {
      this.canceledEdit$.next();
    } else {
      this.redirectToCallOverview();
    }
  }

  redirectToCallOverview(): void {
    this.sideNavService.navigate({headline: {i18nKey: 'calls'}, route: '/app/call'})
  }

  private getStrategies(allActiveStrategies: OutputProgrammeStrategy[], call: OutputCall): OutputProgrammeStrategy[] {
    const savedStrategies = allActiveStrategies
      .filter(strategy => strategy.active)
      .map(element =>
        ({strategy: element.strategy, active: false} as OutputProgrammeStrategy)
      );
    if (!call || !(call as OutputCall).strategies?.length) {
      return savedStrategies;
    }
    Log.debug('Adapting the selected strategies', this, allActiveStrategies, (call as OutputCall).strategies);
    savedStrategies
      .filter(element => (call as OutputCall).strategies.includes(element.strategy))
      .forEach(element => element.active = true)
    return savedStrategies;
  }

  private getPriorities(allPriorities: CallPriorityCheckbox[], call: OutputCall): CallPriorityCheckbox[] {
    if (!call || !(call as OutputCall).priorityPolicies) {
      return allPriorities;
    }
    const savedPolicies = (call as OutputCall).priorityPolicies
      .map(policy => policy.programmeObjectivePolicy ? policy.programmeObjectivePolicy : policy) as any;
    Log.debug('Adapting the priority policies', this, allPriorities, savedPolicies);
    return allPriorities.map(priority => CallPriorityCheckbox.fromSavedPolicies(priority, savedPolicies));
  }

  private getFunds(allFunds: OutputProgrammeFund[], call: OutputCall | InputCallUpdate): OutputProgrammeFund[] {
    const savedFunds = allFunds
      .filter(fund => fund.selected)
      .map(element =>
        ({
          id: element.id,
          abbreviation: element.abbreviation,
          description: element.description,
          selected: false
        } as OutputProgrammeFund)
      );
    if (!call || !(call as OutputCall).funds?.length) {
      return savedFunds;
    }
    Log.debug('Adapting the selected funds', this, allFunds, (call as OutputCall).funds);
    const callFundIds = (call as OutputCall).funds.map(element => element.id ? element.id : element);
    savedFunds
      .filter(element => callFundIds.includes(element.id))
      .forEach(element => element.selected = true)
    return savedFunds;
  }
}
