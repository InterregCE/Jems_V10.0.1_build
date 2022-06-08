import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {ProjectPeriodDTO} from '@cat/api';
import {AbstractControl} from '@angular/forms';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'jems-project-periods-select',
  templateUrl: './project-periods-select.component.html',
  styleUrls: ['./project-periods-select.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPeriodsSelectComponent {

  Alert = Alert;

  @Input()
  periods: ProjectPeriodDTO[];
  @Input()
  control: AbstractControl;
  @Input()
  label: string;

  @Output()
  selectionChanged = new EventEmitter<void>();

  getPeriodArguments(period: ProjectPeriodDTO): { [key: string]: number } {
    return {
      periodNumber: period.number,
      start: period.start,
      end: period.end
    };
  }

}
