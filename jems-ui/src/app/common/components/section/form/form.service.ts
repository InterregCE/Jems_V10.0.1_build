import {Injectable} from '@angular/core';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {ReplaySubject, Subject} from 'rxjs';
import {FormGroup} from '@angular/forms';
import {I18nLabel} from '../../../i18n/i18n-label';
import {Log} from '../../../utils/log';
import {filter, tap} from 'rxjs/operators';
import {RoutingService} from '../../../services/routing.service';

@Injectable()
export class FormService {
  private form: FormGroup;

  valid$ = new ReplaySubject<boolean>(1);
  dirty$ = new Subject<boolean>();
  success$ = new Subject<I18nLabel | string | null>();
  error$ = new Subject<I18nValidationError | null>();

  constructor(private routingService: RoutingService) {
  }

  init(form: FormGroup): void {
    this.form = form;
    this.form.valueChanges
      .pipe(
        filter(() => this.form.dirty),
        tap(() => this.valid$.next(this.form.valid)),
        tap(() => this.setDirty(true)),
        tap(() => this.success$.next(null)),
      ).subscribe();
  }

  setError(error: I18nValidationError | null): void {
    this.error$.next(error);
    if (error) {
      this.success$.next(null);
      if (!error?.i18nKey) {
        (error as any).i18nKey = 'incomplete.form';
      }
      this.setFieldErrors(error);
    }
  }

  setSuccess(message: I18nLabel | string | null): void {
    this.success$.next(message);
    this.error$.next(null);
    setTimeout(() => this.success$.next(null), 3000);
  }

  setDirty(dirty: boolean): void {
    if (!dirty) {
      // mark form as pristine in order to ignore 'dirty' statuses from formChanged$
      this.form.markAsPristine();
      this.routingService.confirmLeave = false;
    } else {
      this.routingService.confirmLeave = true;
    }
    this.dirty$.next(dirty);
  }

  private setFieldErrors(error: I18nValidationError): void {
    Log.debug('Set form backend errors.', this, error);
    Object.keys(this.form.controls).forEach(key => {
      if (!error?.i18nFieldErrors || !error.i18nFieldErrors[key]) {
        return;
      }
      this.form.controls[key].setErrors({i18nError: error.i18nFieldErrors[key].i18nKey});
      this.form.controls[key].markAsTouched();
    });
  }
}
