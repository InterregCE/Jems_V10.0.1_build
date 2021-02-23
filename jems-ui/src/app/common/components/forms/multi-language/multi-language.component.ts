import {
  AfterContentInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ContentChildren,
  Input,
  OnInit,
  QueryList
} from '@angular/core';
import {MultiLanguageInputService} from '../../../services/multi-language-input.service';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {combineLatest, Observable, Subject} from 'rxjs';
import {MultiLanguageFormFieldComponent} from '@common/components/forms/multi-language-form-field/multi-language-form-field.component';
import {MultiLanguageComponentService} from '@common/components/forms/multi-language/multi-language-component.service';
import {INPUT_STATE} from '@common/components/forms/multi-language/multi-language-input-state';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-multi-language',
  templateUrl: './multi-language.component.html',
  styleUrls: ['./multi-language.component.scss'],
  providers: [MultiLanguageComponentService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MultiLanguageComponent implements OnInit, AfterContentInit {

  @ContentChildren(MultiLanguageFormFieldComponent, {descendants: true})
  children: QueryList<MultiLanguageFormFieldComponent>;

  @Input()
  switchButtonsVisible = true;
  @Input()
  useSystemLanguages = false;
  @Input()
  staticLanguages?: string[];

  languages$: Observable<string[]>;
  currentLanguage$: Subject<string>;
  states: { [key: string]: INPUT_STATE } = {};

  constructor(public languageService: MultiLanguageInputService,
              public componentService: MultiLanguageComponentService,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.componentService.init(this.useSystemLanguages, this.staticLanguages);
    this.languages$ = this.componentService.languages$;
    this.currentLanguage$ = this.componentService.currentLanguage$;
  }

  isEmptyState(language: string): boolean {
    return this.states[language] === INPUT_STATE.EMPTY;
  }

  isErrorState(language: string): boolean {
    return this.states[language] === INPUT_STATE.INVALID;
  }

  getBadgeContent(language: string): string {
    if (!Object.keys(this.states).length) {
      return '';
    }
    return this.states[language] !== INPUT_STATE.VALID ? '!' : '';
  }

  ngAfterContentInit(): void {
    this.children.changes
      .pipe(
        startWith(this.children),
        map(children => children?._results || children || []),
        switchMap(children => combineLatest([
          ...children.map((child: MultiLanguageFormFieldComponent) => child.state$)
        ])),
        tap((states: { [key: string]: INPUT_STATE }[]) =>
          this.states = Object.fromEntries(
            this.componentService.languages.map(lang => [lang, this.getLanguageState(lang, states)])
          )
        ),
        tap(() => this.changeDetectorRef.markForCheck()),
        untilDestroyed(this)
      ).subscribe();
  }


  private getLanguageState(language: string, fieldStates: { [key: string]: INPUT_STATE }[]): INPUT_STATE {
    if (fieldStates.find(fieldState => fieldState[language] === INPUT_STATE.INVALID)) {
      return INPUT_STATE.INVALID;
    }
    if (fieldStates.find(fieldState => fieldState[language] === INPUT_STATE.EMPTY)) {
      return INPUT_STATE.EMPTY;
    }
    return INPUT_STATE.VALID;
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
