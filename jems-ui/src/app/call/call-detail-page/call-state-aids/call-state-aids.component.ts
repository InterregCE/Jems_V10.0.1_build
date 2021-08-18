import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {CallDetailPageStore} from '../call-detail-page-store.service';
import {map} from 'rxjs/operators';
import {CallStateAidDTO} from './CallStateAidDTO';

@Component({
  selector: 'app-call-state-aids',
  templateUrl: './call-state-aids.component.html',
  styleUrls: ['./call-state-aids.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallStateAidsComponent {
  @Input()
  stateAids: CallStateAidDTO[];
  @Input()
  initialStateAids: CallStateAidDTO[];

  @Output()
  selectionChanged = new EventEmitter<void>();

  data$: Observable<{
    userCanApply: boolean,
    callIsReadable: boolean,
    callIsEditable: boolean,
    callIsPublished: boolean,
  }>;

  constructor(private callDetailPageStore: CallDetailPageStore) {
    this.data$ = combineLatest([
      this.callDetailPageStore.userCanApply$,
      this.callDetailPageStore.callIsReadable$,
      this.callDetailPageStore.callIsEditable$,
      this.callDetailPageStore.callIsPublished$,
    ])
      .pipe(
        map(([userCanApply, callIsReadable, callIsEditable, callIsPublished]) => ({userCanApply, callIsReadable, callIsEditable, callIsPublished}))
      );
  }

  stateAidDisabled(callIsEditable: boolean, callIsPublished: boolean, stateAid: CallStateAidDTO): boolean {
    if (!callIsEditable) {
      return true;
    }
    if (callIsPublished) {
      const foundStateAid = this.initialStateAids.find(initialStateAid => initialStateAid.id === stateAid.id);
      return !!(foundStateAid && foundStateAid.selected);
    }
    return false;
  }
}
