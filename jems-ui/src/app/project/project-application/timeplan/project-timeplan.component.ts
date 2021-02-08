import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {UntilDestroy} from '@ngneat/until-destroy';
import {Timeline, TimelineOptions, TimelineTimeAxisScaleType} from 'vis-timeline';
import {DataSet} from 'vis-data/peer';
import {ProjectStore} from '../containers/project-application-detail/services/project-store.service';
import {
  Content,
  EMPTY_STRING,
  getGroups,
  getItems,
  getTranslations,
  periodLabelFunction,
  START_DATE,
  TEST_DATA
} from './project-timeplan.utils';
import {InputTranslation} from '@cat/api';

@UntilDestroy()
@Component({
  selector: 'app-project-timeplan',
  templateUrl: './project-timeplan.component.html',
  styleUrls: ['./project-timeplan.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProjectTimeplanComponent implements OnInit {
  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  timeline: Timeline;

  groups: DataSet<any>;
  items: DataSet<any>;
  translations: { [language: string]: Content[]; } = {};

  options: TimelineOptions = {
    // zoomable: false,
    // moveable: false,
    showCurrentTime: false,
    showMajorLabels: false,
    orientation: 'top',
    timeAxis: {scale: 'month' as TimelineTimeAxisScaleType, step: 1},
    format: {minorLabels: periodLabelFunction},
    margin: {
      axis: 10,
      item: {vertical: 10, horizontal: 0}
    },
    min: START_DATE,
    // TODO compute end date based on last available period for this project
    // max: END_DATE,
  };

  constructor(
    private activatedRoute: ActivatedRoute,
    public projectStore: ProjectStore
  ) {
  }

  /**
   * TODO: you need to fetch:
   * - getFullWorkPackagesByProjectId(projectId)
   * - getProjectResults(projectId)
   * - get last period or all periods (those should be in store already, we will then use them for setting boundaries)
   * - get input languages available (instead of 'random lang' button we will use language toggle then)
   *
   * those first 2 you will use as ProjectTimePlan() instead of TEST_DATA in following lines:
   */
  ngOnInit(): void {
    this.translations = getTranslations(TEST_DATA);
    this.groups = getGroups(TEST_DATA);
    // this.updateLanguageSelection(); // TODO uncomment and call with selected language, also remove tmpUpdateRandomLang
    this.tmpUpdateRandomLang();
    this.items = getItems(TEST_DATA);
    const doc = document.getElementById('visualization');

    if (doc) {
      this.timeline = new Timeline(doc, this.items, this.options);
      this.timeline.setGroups(this.groups);
    }
  }

  click(event: Event): void {
    const prop = this.timeline.getEventProperties(event);
    console.log('click gr ' + prop.group + ', id ' + prop.item);
  }

  // TODO remove
  tmpUpdateRandomLang(): void {
    const langs = Object.keys(this.translations);
    const lang = langs[Math.floor(Math.random() * 1000) % langs.length];
    this.updateLanguageSelection(lang);
  }

  /**
   * Call this if different language has been selected.
   */
  updateLanguageSelection(language: InputTranslation.LanguageEnum | string): void {
    const translatedIds = this.translations[language].map(group => group.id);
    const allIds: number[] = this.groups.getIds().map(groupId => Number(groupId));
    const toEmptyIds = allIds.filter(element => !translatedIds.includes(element));

    // if some translations are missing for particular language, we need to reset them to EMPTY_STRING
    this.groups.update(this.translations[language]
      .concat(toEmptyIds.map(groupId => ({id: groupId, content: EMPTY_STRING} as Content)))
    );
  }

}
