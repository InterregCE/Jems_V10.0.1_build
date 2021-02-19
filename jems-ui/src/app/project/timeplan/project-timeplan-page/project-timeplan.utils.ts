import {ProjectResultDTO, ProjectWorkPackageDTO} from '@cat/api';
import {DataSet} from 'vis-data/peer';
import * as moment from 'moment';
import {Moment} from 'moment';
import {TimelineOptions, TimelineTimeAxisScaleType} from 'vis-timeline';
import {TranslateService} from '@ngx-translate/core';
import {NumberService} from '../../../common/services/number.service';

export const colors = [
  'bg-orange',
  'bg-grey',
  'bg-green',
  'bg-brown',
  'bg-purple',
  'bg-cyan',
  'bg-pink',
  'bg-blue',
];

const EMPTY_STRING = '&nbsp';
export const START_DATE = '2000-01-01';

export const RESULT_GROUP_TITLE_ID = 9_950_000;
const OUTPUT_GROUP_UNKNOWN_INDICATOR = 9_900;

enum GroupType {
  WorkPackage,
  Activity,
  Indicator,
  ResultTitle,
}

export const TRANSLATABLE_GROUP_TYPES: GroupType[] = [
  GroupType.WorkPackage,
  GroupType.Activity,
];

function getColor(index: number): string {
  return colors[index % colors.length];
}

function getStartDateFromPeriod(period: number): Moment {
  return moment(START_DATE).add(period, 'M').startOf('month');
}

export function getEndDateFromPeriod(period: number): Moment {
  return moment(START_DATE).add(period, 'M').endOf('month');
}

function getNestedStartDateFromPeriod(period: number): Moment {
  return getStartDateFromPeriod(period).add(1, 'd');
}

function getNestedEndDateFromPeriod(period: number): Moment {
  return getEndDateFromPeriod(period).subtract(1, 'd');
}

function periodLabelFunction(date: Date, scale: string, step: number): string {
  const periodNumber = Math.round(moment(date).diff(
    moment(START_DATE), 'months', true)
  );
  return `Period ${periodNumber}`;
}

function groupTemplateFunction(item: any, translateService: TranslateService): string {
  const data = item.data;
  switch (data.type) {
    case GroupType.WorkPackage:
      return `<span>${translateService.instant(
        'common.label.workpackage',
        {wpNumber: data.wpNumber, title: item.content}
      )}</span>`;
    case GroupType.Activity:
      return `<span>${translateService.instant(
        'common.label.activity',
        {wpNumber: data.wpNumber, activityNumber: data.activityNumber, title: item.content}
      )}</span>`;
    case GroupType.Indicator:
      return `<span>${item.content}</span>`;
    case GroupType.ResultTitle:
      return `<span>${translateService.instant('result.indicator.title')}</span>`;
    default:
      return 'error';
  }
}

function getWorkPackageId(workPackageNumber: number): number {
  return workPackageNumber * 100_000;
}

/**
 * ID 1x_xxx stands for IDs related to activities
 */
function getActivityId(workPackageNumber: number, activityNumber: number): number {
  return getWorkPackageId(workPackageNumber) + 10_000 + activityNumber * 100;
}

function getActivityBoxId(workPackageNumber: number, activityNumber: number): number {
  return getActivityId(workPackageNumber, activityNumber) + 50;
}

function getDeliverableBoxId(workPackageNumber: number, activityNumber: number, deliverableNumber: number): number {
  return getActivityBoxId(workPackageNumber, activityNumber) + deliverableNumber;
}

/**
 * ID 2x_xxx stands for IDs related to outputs
 */
function getOutputIndicatorId(workPackageNumber: number, outputIndicatorId: number): number {
  if (outputIndicatorId) {
    return getWorkPackageId(workPackageNumber) + 20_000 + outputIndicatorId * 100;
  } else {
    return getWorkPackageId(workPackageNumber) + 20_000 + OUTPUT_GROUP_UNKNOWN_INDICATOR;
  }
}

/**
 * it is 1 box per 1 group = we reuse group ID and just add 50
 */
function getOutputBoxId(workPackageNumber: number, outputIndicatorId: number): number {
  return getOutputIndicatorId(workPackageNumber, outputIndicatorId) + 50;
}

/**
 * ID 13x_xxx (everything 3x_xxx) stands for IDs related to results
 */
function getResultIndicatorId(resultIndicatorId: number): number {
  if (resultIndicatorId) {
    return RESULT_GROUP_TITLE_ID + resultIndicatorId * 100;
  } else {
    return RESULT_GROUP_TITLE_ID + 99 * 100;
  }
}

