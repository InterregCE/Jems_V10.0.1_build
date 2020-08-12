import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ProjectStore} from '../services/project-store.service';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Forms} from '../../../../../common/utils/forms';
import {filter, take, takeUntil} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {FormState} from '@common/components/forms/form-state';
import {ActivatedRoute} from '@angular/router';
import {Permission} from '../../../../../security/permissions/permission';
import {OutputProjectStatus} from '@cat/api';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {HeadlineType} from '@common/components/side-nav/headline-type';


@Component({
  selector: 'app-project-application-form-page',
  templateUrl: './project-application-form-page.component.html',
  styleUrls: ['./project-application-form-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPageComponent extends ViewEditForm implements OnInit {
  Permission = Permission;
  OutputProjectStatus = OutputProjectStatus;

  project$ = this.projectStore.getProject();
  projectId = this.activatedRoute.snapshot.params.projectId;
  projectAcronym: string;

  applicationForm = this.formBuilder.group({});
  identificationForm: FormGroup = this.formBuilder.group({
    projectId:[''],
    projectAcronym: ['', Validators.compose([
      Validators.maxLength(25),
      Validators.required])],
    projectTitle: ['', Validators.maxLength(250)],
    projectDuration: ['', Validators.compose([
      Validators.max(999),
      Validators.min(0)])],
  });
  summaryForm: FormGroup = this.formBuilder.group({
    projectSummary: ['', Validators.maxLength(2000)]
  });

  constructor(private projectStore: ProjectStore,
              private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private activatedRoute:ActivatedRoute,
              protected changeDetectorRef: ChangeDetectorRef,
              private sideNavService: SideNavService) {
    super(changeDetectorRef);
  }

  ngOnInit() {
    super.ngOnInit();
    this.projectStore.init(this.projectId);

    this.getProjectAcronym();
    this.sideNavService.setHeadlines(this.destroyed$,[
      new HeadlineRoute('back.project.overview', '/project/' + this.projectId, HeadlineType.BACKROUTE),
      new HeadlineRoute('project.application.form.title', '', HeadlineType.TITLE),
      new HeadlineRoute(this.projectAcronym, '', HeadlineType.SUBTITLE),
      new HeadlineRoute('project.application.form.section.part.a', 'applicationFormHeading', HeadlineType.SECTION),
      new HeadlineRoute('project.application.form.section.part.a.subsection.one', 'projectIdentificationHeading', HeadlineType.SUBSECTION),
      new HeadlineRoute('project.application.form.section.part.a.subsection.two', 'projectSummaryHeading', HeadlineType.SUBSECTION),]);
  }

  getProjectAcronym(): void{
    this.project$.subscribe(data => this.projectAcronym = data.acronym)
  }

  getForm(): FormGroup | null {
    return this.applicationForm;
  }

  onSubmit():void {
    Forms.confirmDialog(
      this.dialog,
      'project.application.form.submit.dialog.title',
      'project.application.form.submit.dialog.message'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(yes => !!yes)
    ).subscribe(() => {
      this.changeFormState$.next(FormState.VIEW);
      // TODO add the save mechanic once the backend endpoints are done.
    });
  }
}
