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
            periods: periods?.length ? [...periods, {projectId: 0, number: periods.length + 1, start: 0, end: 0}] : [],
          });
        }),
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
    if (!this.timeline) {
      this.initializeVisualization(newItems, groups, periods.length);
    } else {
      this.timeline.setItems(newItems);
      this.timeline.setGroups(groups);
    }
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
        const randomId = uuid();
        const group = value as ProjectContractingReportingScheduleDTO[];
        this.timeline.addCustomTime(moment(START_DATE).add(key, 'M').endOf('month').toDate(), randomId);
        const customTimes = timelineCustomTimes.filter((component: { options: { id: string}}) => randomId === component.options.id);
        const financialReports = group.filter(d => d.type == TypeEnum.Finance);
        const contentReports = group.filter(d => d.type == TypeEnum.Content);
        const bothReports = group.filter(d => d.type == TypeEnum.Both);
        let markerContent = '';
        let markerType = TypeEnum.Both;
        if (financialReports.length > 0) {
          markerContent += 'Financial Deadline\n';
          for (const deadline of financialReports) {
            markerContent += deadline.date + '\n';
          }
          markerType = contentReports.length == 0 && bothReports.length == 0 ? TypeEnum.Finance : TypeEnum.Both;
        }
        if (contentReports.length > 0) {
          markerContent += '\nContent Deadline\n';
          for (const deadline of contentReports) {
            markerContent += deadline.date + '\n';
          }
          markerType = financialReports.length == 0 && bothReports.length == 0 ? TypeEnum.Content : TypeEnum.Both;
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

  private initializeVisualization(items: DataSet<any>, groups: DataSet<any>, lastPeriodNumber: number): boolean {
    const doc = this.visualization?.nativeElement;
    if (!doc) {
      return false;
    }

    const endDate = getEndDateFromPeriod(lastPeriodNumber).toISOString();
    const options = getOptions(this.translateService, lastPeriodNumber,  {max: endDate});

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

  private insertMarkerWithBar(type: ProjectContractingReportingScheduleDTO.TypeEnum, content: string, bar: any) {
    const marker = document.createElement('div');
    marker.title = content;
    marker.style.position = 'absolute';
    if (type == TypeEnum.Finance) {
      marker.innerHTML = 'FD';
      marker.className = `vis-custom-time-marker`;
      marker.style.backgroundColor = '#ef0a0a';
      bar.appendChild(marker);
      bar.style.backgroundColor = '#ef0a0a';
    } else if (type == TypeEnum.Content) {
      marker.innerHTML = 'CD';
      marker.className = `vis-custom-time-marker`;
      marker.style.backgroundColor = '#5380ce';
      bar.appendChild(marker);
      bar.style.backgroundColor = '#5380ce';
    } else {
      marker.innerHTML = 'BD';
      marker.className = `vis-custom-time-marker`;
      marker.style.backgroundColor = '#26961b';
      bar.appendChild(marker);
      bar.style.backgroundColor = '#26961b';
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
