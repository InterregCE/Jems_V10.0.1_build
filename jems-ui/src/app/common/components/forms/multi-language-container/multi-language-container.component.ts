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
import {map, startWith, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {combineLatest} from 'rxjs';
import {MultiLanguageFormFieldComponent} from '@common/components/forms/multi-language-form-field/multi-language-form-field.component';
import {MultiLanguageContainerService} from '@common/components/forms/multi-language-container/multi-language-container.service';
import {INPUT_STATE} from '@common/components/forms/multi-language-container/multi-language-input-state';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-multi-language-container',
  templateUrl: './multi-language-container.component.html',
  styleUrls: ['./multi-language-container.component.scss'],
  providers: [MultiLanguageContainerService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MultiLanguageContainerComponent implements OnInit, AfterContentInit {

  @ContentChildren(MultiLanguageFormFieldComponent, {descendants: true})
  children: QueryList<MultiLanguageFormFieldComponent>;

  @Input()
  switchButtonsVisible = true;
  @Input()
  useSystemLanguages = false;
  @Input()
  staticLanguages?: string[];

  states: { [key: string]: INPUT_STATE } = {};

  constructor(public multiLanguageContainerService: MultiLanguageContainerService,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.multiLanguageContainerService.init(this.useSystemLanguages, this.staticLanguages);
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
        withLatestFrom(this.multiLanguageContainerService.languages$),
        tap(([states, languages]) =>
          this.states = Object.fromEntries(
            languages.map(lang => [lang, this.getLanguageState(lang, states as { [key: string]: INPUT_STATE }[])])
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

  isMoreThanOneLanguageEnabled(languages: string[]): boolean {
    return (this.staticLanguages?.length && !this.englishLanguageActive(languages))
      || languages.length > 1;
  }

  private englishLanguageActive(languages: string[]): boolean {
    return !!languages?.find(lang => 'EN' === lang);
  }
}
