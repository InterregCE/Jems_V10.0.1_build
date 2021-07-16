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
import {APPLICATION_FORM} from '@project/application-form-model';
import AvailableInStepEnum = UpdateApplicationFormFieldConfigurationRequestDTO.AvailableInStepEnum;

@Injectable()
export class ApplicationFormConfigurationPageStore {
  static CONFIGURATION_DETAIL_PATH = '/applicationFormConfiguration';

  fieldConfigurations$: Observable<ApplicationFormFieldNode[]>;
  callHasTwoSteps$: Observable<boolean>;

  private savedConfigurations$ = new Subject<ApplicationFormFieldConfigurationDTO[]>();

  constructor(private applicationFormConfigurationService: ApplicationFormFieldConfigurationsService,
              private router: RoutingService,
              private callStore: CallStore) {
    this.fieldConfigurations$ = this.fieldConfigurations();
    this.callHasTwoSteps$ = this.callHasTwoSteps();
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
        this.addLeafNodes('application.config.project.section.a.1', 0, APPLICATION_FORM.SECTION_A.PROJECT_IDENTIFICATION, callPublished, configs)
      ]),
      this.addParentNode('application.config.project.section.b', 1, [
          this.addLeafNodes('application.config.project.section.b.1.1', 1, APPLICATION_FORM.SECTION_B.IDENTITY, callPublished, configs),
          this.addParentNode('application.config.project.section.b.1.2', 1, [
            this.addLeafNodes('application.config.project.section.b.1.2.main.address', 1, APPLICATION_FORM.SECTION_B.ADDRESS.MAIN, callPublished, configs),
            this.addLeafNodes('application.config.project.section.b.1.2.secondary.address', 1, APPLICATION_FORM.SECTION_B.ADDRESS.SECONDARY, callPublished, configs),
          ]),
          this.addLeafNodes('application.config.project.section.b.1.4', 1, APPLICATION_FORM.SECTION_B.CONTACT.LEGAL_REPRESENTATIVE, callPublished, configs),
          this.addLeafNodes('application.config.project.section.b.1.5', 1, APPLICATION_FORM.SECTION_B.CONTACT.CONTACT_PERSON, callPublished, configs),
          this.addLeafNodes('application.config.project.section.b.1.6', 1, APPLICATION_FORM.SECTION_B.MOTIVATION, callPublished, configs),
        ]
      ),
      this.addParentNode('application.config.project.section.c', 2, [
        this.addLeafNodes('application.config.project.section.c.1', 2, APPLICATION_FORM.SECTION_C.PROJECT_OVERALL_OBJECTIVE, callPublished, configs),
        this.addLeafNodes('application.config.project.section.c.2', 2, APPLICATION_FORM.SECTION_C.PROJECT_RELEVANCE_AND_CONTEXT, callPublished, configs),
        this.addLeafNodes('application.config.project.section.c.3', 2, APPLICATION_FORM.SECTION_C.PROJECT_PARTNERSHIP, callPublished, configs),
        this.addLeafNodes('application.config.project.section.c.5', 2, APPLICATION_FORM.SECTION_C.PROJECT_RESULT, callPublished, configs),
        this.addLeafNodes('application.config.project.section.c.7', 2, APPLICATION_FORM.SECTION_C.PROJECT_MANAGEMENT, callPublished, configs),
        this.addLeafNodes('application.config.project.section.c.8', 2, APPLICATION_FORM.SECTION_C.PROJECT_LONG_TERM_PLANS, callPublished, configs)
      ])
    ];
  }

  private addParentNode(id: string, rootIndex: number, children: ApplicationFormFieldNode[]): ApplicationFormFieldNode {
    return {
      id,
      rootIndex,
      children
    };
  }

  private addLeafNodes(id: string, rootIndex: number, section: { [key: string]: string }, callPublished: boolean, configs: ApplicationFormFieldConfigurationDTO[]): ApplicationFormFieldNode {
    return {
      id,
      rootIndex,
      children: this.getConfiguration(section, rootIndex, callPublished, configs)
    };
  }

  private getConfiguration(applicationFormModel: { [key: string]: string }, rootIndex: number, callPublished: boolean,
                           configs: ApplicationFormFieldConfigurationDTO[]): ApplicationFormFieldNode[] {
    const result: ApplicationFormFieldNode[] = [];
    Object.keys(applicationFormModel).forEach(key => {
      const id = applicationFormModel[key];
      const config = configs?.find(conf => conf.id === id);
      result.push({
        id,
        isVisible: config?.visible || false,
        availableInStep: config?.availableInStep || AvailableInStepEnum.NONE,
        isVisibilityLocked: this.isConfigVisibilityLocked(callPublished, config),
        isStepSelectionLocked: this.isConfigStepSelectionLocked(callPublished, config),
        rootIndex
      });
    });
    return result;
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
