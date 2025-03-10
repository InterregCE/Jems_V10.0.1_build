import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {FormArray, FormGroup} from '@angular/forms';
import {I18nLabel} from '@common/i18n/i18n-label';
import {Log} from '@common/utils/log';
import {filter, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {HttpErrorResponse} from '@angular/common/http';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {TranslateService} from '@ngx-translate/core';
import {APIError} from '@common/models/APIError';
import {v4 as uuid} from 'uuid';

@UntilDestroy()
@Injectable()
export class FormService {
  private resetSubject = new Subject();
  private editable = true;
  private serviceId = uuid();

  form: FormGroup | FormArray;
  saveLabel = 'common.save.label';
  valid$ = new ReplaySubject<boolean>(1);
  dirty$ = new ReplaySubject<boolean>(1);
  success$ = new Subject<I18nLabel | string | null>();
  pending$ = new BehaviorSubject<boolean>(false);
  error$ = new Subject<APIError | null>();
  fileSizeOverLimitError$ = new BehaviorSubject<number | null>(null);
  reset$ = this.resetSubject.asObservable();
  showMenu$ = new BehaviorSubject<boolean>(true);

  constructor(
    private routingService: RoutingService,
    private translateService: TranslateService,
    ) {
  }

  init(form: FormGroup | FormArray, editable$?: Observable<boolean>): void {
    this.form = form;
    this.form?.valueChanges
      .pipe(
        filter(() => this.form.dirty),
        tap(() => this.setDirty(true)),
      ).subscribe();

    if (!editable$) {
      return;
    }
    editable$
      .pipe(
        tap(editable => {
          this.editable = editable;
          this.setEditable(editable);
        }),
        untilDestroyed(this)
      ).subscribe();
  }

  setError(httpError: HttpErrorResponse | null): Observable<any> {
    if (!httpError) {
      this.error$.next(null);
      return of(null);
    }

    const apiError = httpError.error || {i18nMessage: {}};
    this.error$.next(apiError);
    this.pending$.next(false);
    this.success$.next(null);
    this.setValid(false);
    if (!apiError.i18nMessage?.i18nKey && !apiError?.message) {
      apiError.i18nMessage.i18nKey = 'incomplete.form';
    }
    this.setFieldErrors(apiError);

    throw httpError;
  }

  checkFileSizeError(size: number, maximumAllowedFileSizeInMB: number): boolean {
      if (size > maximumAllowedFileSizeInMB * 1024 * 1024) {
          this.fileSizeOverLimitError$.next(maximumAllowedFileSizeInMB);
          return true;
      }
      return false;
  }

  setSuccess(message: I18nLabel | string | null): void {
    this.success$.next(message);
    this.error$.next(null);
    this.fileSizeOverLimitError$.next(null);
    this.pending$.next(false);
    if (message) {
      setTimeout(() => this.success$.next(null), 3000);
    }
  }

  setDirty(dirty: boolean): void {
    if (!dirty) {
      // mark form as pristine in order to ignore 'dirty' statuses from formChanged$
      if (this.form) {
        this.form.markAsPristine();
        this.form.markAsUntouched();
      }
      this.routingService.confirmLeaveSet.delete(this.serviceId);
    } else if (this.saveLabel !== 'common.create.label') {
      // confirm page leave unless the form is in create mode
      this.routingService.confirmLeaveSet.add(this.serviceId);
    }
    if(dirty){
      this.setSuccess(null);
    }
    this.setValid(!this.form?.invalid);
    this.dirty$.next(dirty);
  }

  setValid(valid: boolean): void {
    this.valid$.next(valid);
  }

  setCreation(isCreationForm: boolean): void {
    this.saveLabel = isCreationForm ? 'common.create.label' : 'common.save.label';
    this.setDirty(isCreationForm);
  }

  setEditable(editable: boolean): void {
    if (editable) {
      this.form?.enable();
    } else {
      this.form?.disable();
    }
    this.editable = editable;
  }

  isEditable(): boolean {
    return this.editable;
  }

  resetEditable(): void {
    this.setEditable(this.editable);
    this.setDirty(false);
  }

  reset(): void {
    this.resetSubject.next();
  }

  private setFieldErrors(error: APIError): void {
    if (!this.form || this.form instanceof FormArray) {
      return;
    }
    Log.debug('Set form backend errors.', this, error);
    Object.keys(this.form?.controls).forEach(key => {
      if (!error?.formErrors || !error.formErrors[key]) {
        return;
      }
      (this.form as FormGroup)?.controls[key].setErrors({error: this.translateService.instant(error.formErrors[key].i18nKey, error.formErrors[key].i18nArguments)});
      (this.form as FormGroup)?.controls[key].markAsTouched();
    });
  }

  setShowMenu(value: boolean): void {
    this.showMenu$.next(value);
  }
}
