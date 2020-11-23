import {ChangeDetectionStrategy, Component} from '@angular/core';
import {MultiLanguageInputService} from '../../../services/multi-language-input.service';

@Component({
  selector: 'app-multi-language',
  templateUrl: './multi-language.component.html',
  styleUrls: ['./multi-language.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MultiLanguageComponent {

  constructor(public languageService: MultiLanguageInputService) {
  }
}
