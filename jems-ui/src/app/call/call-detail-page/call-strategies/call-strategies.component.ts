import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {OutputProgrammeStrategy} from '@cat/api';
import {CallDetailPageStore} from '../call-detail-page-store.service';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
@Component({
  selector: 'app-call-strategies',
  templateUrl: './call-strategies.component.html',
  styleUrls: ['./call-strategies.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallStrategiesComponent {
  @Input()
  strategies: OutputProgrammeStrategy[];
  @Input()
  initialStrategies: OutputProgrammeStrategy[];

  @Output()
  selectionChanged = new EventEmitter<void>();

  data$: Observable<{
    callIsEditable: boolean,
    callIsPublished: boolean,
    isApplicant: boolean
  }>;

  constructor(private callDetailPageStore: CallDetailPageStore) {
    this.data$ = combineLatest([
      this.callDetailPageStore.callIsEditable$,
      this.callDetailPageStore.callIsPublished$,
      this.callDetailPageStore.userCannotAccessCalls$
    ])
      .pipe(
        map(([callIsEditable, callIsPublished, isApplicant]) => ({callIsEditable, callIsPublished, isApplicant}))
      );
  }

  strategyDisabled(callIsEditable: boolean, callIsPublished: boolean, strategy: OutputProgrammeStrategy): boolean {
    if (!callIsEditable) {
      return true;
    }
    if (callIsPublished) {
      const foundStrategy = this.initialStrategies.find(initialStrategy => initialStrategy.strategy === strategy.strategy);
      return !!(foundStrategy && foundStrategy.active);
    }
    return false;
  }
}
