import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {OutputProjectPeriod} from '@cat/api';
import {AbstractControl} from '@angular/forms';

@Component({
  selector: 'app-project-periods-select',
  templateUrl: './project-periods-select.component.html',
  styleUrls: ['./project-periods-select.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPeriodsSelectComponent {

  @Input()
  periods: OutputProjectPeriod[];
  @Input()
  control: AbstractControl;
  @Input()
  label: string;
  @Input()
  class: string;

  @Output()
  selectionChanged = new EventEmitter<void>();

  getPeriodArguments(period: OutputProjectPeriod): { [key: string]: number } {
    return {
      periodNumber: period.number,
      start: period.start,
      end: period.end
    };
  }

}
