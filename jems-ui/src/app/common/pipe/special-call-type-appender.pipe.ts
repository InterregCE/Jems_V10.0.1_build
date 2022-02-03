import {Pipe, PipeTransform} from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {Observable, of} from 'rxjs';
import {map} from 'rxjs/operators';
import {ProjectCallSettingsDTO} from '@cat/api';
import CallTypeEnum = ProjectCallSettingsDTO.CallTypeEnum;

@Pipe({name: 'specialCallTypeAppender'})
export class SpecialCallTypeAppenderPipe implements PipeTransform {
  constructor(private readonly projectStore: ProjectStore) {
  }

  transform(translationKey: string): Observable<string> {
    if (translationKey === null || translationKey.length === 0) {
      return of('');
    }

    return this.projectStore.project$
      .pipe(map(project => project.callSettings.callType === CallTypeEnum.STANDARD
        ? translationKey
        : `${project.callSettings.callType.toLocaleLowerCase()}.${translationKey}`));
  }
}
