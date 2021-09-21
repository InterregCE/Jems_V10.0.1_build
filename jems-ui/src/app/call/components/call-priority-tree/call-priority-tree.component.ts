import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {CallPriorityCheckbox} from '../../containers/model/call-priority-checkbox';
import {CallDetailPageStore} from '../../call-detail-page/call-detail-page-store.service';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-call-priority-tree',
  templateUrl: './call-priority-tree.component.html',
  styleUrls: ['./call-priority-tree.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallPriorityTreeComponent {
  @Input()
  priorityCheckboxes: CallPriorityCheckbox[];
  @Input()
  initialPriorityCheckboxes: CallPriorityCheckbox[];

  @Output()
  selectionChanged = new EventEmitter<void>();

  data$: Observable<{
    userCanApply: boolean,
    callIsReadable: boolean,
    callIsEditable: boolean,
    callIsPublished: boolean
  }>;

  constructor(private callDetailPageStore: CallDetailPageStore) {
    this.data$ = combineLatest([
      this.callDetailPageStore.userCanApply$,
      this.callDetailPageStore.callIsReadable$,
      this.callDetailPageStore.callIsEditable$,
      this.callDetailPageStore.callIsPublished$
    ])
      .pipe(
        map(([userCanApply, callIsReadable, callIsEditable, callIsPublished]) => ({userCanApply, callIsReadable, callIsEditable, callIsPublished}))
      );
  }

  priorityDisabled(callIsEditable: boolean, callIsPublished: boolean, priority: CallPriorityCheckbox): boolean {
    if (!callIsEditable) {
      return true;
    }
    if (callIsPublished) {
      const foundPriority = this.initialPriorityCheckboxes.find(initialPriority => initialPriority.name === priority.name);
      return !!(foundPriority && (foundPriority.checked || foundPriority.someChecked()));
    }
    return false;
  }

  policyDisabled(callIsEditable: boolean, callIsPublished: boolean, priority: CallPriorityCheckbox, policy: CallPriorityCheckbox): boolean {
    if (!callIsEditable) {
      return true;
    }
    if (callIsPublished) {
      const foundPriority = this.initialPriorityCheckboxes.find(initialPriority => initialPriority.name === priority.name);
      if (foundPriority) {
        const foundPolicy = foundPriority.children.find(initialPolicy => initialPolicy.name === policy.name);
        return !!(foundPolicy && foundPolicy.checked);
      }
    }
    return false;
  }
}
