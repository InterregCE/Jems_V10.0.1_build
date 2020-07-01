import {ChangeDetectorRef, Input, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {FormGroup} from '@angular/forms';
import {I18nValidationError} from '../../validation/i18n-validation-error';
import {takeUntil} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';

export abstract class AbstractForm extends BaseComponent implements OnInit {

  @Input()
  error$: Observable<I18nValidationError | null>;
  @Input()
  success$: Observable<boolean>;

  validationError?: string;
  submitted = false;
  clearOnSuccess = false;

  protected constructor(protected changeDetectorRef: ChangeDetectorRef) {
    super();
  }

  abstract getForm(): FormGroup | null;

  ngOnInit(): void {
    if (this.error$) {
      this.handleError();
    }

    if (this.success$) {
      this.handleSuccess();
    }
  }

  private handleError(): void {
    this.error$
      .pipe(takeUntil(this.destroyed$))
      .subscribe((error: I18nValidationError) => {
        const formGroup = this.getForm();
        if (!formGroup) {
          return;
        }
        this.submitted = false;
        this.validationError = error?.i18nKey;
        Object.keys(formGroup.controls).forEach(key => {
          if (!error?.i18nFieldErrors || !error.i18nFieldErrors[key]) {
            return;
          }
          formGroup.controls[key].setErrors({i18nError: error.i18nFieldErrors[key].i18nKey});
          formGroup.controls[key].markAsTouched();
          this.changeDetectorRef.markForCheck();
        });

      });
  }

  private handleSuccess(): void {
    this.success$
      .pipe(takeUntil(this.destroyed$))
      .subscribe(() => {
        this.submitted = false;
        if (!this.clearOnSuccess) {
          return;
        }
        const formGroup = this.getForm();
        if (!formGroup) {
          return;
        }
        formGroup.reset();
        Object.keys(formGroup.controls).forEach(key => {
          formGroup.get(key)?.setErrors(null);
        });
      });
  }

}
