import {ProjectResultDTO, ProjectWorkPackageDTO} from '@cat/api';
import {DataSet} from 'vis-data/peer';
import * as moment from 'moment';
import {Moment} from 'moment';
import {TimelineOptions, TimelineTimeAxisScaleType} from 'vis-timeline';
import {TranslateService} from '@ngx-translate/core';

export const colors = [
  'bg-purple',
  'bg-cyan'
];

export const EMPTY_STRING = '&nbsp';
export const START_DATE = '2000-01-01';

export const RESULT_BOX_ID = 9_939_999;

enum GroupType {
  WorkPackage,
  Activity,
  OutputIndicator,
  ResultTitle,
  ResultIndicator
}

export const TRANSLATABLE_GROUP_TYPES: GroupType[] = [
  GroupType.WorkPackage,
  GroupType.Activity,
];

function getColor(index: number): string {
  return colors[index % colors.length];
}

export function getStartDateFromPeriod(period: number): Moment {
  return moment(START_DATE).add(period, 'M').startOf('month');
}

export function getEndDateFromPeriod(period: number): Moment {
  return moment(START_DATE).add(period, 'M').endOf('month');
}

export function getNestedStartDateFromPeriod(period: number): Moment {
  return getStartDateFromPeriod(period).add(1, 'd');
}

export function getNestedEndDateFromPeriod(period: number): Moment {
  return getEndDateFromPeriod(period).subtract(1, 'd');
}

export function periodLabelFunction(date: Date, scale: string, step: number): string {
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
    case GroupType.ResultIndicator:
    case GroupType.OutputIndicator:
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
function getOutputId(workPackageNumber: number, outputNumber: number): number {
  return getWorkPackageId(workPackageNumber) + 20_000 + outputNumber * 100;
}

/**
 * it is 1 box per 1 group = we reuse group ID and just add 50
 */
function getOutputBoxId(workPackageNumber: number, outputNumber: number): number {
  return getOutputId(workPackageNumber, outputNumber) + 50;
}

/**
 * ID 13x_xxx (everything 3x_xxx) stands for IDs related to results
 */
function getResultId(indexResult: number): number {
  return 130_000 + (indexResult + 1) * 100;
}

/**
 * it is 1 box per 1 group = we reuse group ID and just add 50
 */
function getResultBoxId(indexResult: number): number {
  return getResultId(indexResult) + 50;
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
export function getItems(timePlan: ProjectTimePlan): DataSet<any> {
  let items = new Array(0);
  timePlan.workPackages.forEach((wp, indexWp) => {
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
        group: getOutputId(wp.workPackageNumber, output.outputNumber),
        start: getNestedStartDateFromPeriod(output.periodNumber),
        end: getNestedEndDateFromPeriod(output.periodNumber),
        type: 'range',
        title: `<span>Period: ${output.periodNumber}<br>Target value: ${output.targetValue || '-'}</span>`,
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

  timePlan.results.forEach((result, indexResult) => {
    items = items.concat({
      id: getResultBoxId(indexResult),
      group: getResultId(indexResult),
      start: getNestedStartDateFromPeriod(result.periodNumber),
      end: getNestedEndDateFromPeriod(result.periodNumber),
      type: 'range',
      content: `R.${indexResult + 1}`,
      className: getColor(indexResult),
    });
  });

  return new DataSet(items);
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
export function getGroups(timePlan: ProjectTimePlan): DataSet<any> {
  let wpSubGroups = new Array(0);
  const workPackages = timePlan.workPackages.map(wp => {
    const activities = wp.activities.map(activity => {
      return {
        id: getActivityId(wp.workPackageNumber, activity.activityNumber),
        treeLevel: 2,
        data: {type: GroupType.Activity, wpNumber: wp.workPackageNumber, activityNumber: activity.activityNumber}
      };
    });
    wpSubGroups = wpSubGroups.concat(activities);

    const outputs = wp.outputs.map(output => {
      return {
        id: getOutputId(wp.workPackageNumber, output.outputNumber),
        content: output.programmeOutputIndicatorId || EMPTY_STRING,
        treeLevel: 2,
        data: {type: GroupType.OutputIndicator},
      };
    });
    wpSubGroups = wpSubGroups.concat(outputs);

    return {
      id: getWorkPackageId(wp.workPackageNumber),
      treeLevel: 1,
      nestedGroups: activities.map(activity => activity.id).concat(outputs.map(output => output.id)),
      data: {type: GroupType.WorkPackage, wpNumber: wp.workPackageNumber},
    };
  });

  let results: any[] = timePlan.results.map((result, indexResult) => {
    return {
      id: getResultId(indexResult),
      content: result.programmeResultIndicatorIdentifier || EMPTY_STRING,
      treeLevel: 2,
      data: {type: GroupType.ResultIndicator},
    };
  });
  if (results.length) {
    // create group for all results
    results = results.concat({
      id: RESULT_BOX_ID,
      treeLevel: 1,
      nestedGroups: results.map(result => result.id),
      data: {type: GroupType.ResultTitle},
    });
  }

  return new DataSet(workPackages.concat(wpSubGroups).concat(results));
}

export class ProjectTimePlan {
  workPackages: ProjectWorkPackageDTO[];
  results: ProjectResultDTO[];
}

export class Content {
  id: number;
  content: string;
}

export function getInputTranslations(timePlan: ProjectTimePlan, translateService: TranslateService): { [language: string]: Content[]; } {
  const languages: { [language: string]: Content[]; } = {};
  timePlan.workPackages.forEach(wp => {
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
