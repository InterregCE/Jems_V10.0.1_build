import {ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {MultiLanguageInputService} from '../../../services/multi-language-input.service';
import {BaseComponent} from '@common/components/base-component';
import {takeUntil, tap} from 'rxjs/operators';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';
import {BehaviorSubject, merge, Observable, of, Subject} from 'rxjs';

@Component({
  selector: 'app-multi-language',
  templateUrl: './multi-language.component.html',
  styleUrls: ['./multi-language.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MultiLanguageComponent extends BaseComponent implements OnInit, OnChanges {

  // TODO: remove this input
  @Input()
  inputs: MultiLanguageInput[];

  @Input()
  switchButtonsVisible = true;
  @Input()
  useSystemLanguages = false;
  @Input()
  staticLanguages?: string[];

  languages$: Observable<string[]>;
  currentLanguage$: Subject<string>;
  inputsChanged$ = new Subject<void>();

  constructor(public languageService: MultiLanguageInputService) {
    super();
  }

  ngOnInit(): void {
    // TODO: remove this logic when last usage of inputs input is gone
    this.inputsChanged$.next();
    this.initializeInputs();

    this.languageService.currentInputLanguage$
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.updateControls())
      ).subscribe();

    if (this.staticLanguages?.length) {
      this.languages$ = of(this.staticLanguages);
      this.currentLanguage$ = new BehaviorSubject(this.staticLanguages[0]);
      return;
    }
    this.languages$ = this.useSystemLanguages
      ? this.languageService.systemLanguages$ : this.languageService.inputLanguages$;
    this.currentLanguage$ = this.useSystemLanguages
      ? this.languageService.currentSystemLanguage$ : this.languageService.currentInputLanguage$;
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
    const languages = this.useSystemLanguages
      ? this.languageService.systemLanguages : this.languageService.inputLanguages;
    return (this.staticLanguages?.length && !this.englishLanguageActive(languages)) || languages.length > 1;
  }

  private englishLanguageActive(languages: string[]): boolean {
    return !!languages?.find(lang => 'EN' === lang);
  }
}
