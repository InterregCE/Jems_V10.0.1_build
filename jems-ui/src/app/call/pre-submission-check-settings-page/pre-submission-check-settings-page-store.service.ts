import {Injectable} from '@angular/core';
import {CallDetailDTO, PluginInfoDTO} from '@cat/api';
import {Observable} from 'rxjs';
import {CallStore} from '../services/call-store.service';
import {map} from 'rxjs/operators';

import {PluginStore} from '@common/services/plugin-store.service';
import TypeEnum = PluginInfoDTO.TypeEnum;

@Injectable()
export class PreSubmissionCheckSettingsPageStore {

  preSubmissionCheckPlugins: Observable<PluginInfoDTO[]>;
  callPreSubmissionCheckPluginKey$: Observable<string>;
  callIsEditable$: Observable<boolean>;

  constructor(private pluginStore: PluginStore,
              private callStore: CallStore) {
    this.preSubmissionCheckPlugins = this.pluginStore.getPluginListByType(TypeEnum.PRESUBMISSIONCHECK);
    this.callIsEditable$ = this.callStore.callIsEditable$;
    this.callPreSubmissionCheckPluginKey$ = this.callStore.call$.pipe(
      map(it => it.preSubmissionCheckPluginKey)
    );
  }

  save(pluginKey: string): Observable<CallDetailDTO> {
    return this.callStore.savePreSubmissionCheckSettings(pluginKey);
  }

}
