import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {KeyValue} from '@angular/common';
import {JemsRegionCheckbox} from '@common/models/jems-region-checkbox';

@Component({
  selector: 'jems-selected-regions',
  templateUrl: './jems-selected-regions.component.html',
  styleUrls: ['./jems-selected-regions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class JemsSelectedRegionsComponent {
  @Input()
  selectedRegions: Map<string, JemsRegionCheckbox[]>;

  originalOrder = (a: KeyValue<string, JemsRegionCheckbox[]>,
                   b: KeyValue<string, JemsRegionCheckbox[]>): number => 0;
}
