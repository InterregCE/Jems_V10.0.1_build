import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  InputCallUpdate,
  OutputCall,
  OutputProgrammeStrategy,
  OutputProgrammeFund,
  ProgrammePriorityService,
  ProgrammeStrategyService,
  ProgrammeFundService
} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {map, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {ActivatedRoute} from '@angular/router';
import {combineLatest} from 'rxjs';
import {CallStore} from '../../services/call-store.service';
import {Permission} from '../../../security/permissions/permission';
import {PermissionService} from '../../../security/permissions/permission.service';
import {Tables} from '../../../common/utils/tables';
import {CallPriorityCheckbox} from '../model/call-priority-checkbox';
import {CallPageSidenavService} from '../../services/call-page-sidenav.service';

@Component({
  selector: 'app-call-configuration',
  templateUrl: './call-configuration.component.html',
  styleUrls: ['./call-configuration.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallConfigurationComponent extends BaseComponent {

  callId = this.activatedRoute?.snapshot?.params?.callId;

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

  details$ = combineLatest([
    this.callStore.call$,
    this.permissionService.permissionsChanged(),
    this.allActiveStrategies$,
    this.allPriorities$,
    this.allFunds$,
  ])
    .pipe(
      map(([call, permissions, allActiveStrategies, allPriorities, allFunds]) => ({
        call,
        applicantCanAccessCall: permissions[0] !== Permission.APPLICANT_USER
          || call.status !== OutputCall.StatusEnum.PUBLISHED,
        strategies: this.getStrategies(allActiveStrategies, call),
        priorities: this.getPriorities(allPriorities, call),
        funds: this.getFunds(allFunds, call)
      })),
      tap(details => {
        // applicant user cannot see published calls
        if (!details.applicantCanAccessCall) {
          this.callNavService.redirectToCallOverview();
        }
      }),
    );

  constructor(public callStore: CallStore,
              private activatedRoute: ActivatedRoute,
              private permissionService: PermissionService,
              // private eventBusService: EventBusService,
              private programmePriorityService: ProgrammePriorityService,
              private programmeStrategyService: ProgrammeStrategyService,
              private programmeFundService: ProgrammeFundService,
              private callNavService: CallPageSidenavService) {
    super();
    this.callStore.init(this.callId);
    this.callNavService.init(this.callId);
  }

  private getStrategies(allActiveStrategies: OutputProgrammeStrategy[], call: OutputCall): OutputProgrammeStrategy[] {
    const savedStrategies = allActiveStrategies
      .filter(strategy => strategy.active)
      .map(element =>
        ({strategy: element.strategy, active: false} as OutputProgrammeStrategy)
      );
    if (!call || !call.strategies?.length) {
      return savedStrategies;
    }
    Log.debug('Adapting the selected strategies', this, allActiveStrategies, call.strategies);
    savedStrategies
      .filter(element => call.strategies.includes(element.strategy))
      .forEach(element => element.active = true);
    return savedStrategies;
  }

  private getPriorities(allPriorities: CallPriorityCheckbox[], call: OutputCall): CallPriorityCheckbox[] {
    if (!call || !call.priorityPolicies) {
      return allPriorities;
    }
    const savedPolicies = call.priorityPolicies
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
      .forEach(element => element.selected = true);
    return savedFunds;
  }
}