export function isResult(itemId: number): boolean {
  const isResultRelated = Math.floor(itemId / 10000) % 10 === 5;
  const isNotGroup = itemId % 100 !== 0;
  return isResultRelated && isNotGroup;
}

/**
 * it is 1 box per 1 group = we reuse group ID and just add 50
 */
function getResultBoxId(resultIndicatorId: number, resultNumber: number): number {
  return getResultIndicatorId(resultIndicatorId) + resultNumber;
}

/**
 * Generate items = boxes visible inside timeline
 *
 * currently we are generating them for:
 *   - the whole WorkPackage
 *     - activity
 *       - deliverable
 *     - output
 *   - result indicator
 */
export function getItems(workPackages: ProjectWorkPackageDTO[], results: ProjectResultDTO[], translateService: TranslateService): DataSet<any> {
  let items = new Array(0);
  workPackages.forEach((wp, indexWp) => {
    let minPeriod = 999;
    let maxPeriod = 0;

    wp.activities.forEach(activity => {
      items = items.concat({
        id: getActivityBoxId(wp.workPackageNumber, activity.activityNumber),
        group: getActivityId(wp.workPackageNumber, activity.activityNumber),
        start: getStartDateFromPeriod(activity.startPeriod),
        end: getEndDateFromPeriod(activity.endPeriod),
        type: 'background',
        className: getColor(indexWp),
      });

      activity.deliverables.forEach(deliverable => {
        items = items.concat({
          id: getDeliverableBoxId(wp.workPackageNumber, activity.activityNumber, deliverable.deliverableNumber),
          group: getActivityId(wp.workPackageNumber, activity.activityNumber),
          start: getNestedStartDateFromPeriod(deliverable.period),
          end: getNestedEndDateFromPeriod(deliverable.period),
          type: 'range',
          content: `D${wp.workPackageNumber}.${activity.activityNumber}.${deliverable.deliverableNumber}`,
          className: getColor(indexWp),
        });
      });

      if (minPeriod > activity.startPeriod) {
        minPeriod = activity.startPeriod;
      }
      if (maxPeriod < activity.endPeriod) {
        maxPeriod = activity.endPeriod;
      }
    });

    wp.outputs.forEach(output => {
      items = items.concat({
        id: getOutputBoxId(wp.workPackageNumber, output.outputNumber),
        group: getOutputIndicatorId(wp.workPackageNumber, output.programmeOutputIndicatorId),
        start: getNestedStartDateFromPeriod(output.periodNumber),
        end: getNestedEndDateFromPeriod(output.periodNumber),
        type: 'range',
        title: getIndicatorTooltip(output.targetValue, translateService),
        content: `O${wp.workPackageNumber}.${output.outputNumber}`,
        className: getColor(indexWp),
      });
    });

    if (minPeriod !== 999 && maxPeriod !== 0) {
      items = items.concat({
        id: wp.workPackageNumber,
        group: getWorkPackageId(wp.workPackageNumber),
        start: getStartDateFromPeriod(minPeriod),
        end: getEndDateFromPeriod(maxPeriod),
        type: 'background',
        className: getColor(indexWp),
      });
    }
  });

  results.forEach((result, indexResult) => {
    items = items.concat({
      id: getResultBoxId(result.programmeResultIndicatorId, result.resultNumber),
      group: getResultIndicatorId(result.programmeResultIndicatorId),
      start: getNestedStartDateFromPeriod(result.periodNumber),
      end: getNestedEndDateFromPeriod(result.periodNumber),
      type: 'range',
      title: getIndicatorTooltip(result.targetValue, translateService),
      content: `R.${result.resultNumber}`,
      data: {type: GroupType.Indicator},
      className: 'bg-blue',
    });
  });

  return new DataSet(items);
}

function getIndicatorTooltip(targetValue: number, translateService: TranslateService): string {
  return targetValue
    ? `<span>${translateService.instant('project.results.result.target.value')}: ${NumberService.toLocale(targetValue)}</span>`
    : '';
}

export function sortNullLast(a: Indicator, b: Indicator): number {
  if (a.id === b.id) {
    return 0;
  } else if (a.id === null) {
    return 1;
  } else if (b.id === null) {
    return -1;
  } else {
    return a.id < b.id ? -1 : 1;
  }
}

/**
 * Generate groups = group represents 1 swim lane inside timeline (the first left collapsable column)
 *
 * currently we are generating them for:
 *   - the whole WorkPackage
 *     - activity
 *     - output
 *   - result indicator
 */
