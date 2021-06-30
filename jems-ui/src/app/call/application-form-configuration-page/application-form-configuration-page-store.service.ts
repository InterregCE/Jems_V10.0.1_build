import {Injectable} from '@angular/core';
import {
  ApplicationFormFieldConfigurationDTO,
  ApplicationFormFieldConfigurationsService, CallDetailDTO,
  UpdateApplicationFormFieldConfigurationRequestDTO
} from '@cat/api';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {RoutingService} from '@common/services/routing.service';
import {CallStore} from '../services/call-store.service';
import {map, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {filter} from 'rxjs/internal/operators';
import {Log} from '@common/utils/log';
import {ApplicationFormFieldNode} from './application-form-field-node';
import AvailableInStepEnum = UpdateApplicationFormFieldConfigurationRequestDTO.AvailableInStepEnum;
import {ApplicationFormFieldId} from '@project/application-form-field-id';

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
        map(([configs, callIsPublished]) => this.getConfigurations(callIsPublished, configs))
      );
  }

  private getConfigurations(callPublished: boolean,
                            configs: ApplicationFormFieldConfigurationDTO[]): ApplicationFormFieldNode[] {
    return [
      {
        id: 'project.application.form.section.part.a',
        parentIndex: 0,
        children: [{
          id: 'project.application.form.section.part.a.subsection.one',
          parentIndex: 0,
          children: [
            this.getConfiguration(ApplicationFormFieldId.PROJECT_ACRONYM, 0, callPublished, configs),
            this.getConfiguration(ApplicationFormFieldId.PROJECT_TITLE, 0, callPublished, configs),
            this.getConfiguration(ApplicationFormFieldId.PROJECT_DURATION, 0, callPublished, configs),
            this.getConfiguration(ApplicationFormFieldId.PROJECT_PRIORITY, 0, callPublished, configs),
            this.getConfiguration(ApplicationFormFieldId.PROJECT_OBJECTIVE, 0, callPublished, configs)
          ]
        }]
      },
      {
        id: 'project.application.form.section.part.c',
        parentIndex: 1,
        children: [{
          id: 'project.application.form.section.part.c.subsection.five',
          parentIndex: 1,
          children: [
            this.getConfiguration(ApplicationFormFieldId.PROJECT_RESULTS_PROGRAMME_RESULT_INDICATOR_AMD_MEASUREMENT_UNIT, 1, callPublished, configs),
            this.getConfiguration(ApplicationFormFieldId.PROJECT_RESULTS_TARGET_VALUE, 1, callPublished, configs),
            this.getConfiguration(ApplicationFormFieldId.PROJECT_RESULTS_DELIVERY_PERIOD, 1, callPublished, configs),
            this.getConfiguration(ApplicationFormFieldId.PROJECT_RESULTS_DESCRIPTION, 1, callPublished, configs),
          ]
        }]
      }
    ];
  }

  private getConfiguration(id: string, parentIndex: number, callPublished: boolean,
                           configs: ApplicationFormFieldConfigurationDTO[]): ApplicationFormFieldNode {
    const config = configs?.find(conf => conf.id === id);
    return {
      id,
      isVisible: config?.isVisible || false,
      availableInStep: config?.availableInStep || AvailableInStepEnum.NONE,
      isVisibilityLocked: this.isConfigVisibilityLocked(callPublished, config),
      isStepSelectionLocked: this.isConfigStepSelectionLocked(callPublished, config),
      parentIndex
    };
  }

  private isConfigVisibilityLocked(callPublished: boolean, config?: ApplicationFormFieldConfigurationDTO): boolean {
    return config?.isVisibilityLocked
      || (callPublished && !!config?.isVisible);
  }

  private isConfigStepSelectionLocked(callPublished: boolean, config?: ApplicationFormFieldConfigurationDTO): boolean {
    return config?.isStepSelectionLocked
      || !config?.isVisible
      || (callPublished && config?.availableInStep === AvailableInStepEnum.STEPONEANDTWO);
  }

  private callHasTwoSteps(): Observable<boolean> {
    return this.callStore.call$
      .pipe(
        map(call => !!call.endDateTimeStep1)
      );
  }
}
