import {InputTranslation} from '@cat/api';

export class PartnerLumpSum {
  lumpSumName: InputTranslation[];
  lumpSumDescription: InputTranslation[];
  lumpSumCost: number | undefined;
  period: number;
  partnerShare: number;

   constructor( lumpSumName: InputTranslation[], lumpSumDescription: InputTranslation[], lumpSumCost: number | undefined, period: number, partnerShare: number) {
     this.lumpSumName = lumpSumName;
     this.lumpSumDescription = lumpSumDescription;
     this.lumpSumCost =  lumpSumCost;
     this.period = period;
     this.partnerShare = partnerShare;
   }
}
