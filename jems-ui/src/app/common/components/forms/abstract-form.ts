import {ChangeDetectorRef, Input, OnInit} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {FormGroup} from '@angular/forms';
import {I18nValidationError} from '../../validation/i18n-validation-error';
import {filter, takeUntil} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';
import {Log} from '../../utils/log';
import {Alert} from './alert';

export abstract class AbstractForm extends BaseComponent implements OnInit {
  Alert = Alert;

  @Input()
  error$: Observable<I18nValidationError | null>;
  @Input()
  success$: Observable<any>;

  showSuccessMessage$ = new Subject<boolean>();
  submitted = false;
  clearOnSuccess = false;
  permanentSuccessAlert = false;

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
        this.submitted = false;
        const formGroup = this.getForm();
        if (!formGroup) {
          return;
        }
        Log.debug('Set form backend errors.', this, error);
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
      .pipe(
        takeUntil(this.destroyed$),
        filter(success => !!success)
      )
      .subscribe(() => {
        this.submitted = false;

        Log.debug('Show success message for 4 seconds.', this);
        this.showSuccessMessage$.next(true);
        if (!this.permanentSuccessAlert) {
          setTimeout(() => this.showSuccessMessage$.next(false), 4000);
        }

        this.handleClearFormOnSuccess();
      });
  }

  private handleClearFormOnSuccess(): void {
    if (!this.clearOnSuccess) {
      return;
    }
    const formGroup = this.getForm();
    if (!formGroup) {
      return;
    }
    Log.debug('Reset form and errors.', this);
    formGroup.reset();
    Object.keys(formGroup.controls).forEach(key => {
      formGroup.get(key)?.setErrors(null);
    });
    formGroup.setErrors([]); // marks the form as invalid without marking the fields with errors.
  }

}
