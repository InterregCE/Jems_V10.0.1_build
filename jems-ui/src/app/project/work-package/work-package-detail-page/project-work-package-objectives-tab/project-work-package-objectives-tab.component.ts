import {ChangeDetectionStrategy, Component, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {OutputWorkPackage} from '@cat/api';
import {ActivatedRoute, Router} from '@angular/router';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, take, tap, withLatestFrom} from 'rxjs/operators';
import {ProjectApplicationFormSidenavService} from '../../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ProjectWorkPackagePageStore} from '../project-work-package-page-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Observable} from 'rxjs';
import {ProjectStore} from '../../../project-application/containers/project-application-detail/services/project-store.service';

@UntilDestroy()
@Component({
  selector: 'app-project-work-package-objectives-tab',
  templateUrl: './project-work-package-objectives-tab.component.html',
  styleUrls: ['./project-work-package-objectives-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectWorkPackageObjectivesTabComponent implements OnInit, OnChanges {

  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  workPackage$: Observable<OutputWorkPackage | any>;
  workPackageId: number;
  workPackageNumber: number;

  form: FormGroup = this.formBuilder.group({
    number: [''],
    name: ['', Validators.maxLength(100)],
    specificObjective: ['', Validators.maxLength(250)],
    objectiveAndAudience: ['', Validators.maxLength(500)],
  });

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              public workPackageStore: ProjectWorkPackagePageStore,
              private projectStore: ProjectStore,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService) {
    this.projectStore.init(this.projectId);
  }

  ngOnInit(): void {
    this.formService.init(this.form, this.workPackageStore.isProjectEditable$);

    this.workPackage$ = this.workPackageStore.workPackage$
      .pipe(
        tap(workPackage => this.workPackageId = workPackage.id),
        tap(workPackage => this.workPackageNumber = workPackage.number),
        tap(workPackage => this.resetForm(workPackage))
      );

    this.formService.reset$
      .pipe(
        withLatestFrom(this.workPackage$),
        tap(([reset, investment]) => {
          if (this.workPackageId) {
            this.resetForm(investment);
            return;
          }
          this.redirectToWorkPackageOverview();
        }),
        untilDestroyed(this)
      ).subscribe();

    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.workPackage) {
      this.resetForm();
    }
  }

  onSubmit(): void {
    if (!this.workPackageId) {
      this.workPackageStore.createWorkPackage(this.form.value)
        .pipe(
          take(1),
          tap(saved => this.redirectToWorkPackageDetail()),
          tap(() => this.projectApplicationFormSidenavService.refreshPackages(this.projectId)),
          catchError(error => this.formService.setError(error))
        ).subscribe();
      return;
    }
    this.workPackageStore.saveWorkPackage({
      id: this.workPackageId,
      ...this.form.value
    })
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.form.workpackage.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  private resetForm(existing?: OutputWorkPackage): void {
    this.formService.setCreation(!this.workPackageId);
    this.form.get('number')?.patchValue(existing?.number || this.workPackageNumber);
    this.form.get('name')?.patchValue(existing?.name || []);
    this.form.get('specificObjective')?.patchValue(existing?.specificObjective || []);
    this.form.get('objectiveAndAudience')?.patchValue(existing?.objectiveAndAudience || []);
    this.formService.resetEditable();
    this.form.controls.number.disable();
  }

  private redirectToWorkPackageOverview(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId, 'applicationFormWorkPackage']);
  }

  private redirectToWorkPackageDetail(): void {
    this.router.navigate([
      'app', 'project', 'detail', this.projectId, 'applicationFormWorkPackage', 'detail', this.workPackageId
    ]);
  }
}
