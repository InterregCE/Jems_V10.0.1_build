import {ProjectResultDTO, ProjectWorkPackageDTO} from '@cat/api';
import {DataSet} from 'vis-data/peer';
import * as moment from 'moment';
import {Moment} from 'moment';
import {TimelineOptions, TimelineTimeAxisScaleType} from 'vis-timeline';

export const colors = [
  'bg-purple',
  'bg-cyan'
];

export const EMPTY_STRING = '&nbsp';
export const START_DATE = '2000-01-01';

export const RESULT_BOX_ID = 9_939_999;

/**
 * If items/groups id is of format x_x3x_xxx, then it is connected to result indicator, those are not translated.
 */
export function shouldGroupBeTranslated(groupId: number): boolean {
  return Math.floor(groupId / 10000) % 10 !== 3;
}

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

function getWorkPackageId(indexWp: number): number {
  return (indexWp + 1) * 100_000;
}

/**
 * ID 1x_xxx stands for IDs related to activities
 */
function getActivityId(indexWp: number, indexActivity: number): number {
  return getWorkPackageId(indexWp) + 10_000 + (indexActivity + 1) * 100;
}

function getActivityBoxId(indexWp: number, indexActivity: number): number {
  return getActivityId(indexWp, indexActivity) + 50;
}

function getDeliverableBoxId(indexWp: number, indexActivity: number, indexDeliverable: number): number {
  return getActivityBoxId(indexWp, indexActivity) + (indexDeliverable + 1);
}

/**
 * ID 2x_xxx stands for IDs related to outputs
 */
function getOutputId(indexWp: number, indexOutput: number): number {
  return getWorkPackageId(indexWp) + 20_000 + (indexOutput + 1) * 100;
}

/**
 * it is 1 box per 1 group = we reuse group ID and just add 50
 */
function getOutputBoxId(indexWp: number, indexOutput: number): number {
  return getOutputId(indexWp, indexOutput) + 50;
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
  timePlan.workPackages.forEach((workPackage, indexWp) => {
    let minPeriod = 999;
    let maxPeriod = 0;

    workPackage.activities.forEach((activity, indexActivity) => {
      items = items.concat({
        id: getActivityBoxId(indexWp, indexActivity),
        group: getActivityId(indexWp, indexActivity),
        start: getStartDateFromPeriod(activity.startPeriod),
        end: getEndDateFromPeriod(activity.endPeriod),
        type: 'background',
        className: getColor(indexWp),
      });

      activity.deliverables.forEach((deliverable, indexDeliverable) => {
        items = items.concat({
          id: getDeliverableBoxId(indexWp, indexActivity, indexDeliverable),
          group: getActivityId(indexWp, indexActivity),
          start: getNestedStartDateFromPeriod(deliverable.period),
          end: getNestedEndDateFromPeriod(deliverable.period),
          type: 'range',
          content: `D${indexWp + 1}.${indexActivity + 1}.${indexDeliverable + 1}`,
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

    workPackage.outputs.forEach((output, indexOutput) => {
      items = items.concat({
        id: getOutputBoxId(indexWp, indexOutput),
        group: getOutputId(indexWp, indexOutput),
        start: getNestedStartDateFromPeriod(output.periodNumber),
        end: getNestedEndDateFromPeriod(output.periodNumber),
        type: 'range',
        title: `<span>Period: ${output.periodNumber}<br>Target value: ${output.targetValue || '-'}</span>`,
        content: `O.${indexOutput + 1}`,
        className: getColor(indexWp),
      });
    });

    if (minPeriod !== 999 && maxPeriod !== 0) {
      items = items.concat({
        id: indexWp + 1,
        group: getWorkPackageId(indexWp),
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
 *   - result indicator //TODO
 */
export function getGroups(timePlan: ProjectTimePlan): DataSet<any> {
  let wpSubGroups = new Array(0);
  const workPackages = timePlan.workPackages.map((workPackage, indexWp) => {
    const activities = workPackage.activities.map((activity, indexActivity) => {
      return {
        id: getActivityId(indexWp, indexActivity),
        content: EMPTY_STRING,
        treeLevel: 2,
      };
    });
    wpSubGroups = wpSubGroups.concat(activities);

    const outputs = workPackage.outputs.map((output, indexOutput) => {
      return {
        id: getOutputId(indexWp, indexOutput),
        content: EMPTY_STRING, // TODO use indicator code
        treeLevel: 2,
      };
    });
    wpSubGroups = wpSubGroups.concat(outputs);

    return {
      id: getWorkPackageId(indexWp),
      content: EMPTY_STRING,
      treeLevel: 1,
      nestedGroups: activities.map(activity => activity.id).concat(outputs.map(output => output.id)),
    };
  });

  let results: any[] = timePlan.results.map((result, indexResult) => {
    return {
      id: getResultId(indexResult),
      content: result.programmeResultIndicatorIdentifier || EMPTY_STRING,
      treeLevel: 2,
    };
  });
  if (results.length) {
    // create group for all results
    results = results.concat({
      id: RESULT_BOX_ID,
      content: getGroupName('Result Indicator:'), // TODO use translation here
      treeLevel: 1,
      nestedGroups: results.map(result => result.id),
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

export function getTranslations(timePlan: ProjectTimePlan): { [language: string]: Content[]; } {
  const languages: { [language: string]: Content[]; } = {};
  timePlan.workPackages.forEach((workPackage, indexWp) => {
    workPackage.name.forEach(translation => {
      if (!languages[translation.language]) {
        languages[translation.language] = new Array<Content>(0);
      }
      languages[translation.language].push({
        id: getWorkPackageId(indexWp),
        content: getGroupName(translation.translation),
      } as Content);
    });

    workPackage.activities.forEach((activity, indexActivity) => {
      activity.title.forEach(translation => {
        if (!languages[translation.language]) {
          languages[translation.language] = new Array<Content>(0);
        }
        languages[translation.language].push({
          id: getActivityId(indexWp, indexActivity),
          content: getGroupName(translation.translation),
          title: translation.translation,
        } as Content);
      });
    });

    workPackage.outputs.forEach((output, indexOutput) => {
      output.title.forEach(translation => {
        if (!languages[translation.language]) {
          languages[translation.language] = new Array<Content>(0);
        }
        languages[translation.language].push({
          id: getOutputId(indexWp, indexOutput),
          content: getGroupName(translation.translation),
        } as Content);
      });
    });
  });
  return languages;
}

export function getGroupName(text: string = EMPTY_STRING): string {
  return `<span>${text || EMPTY_STRING}</span>`;
}

export function getOptions(custom?: Partial<TimelineOptions>): TimelineOptions {
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
    },
    custom
  );
}
