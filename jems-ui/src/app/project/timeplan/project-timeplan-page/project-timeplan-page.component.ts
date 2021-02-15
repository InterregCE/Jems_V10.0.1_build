import {ChangeDetectionStrategy, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {
  Content,
  getEndDateFromPeriod,
  getGroupName,
  getGroups,
  getItems,
  getOptions,
  getTranslations,
  shouldGroupBeTranslated,
  START_DATE,
} from './project-timeplan.utils';
import {ProjectApplicationFormSidenavService} from '../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {InputTranslation, OutputProjectPeriod} from '@cat/api';
import {Timeline} from 'vis-timeline';
import {ProjectTimeplanPageStore} from './project-timeplan-page-store.service';
import {map, tap} from 'rxjs/operators';
import {combineLatest, Observable} from 'rxjs';
import {DataSet} from 'vis-data/peer';
import {Alert} from '@common/components/forms/alert';

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
          translations: getTranslations({workPackages, results}),
          groups: getGroups({workPackages, results}),
          items: getItems({workPackages, results}),
          periods,
        })),
        tap(data => this.createVisualization(data))
      );

    this.data$ = combineLatest([projectData$, this.pageStore.language$])
      .pipe(
        tap(([data, language]) => this.updateLanguageSelection(data, language)),
        map(([data, language]) => ({...data, language})),
      );
  }

  private createVisualization(data: any): boolean {
    if (!data.periods.length) {
      return false;
    }
    const endDate = getEndDateFromPeriod(data.periods.length).toISOString();
    const options = getOptions({max: endDate});
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
  private updateLanguageSelection(data: any, language: InputTranslation.LanguageEnum | string): void {
    const translatedIds = data.translations[language]?.map((group: any) => group.id) || [];
    const allIds: number[] = data.groups.getIds().map((groupId: any) => Number(groupId));
    const toEmptyIds = allIds
      .filter(id => !translatedIds.includes(id))
      .filter(id => shouldGroupBeTranslated(id));

    // if some translations are missing for particular language, we need to reset them to EMPTY_STRING
    data.groups.update((data.translations[language] || [])
      .concat(toEmptyIds.map(groupId => ({id: groupId, content: getGroupName()} as Content)))
    );
  }

}
