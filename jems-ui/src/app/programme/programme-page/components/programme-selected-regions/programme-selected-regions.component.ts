import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ProgrammeRegionCheckbox} from '../../model/programme-region-checkbox';
import {KeyValue} from '@angular/common';

@Component({
  selector: 'jems-programme-selected-regions',
  templateUrl: './programme-selected-regions.component.html',
  styleUrls: ['./programme-selected-regions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeSelectedRegionsComponent {
  @Input()
  selectedRegions: Map<string, ProgrammeRegionCheckbox[]>;

  originalOrder = (a: KeyValue<string, ProgrammeRegionCheckbox[]>,
                   b: KeyValue<string, ProgrammeRegionCheckbox[]>): number => 0;
}
