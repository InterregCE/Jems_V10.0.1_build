import {Injectable} from '@angular/core';
import {CallDetailDTO, PluginInfoDTO, PreSubmissionPluginsDTO} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {CallStore} from '../services/call-store.service';
import {map} from 'rxjs/operators';

import {PluginStore} from '@common/services/plugin-store.service';
import {PluginKeys} from './plugin-keys';
import TypeEnum = PluginInfoDTO.TypeEnum;

@Injectable()
export class PreSubmissionCheckSettingsPageStore {

  preSubmissionCheckPlugins: Observable<PluginInfoDTO[]>;
  callIsEditable$: Observable<boolean>;
  callHasTwoSteps$: Observable<boolean>;
  pluginKeys$: Observable<PluginKeys>;

  constructor(private pluginStore: PluginStore,
              private callStore: CallStore) {
    this.preSubmissionCheckPlugins = combineLatest([this.pluginStore.getPluginListByType(TypeEnum.PRESUBMISSIONCHECK), this.callStore.callType$]).pipe(
      map(([plugins, callType]) => (callType === CallDetailDTO.TypeEnum.SPF) ?
        plugins.filter(plugin =>  plugin.key === 'jems-pre-condition-check-blocked') : plugins)
    );
    this.callIsEditable$ = this.callStore.callIsEditable$;
    this.callHasTwoSteps$ = this.callStore.call$.pipe(map(call => !!call.endDateTimeStep1));
    this.pluginKeys$ = this.callStore.call$.pipe(map((call) => ({
      pluginKey: call.preSubmissionCheckPluginKey,
      firstStepPluginKey: call.firstStepPreSubmissionCheckPluginKey,
      callHasTwoSteps: !!call.endDateTimeStep1
    })));
  }

  save(pluginKeys: PreSubmissionPluginsDTO): Observable<CallDetailDTO> {
    return this.callStore.savePreSubmissionCheckSettings(pluginKeys);
  }
}


