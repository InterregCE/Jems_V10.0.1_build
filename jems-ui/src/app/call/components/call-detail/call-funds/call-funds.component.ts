import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {ProgrammeFundDTO} from '@cat/api';
import {LanguageService} from '../../../../common/services/language.service';

@Component({
  selector: 'app-call-funds',
  templateUrl: './call-funds.component.html',
  styleUrls: ['./call-funds.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallFundsComponent {
  @Input()
  disabled: boolean;
  @Input()
  funds: ProgrammeFundDTO[];
  @Input()
  isApplicant: boolean;

  @Output()
  selectionChanged = new EventEmitter<void>();

  constructor(public languageService: LanguageService) {
  }
}
