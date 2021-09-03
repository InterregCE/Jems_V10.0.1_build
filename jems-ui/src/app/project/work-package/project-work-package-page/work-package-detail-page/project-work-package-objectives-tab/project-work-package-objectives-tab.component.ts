import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {OutputWorkPackage} from '@cat/api';
import {ActivatedRoute, Router} from '@angular/router';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, take, tap} from 'rxjs/operators';
import {WorkPackagePageStore} from '../project-work-package-page-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Observable} from 'rxjs';
import {ProjectApplicationFormSidenavService} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';

@UntilDestroy()
@Component({
  selector: 'app-project-work-package-objectives-tab',
  templateUrl: './project-work-package-objectives-tab.component.html',
  styleUrls: ['./project-work-package-objectives-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectWorkPackageObjectivesTabComponent {

  APPLICATION_FORM = APPLICATION_FORM;
  workPackage$: Observable<OutputWorkPackage | any>;
  projectId: number;
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
              public workPackageStore: WorkPackagePageStore,
              private projectStore: ProjectStore,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService) {
    this.formService.init(this.form);

    this.workPackageStore.isProjectEditable$.pipe(
        tap(editable => this.formService.setEditable(editable)),
        tap(() => this.form.controls.number.disable())
    ).subscribe();

    this.workPackage$ = this.workPackageStore.workPackage$
      .pipe(
        tap(workPackage => this.workPackageId = workPackage.id),
        tap(workPackage => this.workPackageNumber = workPackage.number),
        tap(workPackage => this.resetForm(workPackage))
      );

    this.projectStore.projectId$
      .pipe(
        tap(projectId => this.projectId = projectId),
        untilDestroyed(this)
      ).subscribe();
  }

  onSubmit(): void {
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

  discard(workPackage?: OutputWorkPackage): void {
    if (!workPackage?.id) {
      this.redirectToWorkPackageOverview();
      return;
    }
    this.resetForm(workPackage);
  }

  private resetForm(existing?: OutputWorkPackage): void {
    this.form.get('number')?.patchValue(existing?.number || this.workPackageNumber);
    this.form.get('name')?.patchValue(existing?.name || []);
    this.form.get('specificObjective')?.patchValue(existing?.specificObjective || []);
    this.form.get('objectiveAndAudience')?.patchValue(existing?.objectiveAndAudience || []);
    this.formService.resetEditable();
    this.form.controls.number.disable();
  }

  private redirectToWorkPackageOverview(): void {
    this.router.navigate(['..'], {relativeTo: this.activatedRoute});
  }

}
