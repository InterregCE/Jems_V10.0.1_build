import {Injectable} from '@angular/core';
import {PluginInfoDTO, PluginService} from '@cat/api';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Observable, of} from 'rxjs';
import {tap} from 'rxjs/operators';


@UntilDestroy()
@Injectable({providedIn: 'root'})
export class PluginStore {

  pluginsMap = new Map<PluginInfoDTO.TypeEnum, PluginInfoDTO[] | undefined>();

  constructor(private pluginService: PluginService) {
  }

  fetchPluginList(type: PluginInfoDTO.TypeEnum): Observable<PluginInfoDTO[]> {
    return this.pluginService.getAvailablePluginList(type).pipe(
      tap(pluginList => this.pluginsMap.set(type, pluginList)),
      untilDestroyed(this)
    );
  }

  getPluginListByType(type: PluginInfoDTO.TypeEnum): Observable<PluginInfoDTO[]> {
    const localPlugins = this.pluginsMap.get(type);
    if ( localPlugins === undefined) {
      return this.fetchPluginList(type);
    }else {
      return of(localPlugins);
    }
  }
}
