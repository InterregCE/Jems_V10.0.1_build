import {Pipe, PipeTransform} from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {Observable, of} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {ProjectCallSettingsDTO} from '@cat/api';
import {CallStore} from '../../call/services/call-store.service';
import {Router} from '@angular/router';
import {ProjectPaths} from '@project/common/project-util';
import CallTypeEnum = ProjectCallSettingsDTO.CallTypeEnum;

@Pipe({name: 'adaptTranslationKeyByCallType'})
export class AdaptTranslationKeyByCallTypePipe implements PipeTransform {
  constructor(
    private readonly callStore: CallStore,
    private readonly projectStore: ProjectStore,
    private router: Router) {
  }

  transform(translationKey: string): Observable<string> {
    if (translationKey === null || translationKey.length === 0) {
      return of('');
    }

    return this.getCallType()
        .pipe(map(callType => callType === CallTypeEnum.STANDARD
          ? translationKey
          : `${callType.toLocaleLowerCase()}.${translationKey}`));
  }

  private getCallType(): Observable<CallTypeEnum> {
    // find out if coming from Call or Project
    return of(this.router.url.startsWith(ProjectPaths.PROJECT_DETAIL_PATH))
      .pipe(switchMap(isProjectRoute => {
        return isProjectRoute ? this.projectStore.project$
          .pipe(map(project => project.callSettings.callType))
          : this.callStore.callType$;
      }));
  }
}
