import {Injectable} from '@angular/core';
import {
  ApplicationFormFieldConfigurationDTO,
  ApplicationFormFieldConfigurationsService,
  CallDetailDTO,
  UpdateApplicationFormFieldConfigurationRequestDTO
} from '@cat/api';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {RoutingService} from '@common/services/routing.service';
import {CallStore} from '../services/call-store.service';
import {map, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {filter} from 'rxjs/internal/operators';
import {Log} from '@common/utils/log';
import {ApplicationFormFieldNode} from './application-form-field-node';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import AvailableInStepEnum = UpdateApplicationFormFieldConfigurationRequestDTO.AvailableInStepEnum;

@Injectable()
export class ApplicationFormConfigurationPageStore {
  static CONFIGURATION_DETAIL_PATH = '/applicationFormConfiguration';

  fieldConfigurations$: Observable<ApplicationFormFieldNode[]>;
  callHasTwoSteps$: Observable<boolean>;
  callIsEditable$: Observable<boolean>;

  private savedConfigurations$ = new Subject<ApplicationFormFieldConfigurationDTO[]>();

  constructor(private applicationFormConfigurationService: ApplicationFormFieldConfigurationsService,
              private router: RoutingService,
              private callStore: CallStore) {
    this.fieldConfigurations$ = this.fieldConfigurations();
    this.callHasTwoSteps$ = this.callHasTwoSteps();
    this.callIsEditable$ = this.callStore.callIsEditable$;
  }

  saveConfigurations(formFields: UpdateApplicationFormFieldConfigurationRequestDTO[]): Observable<CallDetailDTO> {
    return this.callStore.call$
      .pipe(
        switchMap(call => this.applicationFormConfigurationService.update(call.id, formFields)),
        tap(call => this.savedConfigurations$.next(call.applicationFormFieldConfigurations))
      );
  }

  private fieldConfigurations(): Observable<ApplicationFormFieldNode[]> {
    const initialConfigs$ = combineLatest([
      this.callStore.call$,
      this.router.routeChanges(ApplicationFormConfigurationPageStore.CONFIGURATION_DETAIL_PATH)
    ])
      .pipe(
        filter(([call]) => !!call?.id),
        switchMap(([call]) => this.applicationFormConfigurationService.getByCallId(call.id)),
        tap(configs => Log.info('Fetched the application form field configurations:', this, configs)),
      );

    return merge(initialConfigs$, this.savedConfigurations$)
      .pipe(
        withLatestFrom(this.callStore.callIsPublished$),
        map(([configs, callIsPublished]) => this.getApplicationFormFieldNodeList(callIsPublished, configs))
      );
  }

  private getApplicationFormFieldNodeList(callPublished: boolean, configs: ApplicationFormFieldConfigurationDTO[]): ApplicationFormFieldNode[] {
    return [
      this.addParentNode('application.config.project.section.a', 0, [
        this.addSectionNodes('application.config.project.section.a.1', 0, APPLICATION_FORM.SECTION_A.PROJECT_IDENTIFICATION, callPublished, configs),
        this.addSectionNodes('application.config.project.section.a.2', 0, APPLICATION_FORM.SECTION_A.PROJECT_SUMMARY, callPublished, configs),
        this.addSectionNodes('application.config.project.section.a.4', 0, APPLICATION_FORM.SECTION_A.PROJECT_A4, callPublished, configs),
      ]),
      this.addParentNode('application.config.project.section.b', 1, [
          this.addSectionNodes('application.config.project.section.b.1.1', 1, APPLICATION_FORM.SECTION_B.IDENTITY, callPublished, configs),
          this.addParentNode('application.config.project.section.b.1.2', 1, [
            this.addSectionNodes('application.config.project.section.b.1.2.main.address', 1, APPLICATION_FORM.SECTION_B.ADDRESS.MAIN, callPublished, configs),
            this.addSectionNodes('application.config.project.section.b.1.2.secondary.address', 1, APPLICATION_FORM.SECTION_B.ADDRESS.SECONDARY, callPublished, configs),
          ]),
          this.addSectionNodes('application.config.project.section.b.1.4', 1, APPLICATION_FORM.SECTION_B.CONTACT.LEGAL_REPRESENTATIVE, callPublished, configs),
          this.addSectionNodes('application.config.project.section.b.1.5', 1, APPLICATION_FORM.SECTION_B.CONTACT.CONTACT_PERSON, callPublished, configs),
          this.addSectionNodes('application.config.project.section.b.1.6', 1, APPLICATION_FORM.SECTION_B.MOTIVATION, callPublished, configs),
          this.addConfigurableParentNode('application.config.project.partner.budget.and.co.financing', 1, callPublished, configs, [
            this.addLeafNode(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_BUDGET_PERIODS, 1, callPublished, configs, false),
            this.addLeafNode(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_ADD_NEW_CONTRIBUTION_ORIGIN, 1, callPublished, configs, false),
            this.addSectionNodes('application.config.project.section.b.budget.staff.cost', 1, APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.STAFF_COST, callPublished, configs, false),
            this.addSectionNodes('application.config.project.section.b.budget.travel.and.accommodation', 1, APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.TRAVEL_AND_ACCOMMODATION, callPublished, configs, false),
            this.addSectionNodes('application.config.project.section.b.budget.external.expertise.and.services', 1, APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.EXTERNAL_EXPERTISE, callPublished, configs, false),
            this.addSectionNodes('application.config.project.section.b.budget.equipment', 1, APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.EQUIPMENT, callPublished, configs, false),
            this.addSectionNodes('application.config.project.section.b.budget.infrastructure.and.works', 1, APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.INFRASTRUCTURE_AND_WORKS, callPublished, configs, false),
            this.addSectionNodes('application.config.project.section.b.budget.unit.costs', 1, APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.UNIT_COSTS, callPublished, configs, false),
          ]),
          this.addSectionNodes('application.config.project.partner.state.aid', 1,  APPLICATION_FORM.SECTION_B.STATE_AID, callPublished, configs),
          this.addLeafNode(APPLICATION_FORM.SECTION_B.PARTNER_ASSOCIATED_ORGANIZATIONS, 1, callPublished, configs)
        ]
      ),
      this.addParentNode('application.config.project.section.c', 2, [
        this.addSectionNodes('application.config.project.section.c.1', 2, APPLICATION_FORM.SECTION_C.PROJECT_OVERALL_OBJECTIVE, callPublished, configs),
        this.addSectionNodes('application.config.project.section.c.2', 2, APPLICATION_FORM.SECTION_C.PROJECT_RELEVANCE_AND_CONTEXT, callPublished, configs),
        this.addSectionNodes('application.config.project.section.c.3', 2, APPLICATION_FORM.SECTION_C.PROJECT_PARTNERSHIP, callPublished, configs),
        this.addParentNode('application.config.project.section.c.4', 2, [
          this.addSectionNodes('application.config.project.section.c.4.objectives', 2, APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.OBJECTIVES, callPublished, configs),
          this.addParentNode('application.config.project.section.c.4.investments', 2, [
            this.addLeafNode(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.TITLE, 2, callPublished, configs),
            ...this.addLeafNodes(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.JUSTIFICATION, 2, callPublished, configs),
            ...this.addLeafNodes(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.ADDRESS, 2, callPublished, configs),
            this.addLeafNode(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.RISK, 2, callPublished, configs),
            this.addLeafNode(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.DOCUMENTATION, 2, callPublished, configs),
            ...this.addLeafNodes(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.OWNERSHIP, 2, callPublished, configs),
          ]),
          this.addSectionNodes('application.config.project.section.c.4.activities', 2, APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.ACTIVITIES, callPublished, configs),
          this.addSectionNodes('application.config.project.section.c.4.outputs', 2, APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.OUTPUTS, callPublished, configs),
        ]),

        this.addSectionNodes('application.config.project.section.c.5', 2, APPLICATION_FORM.SECTION_C.PROJECT_RESULT, callPublished, configs),
        this.addSectionNodes('application.config.project.section.c.7', 2, APPLICATION_FORM.SECTION_C.PROJECT_MANAGEMENT, callPublished, configs),
        this.addSectionNodes('application.config.project.section.c.8', 2, APPLICATION_FORM.SECTION_C.PROJECT_LONG_TERM_PLANS, callPublished, configs)
      ])
    ];
  }

  private addConfigurableParentNode(id: string, rootIndex: number, callPublished: boolean, configs: ApplicationFormFieldConfigurationDTO[], children: ApplicationFormFieldNode[]): ApplicationFormFieldNode {
    return {...this.addLeafNode(id, rootIndex, callPublished, configs), children};
  }

  private addParentNode(id: string, rootIndex: number, children: ApplicationFormFieldNode[]): ApplicationFormFieldNode {
    return {
      id,
      rootIndex,
      children,
      showStepToggle: false,
      showVisibilitySwitch: false
    };
  }

  private addSectionNodes(id: string, rootIndex: number, section: { [key: string]: string }, callPublished: boolean, configs: ApplicationFormFieldConfigurationDTO[], showStepToggle: boolean = true, showVisibilitySwitch: boolean = true): ApplicationFormFieldNode {
    return {
      id,
      rootIndex,
      children: this.addLeafNodes(section, rootIndex, callPublished, configs, showStepToggle, showVisibilitySwitch),
      showStepToggle: false,
      showVisibilitySwitch: false
    };
  }

  private addLeafNode(id: string, rootIndex: number, callPublished: boolean,
                      configs: ApplicationFormFieldConfigurationDTO[], showStepToggle: boolean = true, showVisibilitySwitch: boolean = true): ApplicationFormFieldNode {
    const config = configs?.find(conf => conf.id === id);
    return {
      id,
      visible: config?.visible || false,
      availableInStep: config?.availableInStep || AvailableInStepEnum.NONE,
      visibilityLocked: this.isConfigVisibilityLocked(callPublished, config),
      stepSelectionLocked: this.isConfigStepSelectionLocked(callPublished, config),
      rootIndex,
      showStepToggle,
      showVisibilitySwitch
    };
  }

  private addLeafNodes(applicationFormModel: { [key: string]: string }, rootIndex: number, callPublished: boolean,
                       configs: ApplicationFormFieldConfigurationDTO[], showStepToggle: boolean = true, showVisibilitySwitch: boolean = true): ApplicationFormFieldNode[] {
    return Object.keys(applicationFormModel).flatMap(key => [this.addLeafNode(applicationFormModel[key], rootIndex, callPublished, configs, showStepToggle, showVisibilitySwitch)]);
  }

  private isConfigVisibilityLocked(callPublished: boolean, config?: ApplicationFormFieldConfigurationDTO): boolean {
    return config?.visibilityLocked
      || (callPublished && !!config?.visible);
  }

  private isConfigStepSelectionLocked(callPublished: boolean, config?: ApplicationFormFieldConfigurationDTO): boolean {
    return config?.stepSelectionLocked
      || !config?.visible
      || (callPublished && config?.availableInStep === AvailableInStepEnum.STEPONEANDTWO);
  }

  private callHasTwoSteps(): Observable<boolean> {
    return this.callStore.call$
      .pipe(
        map(call => !!call.endDateTimeStep1)
      );
  }
}
