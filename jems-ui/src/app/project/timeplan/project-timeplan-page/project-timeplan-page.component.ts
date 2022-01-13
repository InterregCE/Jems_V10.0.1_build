import {ChangeDetectionStrategy, Component, ElementRef, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Alert} from '@common/components/forms/alert';
import {TranslateService} from '@ngx-translate/core';
import {ProjectPeriodDTO} from '@cat/api';
import {Timeline} from 'vis-timeline';
import {DataSet} from 'vis-data/peer';
import {map, shareReplay, tap} from 'rxjs/operators';
import {combineLatest, Observable} from 'rxjs';

import {ProjectTimeplanPageStore} from './project-timeplan-page-store.service';
import {
  Content,
  getEndDateFromPeriod,
  getGroups,
  getInputTranslations,
  getItems,
  getOptions,
  isResult,
  RESULT_GROUP_TITLE_ID,
  START_DATE,
  TRANSLATABLE_GROUP_TYPES,
} from './project-timeplan.utils';
import {MultiLanguageGlobalService} from '@common/components/forms/multi-language-container/multi-language-global.service';

@Component({
  selector: 'app-project-timeplan-page',
  templateUrl: './project-timeplan-page.component.html',
  styleUrls: ['./project-timeplan-page.component.scss'],
  providers: [ProjectTimeplanPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectTimeplanPageComponent {
  Alert = Alert;
  timeline: Timeline;

  @ViewChild('visualization', {static: true})
  visualization: ElementRef;

  periodsUnavailable$: Observable<boolean>;
  workPackagesUnavailable$: Observable<boolean>;
  dataAvailable$: Observable<boolean>;

  constructor(private translateService: TranslateService,
              private multiLanguageGlobalService: MultiLanguageGlobalService,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              public pageStore: ProjectTimeplanPageStore) {
    const data$ = combineLatest([
      this.pageStore.workPackages$,
      this.pageStore.projectResults$,
      this.pageStore.periods$,
      this.multiLanguageGlobalService.activeInputLanguage$
    ])
      .pipe(
        map(([workPackages, results, periods, language]) => ({
          workPackages,
          results,
          timelineGroups: getGroups(workPackages, results),
          timelineItems: getItems(workPackages, results, this.translateService),
          timelineTranslations: getInputTranslations(workPackages)[language] || [],
          periods,
        })),
        tap(data => this.createVisualizationOrUpdateJustTranslations(data.periods, data.timelineItems, data.timelineGroups)),
        tap((data) => this.updateLanguageSelection(data.timelineGroups, data.timelineTranslations)),
        shareReplay(1)
      );

    this.periodsUnavailable$ = data$
      .pipe(
        map(data => !data.periods.length)
      );

    this.workPackagesUnavailable$ = data$
      .pipe(
        map(data => !data.workPackages.length && !data.results.length),
      );

    this.dataAvailable$ = data$
      .pipe(
        map(data => (!!data.workPackages.length || !!data.results.length) && !!data.periods.length),
      );
  }

  private createVisualizationOrUpdateJustTranslations(periods: ProjectPeriodDTO[], newItems: DataSet<any>, groups: DataSet<any>): void {
    if (!periods.length || !groups.length) {
      return;
    }
    if (!this.timeline) {
      this.initializeVisualization(newItems, groups, periods.length);
    } else {
      this.timeline.setItems(newItems);
      this.timeline.setGroups(groups);
    }
  }

  private initializeVisualization(items: DataSet<any>, groups: DataSet<any>, lastPeriodNumber: number): boolean {
    const doc = this.visualization?.nativeElement;
    if (!doc) {
      return false;
    }

    const endDate = getEndDateFromPeriod(lastPeriodNumber).toISOString();
    const options = getOptions(this.translateService, {max: endDate});

    this.timeline = new Timeline(doc, items, options);
    this.timeline.setWindow(START_DATE, endDate);
    this.timeline.setGroups(groups);
    return true;
  }

  /**
   * Call this if different language has been selected.
   */
  private updateLanguageSelection(groups: DataSet<any>, translationsForChosenLanguage: Content[]): void {
    const resultStaticTitle = groups.getIds().filter(id => id === RESULT_GROUP_TITLE_ID);

    const idsToTranslate = groups.get()
      .filter(group => TRANSLATABLE_GROUP_TYPES.includes(group.data.type)).map(group => group.id)
      .concat(resultStaticTitle);

    const alreadyPreparedTranslatedIds: number[] = translationsForChosenLanguage.map((group: any) => group.id) || [];
    const toEmptyIds: number[] = idsToTranslate.filter(id => !alreadyPreparedTranslatedIds.includes(id));

    this.updateTranslationsInGroupsAndSetMissingTranslationsToEmptyString(groups, translationsForChosenLanguage, toEmptyIds);
  }

  private updateTranslationsInGroupsAndSetMissingTranslationsToEmptyString(groups: DataSet<any>, translations: Content[], toEmptyIds: number[]): void {
    groups.update(
      translations.concat(toEmptyIds.map(groupId => ({id: groupId, content: '', title: ''} as Content)))
    );
  }

  click(event: Event): void {
    const prop = this.timeline.getEventProperties(event);
    if (prop.item && typeof prop.item === 'number') {

      if (isResult(prop.item)) {
        this.router.navigate(['..', 'applicationFormResults'], {relativeTo: this.activatedRoute});
      }

    }
  }

}
