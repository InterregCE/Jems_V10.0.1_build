import {Injectable} from '@angular/core';
import {CallDetailDTO, PluginInfoDTO, PreSubmissionPluginsDTO} from '@cat/api';
import {Observable} from 'rxjs';
import {CallStore} from '../services/call-store.service';
import {map} from 'rxjs/operators';

import {PluginStore} from '@common/services/plugin-store.service';
import {PluginKeys} from './plugin-keys';
import TypeEnum = PluginInfoDTO.TypeEnum;

@Injectable()
export class PreSubmissionCheckSettingsPageStore {

  preSubmissionCheckPlugins: Observable<PluginInfoDTO[]>;
  reportPartnerCheckPlugins: Observable<PluginInfoDTO[]>;
  controlReportSamplingCheckPlugins: Observable<PluginInfoDTO[]>;
  reportProjectCheckPlugins: Observable<PluginInfoDTO[]>;
  controlReportPartnerCheckPlugins: Observable<PluginInfoDTO[]>;
  callIsEditable$: Observable<boolean>;
  callHasTwoSteps$: Observable<boolean>;
  pluginKeys$: Observable<PluginKeys>;

  constructor(private pluginStore: PluginStore,
              private callStore: CallStore) {
    this.preSubmissionCheckPlugins = this.pluginStore.getPluginListByType(TypeEnum.PRESUBMISSIONCHECK);
    this.reportPartnerCheckPlugins = this.pluginStore.getPluginListByType(TypeEnum.REPORTPARTNERCHECK);
    this.controlReportPartnerCheckPlugins = this.pluginStore.getPluginListByType(TypeEnum.PARTNERCONTROLREPORTCHECK);
    this.reportProjectCheckPlugins = this.pluginStore.getPluginListByType(TypeEnum.REPORTPROJECTCHECK);
    this.controlReportSamplingCheckPlugins = this.pluginStore.getPluginListByType(TypeEnum.PARTNERCONTROLRISKBASEDSAMPLING);
    this.callIsEditable$ = this.callStore.callIsEditable$;
    this.callHasTwoSteps$ = this.callStore.call$.pipe(map(call => !!call.endDateTimeStep1));
    this.pluginKeys$ = this.callStore.call$.pipe(map((call) => ({
      pluginKey: call.preSubmissionCheckPluginKey,
      firstStepPluginKey: call.firstStepPreSubmissionCheckPluginKey,
      reportPartnerCheckPluginKey: call.reportPartnerCheckPluginKey,
      reportProjectCheckPluginKey: call.reportProjectCheckPluginKey,
      callHasTwoSteps: !!call.endDateTimeStep1,
      controlReportPartnerCheckPluginKey: call.controlReportPartnerCheckPluginKey,
      controlReportSamplingCheckPluginKey: call.controlReportSamplingCheckPluginKey
    })));
  }

  save(pluginKeys: PreSubmissionPluginsDTO): Observable<CallDetailDTO> {
    return this.callStore.savePreSubmissionCheckSettings(pluginKeys);
  }
}
