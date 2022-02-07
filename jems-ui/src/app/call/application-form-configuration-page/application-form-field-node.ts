import {ApplicationFormFieldConfigurationDTO} from '@cat/api';
import AvailableInStepEnum = ApplicationFormFieldConfigurationDTO.AvailableInStepEnum;

export class ApplicationFormFieldNode {
  id: string;
  visible?: boolean;
  availableInStep?: AvailableInStepEnum;
  visibilityLocked?: boolean;
  stepSelectionLocked?: boolean;
  children?: ApplicationFormFieldNode[];
  rootIndex: number;
  showStepToggle: boolean;
  showVisibilitySwitch: boolean;
  isHiddenInSpf: boolean;
}
