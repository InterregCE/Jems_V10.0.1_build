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
import {filter, map, switchMap, tap, withLatestFrom} from 'rxjs/operators';
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
  callType$: Observable<CallDetailDTO.TypeEnum>;

  private savedConfigurations$ = new Subject<ApplicationFormFieldConfigurationDTO[]>();

  constructor(private applicationFormConfigurationService: ApplicationFormFieldConfigurationsService,
              private router: RoutingService,
              private callStore: CallStore) {
    this.fieldConfigurations$ = this.fieldConfigurations();
    this.callHasTwoSteps$ = this.callHasTwoSteps();
    this.callIsEditable$ = this.callStore.callIsEditable$;
    this.callType$ = this.callStore.callType$.asObservable();
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
        withLatestFrom(this.callStore.callIsPublished$, this.callStore.callType$),
        map(([configs, callIsPublished, callType]) => {
          if (callType === CallDetailDTO.TypeEnum.SPF) {
           return this.getSPFApplicationFormFieldNodeList(this.getApplicationFormFieldNodeList(callIsPublished,configs));
          }
          return this.getStandardApplicationFormFieldNodeList(this.getApplicationFormFieldNodeList(callIsPublished, configs));
        })
      );
  }

  private getApplicationFormFieldNodeList(callPublished: boolean, configs: ApplicationFormFieldConfigurationDTO[]): ApplicationFormFieldNode[] {
    return [
      this.getSectionAFormFieldNodeList(callPublished,configs),
      this.getSectionBFormFieldNodeList(callPublished,configs),
      this.getSectionCFormFieldNodeList(callPublished,configs),
    ];
  }

  private getSPFApplicationFormFieldNodeList(nodes: ApplicationFormFieldNode[]) {
    return nodes.filter(function removeSPFHiddenFields(node): boolean {
        if (!node.isHiddenInSpfCall) {
          if (node.children) {
            node.children = node.children.filter(removeSPFHiddenFields);
          }
          return true;
        }
        return false;
      }
    );
  }


  private getStandardApplicationFormFieldNodeList(nodes: ApplicationFormFieldNode[]) {
    return nodes.filter(function removeStandardCallHiddenFields(node): boolean {
      if (!node.isHiddenInStandardCall) {
        if (node.children) {
          node.children = node.children.filter(removeStandardCallHiddenFields);
        }
        return true;
      }
      return false;
    });
  }


  private addConfigurableParentNode(
    id: string,
    rootIndex: number,
    callPublished: boolean,
    configs: ApplicationFormFieldConfigurationDTO[],
    children: ApplicationFormFieldNode[]
  ): ApplicationFormFieldNode {
    return {...this.addLeafNode(id, rootIndex, callPublished, configs), children};
  }

  private static addParentNode(id: string, rootIndex: number, children: ApplicationFormFieldNode[], isHiddenInSpfCall = false, isHiddenInStandardCall = false): ApplicationFormFieldNode {
    return {
      id,
      rootIndex,
      children,
      showStepToggle: false,
      showVisibilitySwitch: false,
      isHiddenInSpfCall,
      isHiddenInStandardCall
    };
  }

  private addSectionNodes(id: string, rootIndex: number, section: { [key: string]: string }, callPublished: boolean, configs: ApplicationFormFieldConfigurationDTO[], showStepToggle = true, showVisibilitySwitch = true, isHiddenInSpfCall = false,  isHiddenInStandardCall = false): ApplicationFormFieldNode {
    return {
      id,
      rootIndex,
      children: this.addLeafNodes(section, rootIndex, callPublished, configs, showStepToggle, showVisibilitySwitch),
      showStepToggle: false,
      showVisibilitySwitch: false,
      isHiddenInSpfCall,
      isHiddenInStandardCall
    };
  }

  private addLeafNode(id: string, rootIndex: number, callPublished: boolean,
                      configs: ApplicationFormFieldConfigurationDTO[], showStepToggle = true, showVisibilitySwitch = true, isHiddenInSpfCall = false,  isHiddenInStandardCall = false): ApplicationFormFieldNode {
    const config = configs?.find(conf => conf.id === id);
    return {
      id,
      visible: config?.visible || false,
      availableInStep: config?.availableInStep || AvailableInStepEnum.NONE,
      visibilityLocked: ApplicationFormConfigurationPageStore.isConfigVisibilityLocked(callPublished, config),
      stepSelectionLocked: ApplicationFormConfigurationPageStore.isConfigStepSelectionLocked(callPublished, config),
      rootIndex,
      showStepToggle,
      showVisibilitySwitch,
      isHiddenInSpfCall,
      isHiddenInStandardCall
    };
  }

  private addLeafNodes(applicationFormModel: { [key: string]: string }, rootIndex: number, callPublished: boolean,
                       configs: ApplicationFormFieldConfigurationDTO[], showStepToggle = true, showVisibilitySwitch = true, isHiddenInSpf = false): ApplicationFormFieldNode[] {
    return Object.keys(applicationFormModel).flatMap(key => [this.addLeafNode(applicationFormModel[key], rootIndex, callPublished, configs, showStepToggle, showVisibilitySwitch, isHiddenInSpf)]);
  }

  private static isConfigVisibilityLocked(callPublished: boolean, config?: ApplicationFormFieldConfigurationDTO): boolean {
    return config?.visibilityLocked
      || (callPublished && !!config?.visible);
  }

  private static isConfigStepSelectionLocked(callPublished: boolean, config?: ApplicationFormFieldConfigurationDTO): boolean {
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

  private getSectionAFormFieldNodeList(callPublished: boolean, configs: ApplicationFormFieldConfigurationDTO[]): ApplicationFormFieldNode {
    return ApplicationFormConfigurationPageStore.addParentNode('application.config.project.section.a', 0, [
      this.addSectionNodes('application.config.project.section.a.1', 0, APPLICATION_FORM.SECTION_A.PROJECT_IDENTIFICATION, callPublished, configs),
      this.addSectionNodes('application.config.project.section.a.2', 0, APPLICATION_FORM.SECTION_A.PROJECT_SUMMARY, callPublished, configs),
      this.addSectionNodes('application.config.project.section.overview.tables', 0, APPLICATION_FORM.SECTION_A.PROJECT_OVERVIEW_TABLES, callPublished, configs),
    ]);
  }

  private getSectionBFormFieldNodeList(callPublished: boolean, configs: ApplicationFormFieldConfigurationDTO[]): ApplicationFormFieldNode {
    return ApplicationFormConfigurationPageStore.addParentNode('application.config.project.section.b', 1, [
        ApplicationFormConfigurationPageStore.addParentNode('application.config.project.section.b.1.1', 1,[
          this.addLeafNode('application.config.project.partner.role',1,callPublished, configs, true, true, true),
          this.addLeafNode('application.config.project.partner.name',1,callPublished, configs),
          this.addLeafNode('application.config.project.organization.original.name',1,callPublished, configs),
          this.addLeafNode('application.config.project.organization.english.name',1,callPublished, configs),
          this.addLeafNode('application.config.project.organization.department',1,callPublished, configs),
          this.addLeafNode('application.config.project.partner.type',1,callPublished, configs),
          this.addLeafNode('application.config.project.partner.sub.type',1,callPublished, configs),
          this.addLeafNode('application.config.project.partner.nace.group.level',1,callPublished, configs),
          this.addLeafNode('application.config.project.partner.other.identifier.number.and.description',1,callPublished, configs),
          this.addLeafNode('application.config.project.partner.pic',1,callPublished, configs),
          this.addLeafNode('application.config.project.partner.legal.status',1,callPublished, configs),
          this.addLeafNode('application.config.project.partner.vat',1,callPublished, configs),
          this.addLeafNode('application.config.project.partner.recoverVat',1,callPublished, configs),
        ]),
        ApplicationFormConfigurationPageStore.addParentNode('application.config.project.section.b.1.2', 1, [
          this.addSectionNodes('application.config.project.section.b.1.2.main.address', 1, APPLICATION_FORM.SECTION_B.ADDRESS.MAIN, callPublished, configs),
          this.addSectionNodes('application.config.project.section.b.1.2.secondary.address', 1, APPLICATION_FORM.SECTION_B.ADDRESS.SECONDARY, callPublished, configs),
        ]),
        this.addSectionNodes('application.config.project.section.b.1.4', 1, APPLICATION_FORM.SECTION_B.CONTACT.LEGAL_REPRESENTATIVE, callPublished, configs),
        this.addSectionNodes('application.config.project.section.b.1.5', 1, APPLICATION_FORM.SECTION_B.CONTACT.CONTACT_PERSON, callPublished, configs),
        this.addSectionNodes('application.config.project.section.b.1.6', 1, APPLICATION_FORM.SECTION_B.MOTIVATION, callPublished, configs),
        this.getBudgetAndCoFinancingSubSection(callPublished, configs),
        this.addSectionNodes('application.config.project.partner.state.aid', 1, APPLICATION_FORM.SECTION_B.STATE_AID, callPublished, configs),
        this.addLeafNode(APPLICATION_FORM.SECTION_B.PARTNER_ASSOCIATED_ORGANIZATIONS, 1, callPublished, configs)
      ]
    );
  }

  private getSectionCFormFieldNodeList(callPublished: boolean, configs: ApplicationFormFieldConfigurationDTO[]): ApplicationFormFieldNode {
    return ApplicationFormConfigurationPageStore.addParentNode('application.config.project.section.c', 2, [
      this.addSectionNodes('application.config.project.section.c.1', 2, APPLICATION_FORM.SECTION_C.PROJECT_OVERALL_OBJECTIVE, callPublished, configs),
      this.getProjectRelevanceAndContextSubSection(callPublished, configs),
      this.addSectionNodes('application.config.project.section.c.3', 2, APPLICATION_FORM.SECTION_C.PROJECT_PARTNERSHIP, callPublished, configs),
      ApplicationFormConfigurationPageStore.addParentNode('application.config.project.section.c.4', 2, [
        this.addSectionNodes('application.config.project.section.c.4.objectives', 2, APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.OBJECTIVES, callPublished, configs),
        ApplicationFormConfigurationPageStore.addParentNode('application.config.project.section.c.4.investments', 2, [
          this.addLeafNode(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.TITLE, 2, callPublished, configs, true, true, true),
          this.addLeafNode(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.EXPECTED_DELIVERY_PERIOD, 2, callPublished, configs, true, true, true),
          this.addLeafNode('application.config.project.investment.why.is.investment.needed',2, callPublished, configs),
          this.addLeafNode('application.config.project.investment.cross.border.transnational.relevance.of.investment',2, callPublished, configs, true, true, true),
          this.addLeafNode('application.config.project.investment.who.is.benefiting',2, callPublished, configs, true, true, true),
          this.addLeafNode('application.config.project.investment.pilot.clarification',2, callPublished, configs, true, true, true),
          ...this.addLeafNodes(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.ADDRESS, 2, callPublished, configs,true, true, true),
          this.addLeafNode(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.RISK, 2, callPublished, configs, true, true, true),
          ...this.addLeafNodes(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.DOCUMENTATION, 2, callPublished, configs, true, true, true),
          ...this.addLeafNodes(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.OWNERSHIP, 2, callPublished, configs, true, true, true),
        ]),
        this.addSectionNodes('application.config.project.section.c.4.activities', 2, APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.ACTIVITIES, callPublished, configs),
        this.addSectionNodes('application.config.project.section.c.4.outputs', 2, APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.OUTPUTS, callPublished, configs),
      ]),

      this.addSectionNodes('application.config.project.section.c.5', 2, APPLICATION_FORM.SECTION_C.PROJECT_RESULT, callPublished, configs),
      this.addSectionNodes('application.config.project.section.c.7', 2, APPLICATION_FORM.SECTION_C.PROJECT_MANAGEMENT, callPublished, configs),
      this.addSectionNodes('application.config.project.section.c.8', 2, APPLICATION_FORM.SECTION_C.PROJECT_LONG_TERM_PLANS, callPublished, configs)
    ]);
  }

  private getProjectRelevanceAndContextSubSection(callPublished: boolean, configs: ApplicationFormFieldConfigurationDTO[]): ApplicationFormFieldNode{
    return ApplicationFormConfigurationPageStore.addParentNode('application.config.project.section.c.2', 2,[
      this.addLeafNode('application.config.project.territorial.challenges', 2, callPublished, configs, true, true),
      this.addLeafNode('application.config.project.how.are.challenges.and.opportunities.tackled', 2, callPublished, configs, true, true),
      this.addLeafNode('application.config.project.why.is.cooperation.needed', 2, callPublished, configs, true, true),
      this.addLeafNode('application.config.project.target.group', 2, callPublished, configs, true, true),
      this.addLeafNode('application.config.project.spf.recipient.group', 2, callPublished, configs, true, true, false, true),
      this.addLeafNode('application.config.project.strategy.contribution', 2, callPublished, configs, true, true ),
      this.addLeafNode('application.config.project.synergies', 2, callPublished, configs, true, true ),
      this.addLeafNode('application.config.project.how.builds.project.on.available.knowledge', 2, callPublished, configs, true, true),
    ]);
  }

  private getBudgetAndCoFinancingSubSection(callPublished: boolean, configs: ApplicationFormFieldConfigurationDTO[]): ApplicationFormFieldNode{
    return this.addConfigurableParentNode('application.config.project.partner.budget.and.co.financing', 1, callPublished, configs, [
      this.addLeafNode(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_BUDGET_PERIODS, 1, callPublished, configs, false),
      this.addLeafNode(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_ADD_NEW_CONTRIBUTION_ORIGIN, 1, callPublished, configs, false),
      this.addLeafNode(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PROJECT_LUMP_SUMS_DESCRIPTION, 1, callPublished, configs, false),
      this.addSectionNodes('application.config.project.section.b.budget.staff.cost',
        1, APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.STAFF_COST, callPublished, configs, false),
      this.addSectionNodes('application.config.project.section.b.budget.travel.and.accommodation',
        1, APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.TRAVEL_AND_ACCOMMODATION, callPublished, configs, false),
      this.addSectionNodes('application.config.project.section.b.budget.external.expertise.and.services',
        1, APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.EXTERNAL_EXPERTISE, callPublished, configs, false),
      this.addSectionNodes('application.config.project.section.b.budget.equipment',
        1, APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.EQUIPMENT, callPublished, configs, false),
      this.addSectionNodes('application.config.project.section.b.budget.infrastructure.and.works',
        1, APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.INFRASTRUCTURE_AND_WORKS, callPublished, configs, false, true, true),
      this.addSectionNodes('application.config.project.section.b.budget.unit.costs',
        1, APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.UNIT_COSTS, callPublished, configs, false),
      this.getBudgetSpfCostsSubSection(callPublished, configs)
    ]);
  }

  private getBudgetSpfCostsSubSection(callPublished: boolean, configs: ApplicationFormFieldConfigurationDTO[]): ApplicationFormFieldNode {
    return ApplicationFormConfigurationPageStore.addParentNode('application.config.project.section.b.budget.spf.cost', 1,[
      this.addLeafNode('application.config.project.partner.budget.spf.description',1, callPublished, configs, false, true, false, true),
      this.addLeafNode('application.config.project.partner.budget.spf.comments',1, callPublished, configs, false, true, false, true),
      this.addLeafNode('application.config.project.partner.budget.spf.unit.type.and.number.of.units',1, callPublished, configs, false, true, false, true),
      this.addLeafNode('application.config.project.partner.budget.spf.price.per.unit',1, callPublished, configs, false, true, false, true)
    ], false, true);
  }

}
