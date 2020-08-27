import {Component, EventEmitter, OnInit} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {ActivatedRoute} from '@angular/router';
import {WorkPackageService, InputWorkPackageUpdate, InputWorkPackageCreate} from '@cat/api'
import {combineLatest, merge, of, Subject} from 'rxjs';
import {catchError, flatMap, map, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {I18nValidationError} from '@common/validation/i18n-validation-error';

@Component({
  selector: 'app-project-application-form-work-package',
  templateUrl: './project-application-form-work-package.component.html',
  styleUrls: ['./project-application-form-work-package.component.scss']
})
export class ProjectApplicationFormWorkPackageComponent extends BaseComponent implements OnInit {
  workPackageId = this.activatedRoute?.snapshot?.params?.workPackageId;
  projectId = 2;

  saveError$ = new Subject<I18nValidationError | null>();
  saveSuccess$ = new Subject<boolean>();
  updateWorkPackageData$ = new EventEmitter<InputWorkPackageUpdate>()
  createWorkPackageData$ = new EventEmitter<InputWorkPackageCreate>()

  constructor( private workPackageService: WorkPackageService,
               private activatedRoute: ActivatedRoute) {
    super();
  }

  private updatedWorkPackageData$ = this.updateWorkPackageData$
    .pipe(
      flatMap((data) => this.workPackageService.updateWorkPackage(data)),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      tap(saved => Log.info('Updated work package data:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.saveError$.next(error.error);
        throw error;
      })
    );

  private createdWorkPackageData$ = this.createWorkPackageData$
    .pipe(
      flatMap((data) => this.workPackageService.createWorkPackage(data)),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      tap(saved => Log.info('Created work package data:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.saveError$.next(error.error);
        throw error;
      })
    );

  private workPackageDetails$ = merge(
    this.workPackageId
      ?  this.workPackageService.getWorkPackageById(this.workPackageId)
      : of({}),
    this.updatedWorkPackageData$,
    this.createdWorkPackageData$
  )
    .pipe(
      takeUntil(this.destroyed$),
      map(workPackage => ({
        workPackage,
        editable: true
      })),
    );

  details$  = combineLatest ([
    this.workPackageDetails$,
    this.workPackageService.getWorkPackageNumberForProjectId(this.projectId),
   ])
    .pipe(
      map(
      ([workPackage, workPackageNumber]) => ({workPackage, workPackageNumber})
      )
    )

  ngOnInit(): void {
  }

}
