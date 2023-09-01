import {Component, Input} from '@angular/core';
import {ExpenditureParkingMetadataDTO} from '@cat/api';

export enum ExpenditureParkedByEnum {
  NONE = 'NONE',
  JSMA = 'JSMA',
  CONTROL = 'CONTROL',
}

interface ExpenditureParkedByValue {
  icon: string;
  label: string;
  class: string;
}

@Component({
  selector: 'jems-expenditure-item-parked-by-chip',
  templateUrl: './expenditure-item-parked-by-chip.component.html',
  styleUrls: ['./expenditure-item-parked-by-chip.component.scss']
})
export class ExpenditureItemParkedByChipComponent {

  @Input()
  parkedBy: ExpenditureParkedByEnum;

  parkedByValues: Record<ExpenditureParkedByEnum, ExpenditureParkedByValue | undefined> = {
    NONE: undefined,
    JSMA: {
      icon: 'person_search',
      label: 'project.application.partner.report.expenditures.tab.cost.parked.by.jsma',
      class: 'parkedBy-JSMA'
    } as ExpenditureParkedByValue,
    CONTROL: {
      icon: 'supervised_user_circle',
      label: 'project.application.partner.report.expenditures.tab.cost.parked.by.control',
      class: 'parkedBy-CONTROL'
    } as ExpenditureParkedByValue,
  };

  static getParkedBy(parkingMetadata: ExpenditureParkingMetadataDTO, parkedByControl = false): ExpenditureParkedByEnum {
    if (parkedByControl) {return ExpenditureParkedByEnum.CONTROL;}
    if (!parkingMetadata) {return ExpenditureParkedByEnum.NONE;}
    return parkingMetadata.reportProjectOfOriginId ? ExpenditureParkedByEnum.JSMA : ExpenditureParkedByEnum.CONTROL;
  }
}


