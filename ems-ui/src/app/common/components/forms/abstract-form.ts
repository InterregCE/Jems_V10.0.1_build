import {ChangeDetectorRef, Input, OnDestroy, OnInit} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {FormGroup} from '@angular/forms';
import {I18nValidationError} from '../../validation/i18n-validation-error';
import {takeUntil} from 'rxjs/operators';

export abstract class AbstractForm implements OnDestroy, OnInit {

  @Input()
  error$: Observable<I18nValidationError | null>;
  @Input()
  success$: Observable<boolean>;

  validationError?: string;
  submitted = false;

  destroyed$ = new Subject();

  protected constructor(protected changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnDestroy() {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  abstract getForm(): FormGroup | null;

  abstract isClearable(): boolean | null;

  ngOnInit(): void {
    const formGroup = this.getForm();
    if (!formGroup) {
      return;
    }

    if (this.error$) {
      this.handleError(formGroup);
    }

    if (this.success$) {
      this.handleSuccess(formGroup);
    }
  }

  private handleError(formGroup: FormGroup): void {
    this.error$
      .pipe(takeUntil(this.destroyed$))
      .subscribe((error: I18nValidationError) => {
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

  private handleSuccess(formGroup: FormGroup): void {
    this.success$
      .pipe(takeUntil(this.destroyed$))
      .subscribe(() => {
        this.submitted = false;
        if (this.isClearable()) {
          formGroup.reset();
          Object.keys(formGroup.controls).forEach(key => {
            formGroup.get(key)?.setErrors(null);
          });
        }
      });
  }

}
