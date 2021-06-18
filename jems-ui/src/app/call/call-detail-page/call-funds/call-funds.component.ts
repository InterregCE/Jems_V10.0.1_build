import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {ProgrammeFundDTO} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {CallDetailPageStore} from '../call-detail-page-store.service';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-call-funds',
  templateUrl: './call-funds.component.html',
  styleUrls: ['./call-funds.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallFundsComponent {
  @Input()
  funds: ProgrammeFundDTO[];
  @Input()
  initialFunds: ProgrammeFundDTO[];

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

  fundDisabled(callIsEditable: boolean, callIsPublished: boolean, fund: ProgrammeFundDTO): boolean {
    if (!callIsEditable) {
      return true;
    }
    if (callIsPublished) {
      const foundFund = this.initialFunds.find(initialFunds => initialFunds.id === fund.id);
      return !!(foundFund && foundFund.selected);
    }
    return false;
  }
}
