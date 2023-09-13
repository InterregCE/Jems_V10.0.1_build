import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {ProjectPeriodDTO} from '@cat/api';
import {AbstractControl} from '@angular/forms';
import {Alert} from '@common/components/forms/alert';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectUtil} from '@project/common/project-util';

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
  @Input()
  required: boolean;
  @Input()
  disabled = false;

  @Output()
  selectionChanged = new EventEmitter<void>();

  ProjectUtil = ProjectUtil;

  constructor(public projectStore: ProjectStore) { }
}
