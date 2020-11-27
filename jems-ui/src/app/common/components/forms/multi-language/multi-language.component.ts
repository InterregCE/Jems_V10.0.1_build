import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges
} from '@angular/core';
import {MultiLanguageInputService} from '../../../services/multi-language-input.service';
import {AbstractControl, FormGroup} from '@angular/forms';
import {BaseComponent} from '@common/components/base-component';
import {map, takeUntil, tap} from 'rxjs/operators';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';
import {merge, Observable} from 'rxjs';

@Component({
  selector: 'app-multi-language',
  templateUrl: './multi-language.component.html',
  styleUrls: ['./multi-language.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MultiLanguageComponent extends BaseComponent implements OnInit, OnChanges {

  @Input()
  inputs: { [key: string]: MultiLanguageInput };
  @Input()
  form?: FormGroup;

  constructor(public languageService: MultiLanguageInputService) {
    super();
  }

  ngOnInit(): void {
    if (!this.form || !this.inputs) {
      return;
    }

    const inputValueChanges = Object.keys(this.inputs)
      .map(input => this.getControl(input).valueChanges
        .pipe(
          map(value => ({input, value}))
        )
      ) as Observable<any>[];

    merge(...inputValueChanges)
      .pipe(
        takeUntil(this.destroyed$),
        tap(changed => this.languageService.updateInputValue(
          changed.value, this.inputs[changed.input], this.getControl(changed.input).valid
        ))
      ).subscribe();

    this.languageService.currentLanguage$
      .pipe(
        takeUntil(this.destroyed$),
        tap(language => this.updateControls())
      ).subscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.inputs && this.inputs && this.form) {
      this.updateControls();
    }
  }

  private getControl(name: string): AbstractControl {
    return this.form?.get(name) as AbstractControl;
  }

  private updateControls(): void {
    Object.keys(this.inputs)
      .forEach(input => this.getControl(input).patchValue(
        this.languageService.getInputValue(this.inputs[input]))
      );
  }
}
