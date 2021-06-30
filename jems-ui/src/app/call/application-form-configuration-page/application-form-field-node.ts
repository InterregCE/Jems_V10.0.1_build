import {ApplicationFormFieldConfigurationDTO} from '@cat/api';
import AvailableInStepEnum = ApplicationFormFieldConfigurationDTO.AvailableInStepEnum;

export class ApplicationFormFieldNode {
  id: string;
  isVisible?: boolean;
  availableInStep?: AvailableInStepEnum;
  isVisibilityLocked?: boolean;
  isStepSelectionLocked?: boolean;
  children?: ApplicationFormFieldNode[];
  parentIndex: number;
}
