import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  CallDetailDTO,
  OutputProgrammeStrategy,
  ProgrammeFundDTO,
  ProgrammeFundService,
  ProgrammePriorityService,
  ProgrammeSpecificObjectiveDTO,
  ProgrammeStrategyService
} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {map, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {ActivatedRoute} from '@angular/router';
import {combineLatest} from 'rxjs';
import {CallStore} from '../../services/call-store.service';
import {PermissionService} from '../../../security/permissions/permission.service';
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
    .get()
    .pipe(
      tap(priorities => Log.info('Fetched the priorities:', this, priorities)),
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
    this.callStore.isApplicant$,
    this.allActiveStrategies$,
    this.allPriorities$,
    this.allFunds$,
  ])
    .pipe(
      map(([call, isApplicant, allActiveStrategies, allPriorities, allFunds]) => ({
        call,
        isApplicant,
        strategies: this.getStrategies(allActiveStrategies, call),
        priorities: this.getPriorities(allPriorities, call),
        funds: this.getFunds(allFunds, call)
      })),
    );

  constructor(public callStore: CallStore,
              private activatedRoute: ActivatedRoute,
              private permissionService: PermissionService,
              private programmePriorityService: ProgrammePriorityService,
              private programmeStrategyService: ProgrammeStrategyService,
              private programmeFundService: ProgrammeFundService,
              private callNavService: CallPageSidenavService) {
    super();
    this.callStore.init(this.callId);
    this.callNavService.init(this.callId);
  }

  private getStrategies(allActiveStrategies: OutputProgrammeStrategy[], call: CallDetailDTO): OutputProgrammeStrategy[] {
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

  private getPriorities(allPriorities: CallPriorityCheckbox[], call: CallDetailDTO): CallPriorityCheckbox[] {
    if (!call || !call.objectives) {
      return allPriorities;
    }
    const savedPolicies = call.objectives
      .map(policy => policy.objective ? policy.objective : policy) as any;
    Log.debug('Adapting the priority policies', this, allPriorities, savedPolicies);
    return allPriorities.map(priority => CallPriorityCheckbox.fromSavedPolicies(priority, savedPolicies));
  }

  private getFunds(allFunds: ProgrammeFundDTO[], call: CallDetailDTO): ProgrammeFundDTO[] {
    const savedFunds = allFunds
      .filter(fund => fund.selected)
      .map(element =>
        ({
          id: element.id,
          abbreviation: element.abbreviation,
          description: element.description,
          selected: false
        } as ProgrammeFundDTO)
      );
    if (!call || !call.funds?.length) {
      return savedFunds;
    }
    Log.debug('Adapting the selected funds', this, allFunds, call.funds);
    const callFundIds = call.funds.map(element => element.id ? element.id : element);
    savedFunds
      .filter(element => callFundIds.includes(element.id))
      .forEach(element => element.selected = true);
    return savedFunds;
  }
}
