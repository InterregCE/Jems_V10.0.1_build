import {ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {MultiLanguageInputService} from '../../../services/multi-language-input.service';
import {BaseComponent} from '@common/components/base-component';
import {takeUntil, tap} from 'rxjs/operators';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';
import {merge, Subject} from 'rxjs';
import {OutputProgrammeLanguage} from '@cat/api';

@Component({
  selector: 'app-multi-language',
  templateUrl: './multi-language.component.html',
  styleUrls: ['./multi-language.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MultiLanguageComponent extends BaseComponent implements OnInit, OnChanges {
  LANGUAGE = OutputProgrammeLanguage.CodeEnum;

  @Input()
  inputs: MultiLanguageInput[];
  @Input()
  languages?: OutputProgrammeLanguage.CodeEnum[];
  @Input()
  staticLanguages?: OutputProgrammeLanguage.CodeEnum[];

  inputsChanged$ = new Subject<void>();

  constructor(public languageService: MultiLanguageInputService) {
    super();
  }

  ngOnInit(): void {
    this.inputsChanged$.next();
    this.initializeInputs();

    this.languageService.currentLanguage$
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.updateControls())
      ).subscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.inputs) {
      this.inputsChanged$.next();
      this.initializeInputs();
    }
  }

  private initializeInputs(): void {
    if (!this.inputs?.length) {
      return;
    }
    this.inputs.forEach(input => {
      input.formControl?.valueChanges
        .pipe(
          takeUntil(
            // unsubscribe when inputs are changed or the component is destroyed
            merge(this.inputsChanged$, this.destroyed$)
          ),
          tap(value => this.languageService.updateInputValue(value, input, input.formControl?.valid))
        )
        .subscribe();
    });
    this.updateControls();
  }

  private updateControls(): void {
    if (!this.inputs?.length) {
      return;
    }
    this.inputs.forEach(input => input.formControl?.patchValue(this.languageService.getInputValue(input)));
  }

  isMoreThanOneLanguageInputEnabled(): boolean {
    return (this.staticLanguages?.length && !this.englishLanguageActive())
      || this.languageService.languages.length > 1;
  }

  private englishLanguageActive(): boolean {
    return !!this.languageService.languages?.find(lang => this.LANGUAGE.EN === lang);
  }
}
