import {ProgrammeLumpSum} from './lump-sums/programmeLumpSum';
import {ProgrammeUnitCost} from './programmeUnitCost';
import {CallFlatRateSetting} from './call-flat-rate-setting';

export class ProjectCallSettings {
  callId: number;
  callName: string;
  startDate: Date;
  endDate: Date;
  lengthOfPeriod: number;
  flatRates: CallFlatRateSetting;
  lumpSums: Array<ProgrammeLumpSum>;
  unitCosts: Array<ProgrammeUnitCost>;
  multipleFundsAllowed: boolean;

  constructor(callId: number, callName: string, startDate: Date, endDate: Date, lengthOfPeriod: number, flatRates: CallFlatRateSetting, lumpSums: Array<ProgrammeLumpSum>, unitCosts: Array<ProgrammeUnitCost>, multipleFundsAllowed: boolean) {
    this.callId = callId;
    this.callName = callName;
    this.startDate = startDate;
    this.endDate = endDate;
    this.lengthOfPeriod = lengthOfPeriod;
    this.flatRates = flatRates;
    this.lumpSums = lumpSums;
    this.unitCosts = unitCosts;
    this.multipleFundsAllowed = multipleFundsAllowed;
  }
}
