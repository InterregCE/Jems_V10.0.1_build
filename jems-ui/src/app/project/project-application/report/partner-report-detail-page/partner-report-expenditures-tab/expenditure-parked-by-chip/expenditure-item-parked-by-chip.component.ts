import {Component, Input} from '@angular/core';
import {ExpenditureParkingMetadataDTO} from '@cat/api';

interface ParkedByI {
  icon: string;
  label: string;
  class: string;
}

const ParkedByEnum = {
  'JSMA': {icon: 'person_search', label: 'project.application.partner.report.expenditures.tab.cost.parked.by.jsma', class: 'parkedBy-JSMA'} as ParkedByI,
  'CONTROL': {icon: 'supervised_user_circle', label: 'project.application.partner.report.expenditures.tab.cost.parked.by.control', class: 'parkedBy-CONTROL'} as ParkedByI,
}

@Component({
  selector: 'jems-expenditure-item-parked-by-chip',
  templateUrl: './expenditure-item-parked-by-chip.component.html',
  styleUrls: ['./expenditure-item-parked-by-chip.component.scss']
})
export class ExpenditureItemParkedByChipComponent {

  @Input()
  parkingMetadata: ExpenditureParkingMetadataDTO;

  getParkedBy(parkingMetadata: ExpenditureParkingMetadataDTO): ParkedByI {
    return parkingMetadata?.reportProjectOfOriginId ? ParkedByEnum.JSMA : ParkedByEnum.CONTROL;
  }

}


