import {ChangeDetectionStrategy, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {
  Content,
  getEndDateFromPeriod,
  getGroups,
  getItems,
  getOptions,
  getInputTranslations,
  START_DATE,
  TRANSLATABLE_GROUP_TYPES,
} from './project-timeplan.utils';
import {ProjectApplicationFormSidenavService} from '../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {InputTranslation, OutputProjectPeriod} from '@cat/api';
import {Timeline} from 'vis-timeline';
import {ProjectTimeplanPageStore} from './project-timeplan-page-store.service';
import {map, tap} from 'rxjs/operators';
import {combineLatest, Observable} from 'rxjs';
import {DataSet} from 'vis-data/peer';
import {Alert} from '@common/components/forms/alert';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-project-timeplan-page',
  templateUrl: './project-timeplan-page.component.html',
  styleUrls: ['./project-timeplan-page.component.scss'],
  providers: [ProjectTimeplanPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectTimeplanPageComponent implements OnInit {
  Alert = Alert;
  timeline: Timeline;

  @ViewChild('visualization', {static: true})
  visualization: ElementRef;

  data$: Observable<{
    translations: { [language: string]: Content[]; },
    groups: DataSet<any>,
    items: DataSet<any>,
    periods: OutputProjectPeriod[],
    language: string
  }>;

  constructor(private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private translateService: TranslateService,
              public pageStore: ProjectTimeplanPageStore) {
  }

  ngOnInit(): void {
    const projectData$ = combineLatest([
      this.pageStore.workPackages$,
      this.pageStore.projectResults$,
      this.pageStore.periods$,
    ])
      .pipe(
        map(([workPackages, results, periods]) => ({
          translations: getInputTranslations({workPackages, results}, this.translateService),
          groups: getGroups({workPackages, results}),
          items: getItems({workPackages, results}),
          periods,
        })),
        tap(data => this.createVisualization(data))
      );

    this.data$ = combineLatest([projectData$, this.pageStore.language$])
      .pipe(
        tap(([data, language]) => this.updateLanguageSelection(data.groups, data.translations, language)),
        map(([data, language]) => ({...data, language})),
      );
  }

  private createVisualization(data: any): boolean {
    if (!data.periods.length) {
      return false;
    }
    const endDate = getEndDateFromPeriod(data.periods.length).toISOString();
    const options = getOptions(this.translateService, {max: endDate});
    const doc = this.visualization?.nativeElement;
    if (doc) {
      this.timeline = new Timeline(doc, data.items, options);
      this.timeline.setGroups(data.groups);
      this.timeline.setWindow(START_DATE, endDate);
      return true;
    }
    return false;
  }

  /**
   * Call this if different language has been selected.
   */
  private updateLanguageSelection(groups: DataSet<any>, translations: { [language: string]: Content[]; }, language: InputTranslation.LanguageEnum | string): void {
    const idsToTranslate = groups.get()
      .filter(group => TRANSLATABLE_GROUP_TYPES.includes(group.data.type))
      .map(group => group.id);

    const alreadyPreparedTranslatedIds = translations[language]?.map((group: any) => group.id) || [];
    const toEmptyIds = idsToTranslate.filter(id => !alreadyPreparedTranslatedIds.includes(id));

    // if some translations are missing for particular language, we need to reset them to EMPTY_STRING (toEmptyIds)
    groups.update(
      (translations[language] || [])
        .concat(toEmptyIds.map(groupId => ({id: groupId, content: '', title: ''} as Content)))
    );
  }

}
