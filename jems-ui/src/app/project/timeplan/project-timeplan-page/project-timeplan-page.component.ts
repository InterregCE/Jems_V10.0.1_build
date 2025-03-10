import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Input,
  OnInit,
  ViewChild
} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Alert} from '@common/components/forms/alert';
import {TranslateService} from '@ngx-translate/core';
import {ProjectContractingReportingScheduleDTO, ProjectPeriodDTO} from '@cat/api';
import {Timeline, TimelineOptions} from 'vis-timeline';
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
import moment from 'moment/moment';
import TypeEnum = ProjectContractingReportingScheduleDTO.TypeEnum;
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {v4 as uuid} from 'uuid';

@UntilDestroy()
@Component({
  selector: 'jems-project-timeplan-page',
  templateUrl: './project-timeplan-page.component.html',
  styleUrls: ['./project-timeplan-page.component.scss'],
  providers: [ProjectTimeplanPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectTimeplanPageComponent implements OnInit {
  Alert = Alert;
  timeline: Timeline;

  @Input()
  reportingDeadlines$: Observable<ProjectContractingReportingScheduleDTO[]>;

  @ViewChild('visualization', {static: true})
  visualization: ElementRef;

  periodsUnavailable$: Observable<boolean>;
  workPackagesUnavailable$: Observable<boolean>;
  dataAvailable$: Observable<boolean>;
  reportingDeadlines: ProjectContractingReportingScheduleDTO[];
  periodCount: number;

  constructor(private translateService: TranslateService,
              private multiLanguageGlobalService: MultiLanguageGlobalService,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              public pageStore: ProjectTimeplanPageStore) {
    const data$ = combineLatest([
      this.pageStore.workPackages$,
      this.pageStore.projectResults$,
      this.pageStore.periods$,
      this.multiLanguageGlobalService.activeInputLanguage$,
      this.multiLanguageGlobalService.activeSystemLanguage$
    ])
      .pipe(
        map(([workPackages, _results, periods, inputLanguage, systemLanguage]) => {
          const results = _results.map(result => result.periodNumber !== 255 ? result :
            {...result, periodNumber: periods?.length + 1});
          return ({
            workPackages,
            results,
            timelineGroups: getGroups(workPackages, results),
            timelineItems: getItems(workPackages, results, this.translateService),
            timelineTranslations: getInputTranslations(workPackages)[inputLanguage] || [],
            periods: periods?.length ? [...periods, {projectId: 0, number: periods.length + 1, start: 0, end: 0, startDate: '', endDate: ''}] : [],
          });
        }),
        tap(data => this.createVisualizationOrUpdateJustTranslations(data.periods, data.timelineItems, data.timelineGroups)),
        tap((data) => this.updateLanguageSelection(data.timelineGroups, data.timelineTranslations)),
        tap(data => this.periodCount = data.periods.length),
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

  ngOnInit(): void {
    if (this.reportingDeadlines$) {
      combineLatest([
        this.reportingDeadlines$,
        this.dataAvailable$
      ]).pipe(
        map(([reportingDeadlines, dataAvailable]) => ({
          reportingDeadlines,
          dataAvailable,
        })),
        tap(data => this.reportingDeadlines = data.reportingDeadlines),
        tap(data => this.visualizeReportDeadlines()),
        untilDestroyed(this)
      ).subscribe();
    }
  }

  private createVisualizationOrUpdateJustTranslations(periods: ProjectPeriodDTO[], newItems: DataSet<any>, groups: DataSet<any>): void {
    if (!periods.length || !groups.length) {
      return;
    }

    const endDate = getEndDateFromPeriod(periods.length).toISOString();
    const options = getOptions(this.translateService, periods.length,  {max: endDate});

    if (!this.timeline) {
      this.initializeVisualization(newItems, options);
    } else {
      this.timeline.setOptions(options);
      this.timeline.setItems(newItems);
    }

    this.timeline.setWindow(START_DATE, endDate);
    this.timeline.setGroups(groups);
  }

  private initializeVisualization(items: DataSet<any>, options: TimelineOptions): void {
    const doc = this.visualization?.nativeElement;
    if (!doc) {
      return;
    }
    this.timeline = new Timeline(doc, items, options);
  }

  private visualizeReportDeadlines() {
    const deadlinesGroupedByPeriod = this.reportingDeadlines.reduce(function(arr, deadline) {
      arr[deadline.periodNumber] = arr[deadline.periodNumber] || [];
      arr[deadline.periodNumber].push(deadline);
      return arr;
    }, Object.create(null));

    if (this.timeline) {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      const timelineCustomTimes = this.timeline.customTimes;
      this.clearReportDeadlinesVisualisation(timelineCustomTimes);

      for (const [key, value] of Object.entries(deadlinesGroupedByPeriod)) {
        // To discard pins whose periods are greater than available timeplan periods
        if (Number(key) >= this.periodCount && Number(key) != 255) {
          continue;
        }
        const randomId = uuid();
        const group = value as ProjectContractingReportingScheduleDTO[];
        const markerMonth = Number(key) === 255 ? this.periodCount : key;
        this.timeline.addCustomTime(moment(START_DATE).add(markerMonth, 'M').endOf('month').toDate(), randomId);
        const customTimes = timelineCustomTimes.filter((component: { options: { id: string}}) => randomId === component.options.id);
        const financialReports = group.filter(d => d.type === TypeEnum.Finance);
        const contentReports = group.filter(d => d.type === TypeEnum.Content);
        const bothReports = group.filter(d => d.type === TypeEnum.Both);
        let markerContent = '';
        let markerType = TypeEnum.Both;
        if (financialReports.length > 0) {
          markerContent += 'Financial Deadline\n';
          for (const deadline of financialReports) {
            markerContent += deadline.date + '\n';
          }
          markerType = contentReports.length === 0 && bothReports.length === 0 ? TypeEnum.Finance : TypeEnum.Both;
        }
        if (contentReports.length > 0) {
          markerContent += '\nContent Deadline\n';
          for (const deadline of contentReports) {
            markerContent += deadline.date + '\n';
          }
          markerType = financialReports.length === 0 && bothReports.length === 0 ? TypeEnum.Content : TypeEnum.Both;
        }
        if (bothReports.length > 0) {
          markerContent += '\nBoth Deadline\n';
          for (const deadline of bothReports) {
            markerContent += deadline.date + '\n';
          }
        }
        this.insertMarkerWithBar(markerType, markerContent.trim(), customTimes[0].bar);
      }
    }
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

  private insertMarkerWithBar(type: ProjectContractingReportingScheduleDTO.TypeEnum, content: string, bar: any) {
    const marker = document.createElement('div');
    marker.title = content;
    marker.style.position = 'absolute';
    if (type === TypeEnum.Finance) {
      marker.innerHTML = '<span class="material-icons">savings</span>';
      marker.className = `vis-custom-time-marker finance`;
      bar.className = 'vis-custom-time finance';
      bar.appendChild(marker);
    } else if (type === TypeEnum.Content) {
      marker.innerHTML = '<span class="material-icons">description</span>';
      marker.className = `vis-custom-time-marker content`;
      bar.className = 'vis-custom-time content';
      bar.appendChild(marker);
    } else {
      marker.innerHTML = '<span class="material-icons">description</span><span class="material-icons">savings</span>';
      marker.className = `vis-custom-time-marker`;
      bar.className = 'vis-custom-time';
      bar.appendChild(marker);
    }
  }

  private clearReportDeadlinesVisualisation(timelineCustomTimes: any) {
    const timelineCustomTimesLength = timelineCustomTimes.length;
    let index = 0;
    while (index < timelineCustomTimesLength) {
      const id = timelineCustomTimes[0].options.id;
      this.timeline.removeCustomTime(id);
      index++;
    }
  }
}
