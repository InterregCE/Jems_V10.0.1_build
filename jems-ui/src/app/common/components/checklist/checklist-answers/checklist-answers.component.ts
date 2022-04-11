import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {
  HeadlineMetadataDTO,
  OptionsToggleMetadataDTO,
  ProgrammeChecklistComponentDTO,
  ProgrammeChecklistDetailDTO
} from '@cat/api';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'jems-checklist-answers',
  templateUrl: './checklist-answers.component.html',
  styleUrls: ['./checklist-answers.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChecklistAnswersComponent {
  COMPONENT_TYPE = ProgrammeChecklistComponentDTO.TypeEnum;
  Alert = Alert;

  @Input()
  checklist: ProgrammeChecklistDetailDTO;

  @Input()
  dirty = false;

  getHeadline(component: ProgrammeChecklistComponentDTO): HeadlineMetadataDTO {
    return component.metadata as HeadlineMetadataDTO;
  }

  getOptionsToggle(component: ProgrammeChecklistComponentDTO): OptionsToggleMetadataDTO {
    return component.metadata as OptionsToggleMetadataDTO;
  }
}