export function getGroups(workPackages: ProjectWorkPackageDTO[], results: ProjectResultDTO[]): DataSet<any> {
  let wpSubGroups = new Array(0);
  const wpGroups = workPackages.map(wp => {
    const activities = wp.activities.map(activity => {
      return {
        id: getActivityId(wp.workPackageNumber, activity.activityNumber),
        treeLevel: 2,
        data: {type: GroupType.Activity, wpNumber: wp.workPackageNumber, activityNumber: activity.activityNumber}
      };
    });
    wpSubGroups = wpSubGroups.concat(activities);

    const uniqueOutputIndicators: Indicator[] = [];
    wp.outputs.forEach(output => {
      if (uniqueOutputIndicators.findIndex(x => x.id === output.programmeOutputIndicatorId) === -1) {
        uniqueOutputIndicators.push({id: output.programmeOutputIndicatorId, identifier: output.programmeOutputIndicatorIdentifier});
      }
    });

    const outputGroups = uniqueOutputIndicators.sort(sortNullLast).map(indicator => {
      return {
        id: getOutputIndicatorId(wp.workPackageNumber, indicator.id),
        content: indicator.identifier || EMPTY_STRING,
        treeLevel: 2,
        data: {type: GroupType.Indicator},
      };
    });

    wpSubGroups = wpSubGroups.concat(outputGroups);

    return {
      id: getWorkPackageId(wp.workPackageNumber),
      treeLevel: 1,
      nestedGroups: activities.map(activity => activity.id).concat(outputGroups.map(output => output.id)),
      data: {type: GroupType.WorkPackage, wpNumber: wp.workPackageNumber},
    };
  });

  const uniqueResultIndicators: Indicator[] = [];
  results.forEach(result => {
    if (uniqueResultIndicators.findIndex(x => x.id === result.programmeResultIndicatorId) === -1) {
      uniqueResultIndicators.push({id: result.programmeResultIndicatorId, identifier: result.programmeResultIndicatorIdentifier});
    }
  });

  let resultGroups: any[] = uniqueResultIndicators.sort(sortNullLast).map(indicator => {
    return {
      id: getResultIndicatorId(indicator.id),
      content: indicator.identifier || EMPTY_STRING,
      treeLevel: 2,
      data: {type: GroupType.Indicator},
    };
  });

  if (resultGroups.length) {
    // create group for all results
    resultGroups = resultGroups.concat({
      id: RESULT_GROUP_TITLE_ID,
      treeLevel: 1,
      nestedGroups: resultGroups.map(result => result.id),
      data: {type: GroupType.ResultTitle},
    });
  }

  return new DataSet(wpGroups.concat(wpSubGroups).concat(resultGroups));
}

class Indicator {
  id: number;
  identifier: string;
}

export class Content {
  id: number;
  content: string;
}

export function getInputTranslations(workPackages: ProjectWorkPackageDTO[]): { [language: string]: Content[]; } {
  const languages: { [language: string]: Content[]; } = {};
  workPackages.forEach(wp => {
    wp.name.forEach(translation => {
      if (!languages[translation.language]) {
        languages[translation.language] = new Array<Content>(0);
      }
      languages[translation.language].push({
        id: getWorkPackageId(wp.workPackageNumber),
        content: translation.translation,
        title: translation.translation,
      } as Content);
    });

    wp.activities.forEach(activity => {
      activity.title.forEach(translation => {
        if (!languages[translation.language]) {
          languages[translation.language] = new Array<Content>(0);
        }
        languages[translation.language].push({
          id: getActivityId(wp.workPackageNumber, activity.activityNumber),
          content: translation.translation,
          title: translation.translation,
        } as Content);
      });
    });
  });
  return languages;
}

export function getOptions(translateService: TranslateService, custom?: Partial<TimelineOptions>): TimelineOptions {
  return Object.assign(
    {
      showCurrentTime: false,
      showMajorLabels: false,
      orientation: 'top',
      timeAxis: {scale: 'month' as TimelineTimeAxisScaleType, step: 1},
      format: {minorLabels: periodLabelFunction},
      margin: {
        axis: 10,
        item: {vertical: 10, horizontal: 0}
      },
      min: getStartDateFromPeriod(1),
      // if 1 Period = 1 Month, we can zoom only to max 1 Period ~= 30days
      zoomMin: 33 * 24 * 60 * 60 * 1000,
      groupTemplate(item: any, element: any, d: any): string { return groupTemplateFunction(item, translateService); }
    },
    custom
  );
}
