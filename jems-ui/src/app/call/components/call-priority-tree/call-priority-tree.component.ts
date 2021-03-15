import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {CallPriorityCheckbox} from '../../containers/model/call-priority-checkbox';
import {InputTranslation} from '@cat/api';
import {LanguageService} from '../../../common/services/language.service';

@Component({
  selector: 'app-call-priority-tree',
  templateUrl: './call-priority-tree.component.html',
  styleUrls: ['./call-priority-tree.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallPriorityTreeComponent extends BaseComponent {
  @Input()
  priorityCheckboxes: CallPriorityCheckbox[];
  @Input()
  disabled: boolean;
  @Input()
  isApplicant: boolean;
  @Input()
  initialPriorityCheckboxes: CallPriorityCheckbox[];

  @Output()
  selectionChanged = new EventEmitter<void>();

  constructor(public languageService: LanguageService) {
    super();
  }

  priorityVisible(priority: CallPriorityCheckbox): boolean {
    return !this.isApplicant || priority.checked || priority.someChecked();
  }

  translated(element: InputTranslation[], currentSystemLanguage: string | null): string {
    if (!currentSystemLanguage || !element) {
      return '';
    }
    const elementInSystemLang = element.find((it: InputTranslation) => it.language === currentSystemLanguage);
    return !!elementInSystemLang ? elementInSystemLang.translation : '';
  }

  isPriorityAlreadySelected(priority: CallPriorityCheckbox): boolean {
    const foundPriority = this.initialPriorityCheckboxes.find(initialPriority => initialPriority.name === priority.name);
    return !!(foundPriority && (foundPriority.checked || foundPriority.someChecked()));
  }

  isPolicyAlreadySelected(priority: CallPriorityCheckbox, policy: CallPriorityCheckbox): boolean {
    const foundPriority = this.initialPriorityCheckboxes.find(initialPriority => initialPriority.name === priority.name);
    if (foundPriority) {
      const foundPolicy = foundPriority.children.find(initialPolicy => initialPolicy.name === policy.name);
      return !!(foundPolicy && foundPolicy.checked);
    }
    return false;
  }
}
