import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Input,
  OnInit
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InputProjectData, OutputProject} from '@cat/api';
import {Permission} from '../../../../security/permissions/permission';
import {Tools} from '../../../../common/utils/tools';
import {catchError, distinctUntilChanged, take, takeUntil, tap} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectStore} from '../../containers/project-application-detail/services/project-store.service';

@Component({
  selector: 'app-project-application-form',
  templateUrl: './project-application-form.component.html',
  styleUrls: ['./project-application-form.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormComponent extends BaseComponent implements OnInit {
  Permission = Permission;
  tools = Tools;

  @Input()
  project: OutputProject;
  @Input()
  editable: boolean;
  @Input()
  priorities: string[];
  @Input()
  objectivesWithPolicies: { [key: string]: InputProjectData.SpecificObjectiveEnum[] };

  currentPriority?: string;
  previousObjective: InputProjectData.SpecificObjectiveEnum;
  currentObjectives: any = [];
  selectedSpecificObjective: InputProjectData.SpecificObjectiveEnum;

  applicationForm: FormGroup = this.formBuilder.group({
    projectId: [''],
    acronym: ['', Validators.compose([
      Validators.maxLength(25),
      Validators.required])
    ],
    title: ['', Validators.maxLength(250)],
    duration: ['', Validators.compose([
      Validators.max(999),
      Validators.min(1)
    ])],
    projectPeriodLength: [''],
    projectPeriodCount: [''],
    introProgrammeLanguage: ['', Validators.maxLength(2000)],
    programmePriority: ['', Validators.required],
    specificObjective: ['', Validators.required]
  });

  projectAcronymErrors = {
    maxlength: 'project.acronym.size.too.long',
    required: 'project.acronym.should.not.be.empty'
  };
  projectTitleErrors = {
    maxlength: 'project.title.size.too.long',
  };
  projectDurationErrors = {
    max: 'project.duration.size.max',
    min: 'project.duration.size.max',
  };
  projectSummaryErrors = {
    maxlength: 'project.summary.size.too.long'
  };
  programmePriorityErrors = {
    required: 'project.priority.should.not.be.empty'
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              public projectStore: ProjectStore,
              private formService: FormService) {
    super();
  }

  ngOnInit(): void {
    this.formService.init(this.applicationForm);
    this.resetForm();
    if (this.editable) {
      this.applicationForm.controls.projectId.disable();
      this.applicationForm.controls.projectPeriodLength.disable();
      this.applicationForm.controls.projectPeriodCount.disable();
    } else {
      this.applicationForm.disable();
    }

    this.applicationForm.controls.duration?.valueChanges
      .pipe(
        takeUntil(this.destroyed$),
        distinctUntilChanged()
      )
      .subscribe(duration =>
        this.applicationForm.controls.projectPeriodCount.setValue(this.projectPeriodCount(duration))
      );
  }

  save(): void {
    const data = {
      acronym: this.applicationForm.controls.acronym.value,
      title: this.applicationForm.controls.title.value,
      duration: this.applicationForm.controls.duration.value,
      introProgrammeLanguage: this.applicationForm.controls.introProgrammeLanguage.value,
      intro: '',
      specificObjective: this.selectedSpecificObjective
    };
    this.projectStore.updateProjectData(data)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.form.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  resetForm(): void {
    this.applicationForm.controls.projectId.setValue(this.project.id);
    this.applicationForm.controls.acronym.setValue(this.project.acronym);
    this.applicationForm.controls.title.setValue(this.project?.projectData?.title);
    this.applicationForm.controls.duration.setValue(this.project?.projectData?.duration);
    this.applicationForm.controls.projectPeriodLength.setValue(this.project?.call.lengthOfPeriod);
    this.applicationForm.controls.projectPeriodCount.setValue(
      this.projectPeriodCount(this.project?.projectData?.duration)
    );
    this.applicationForm.controls.introProgrammeLanguage.setValue(this.project?.projectData?.introProgrammeLanguage);
    if (this.project?.projectData?.specificObjective) {
      this.previousObjective = this.project?.projectData?.specificObjective.programmeObjectivePolicy;
      this.selectedSpecificObjective = this.project?.projectData?.specificObjective.programmeObjectivePolicy;
      const prevPriority = this.project?.projectData?.programmePriority.code
        + ' - ' + this.project?.projectData?.programmePriority.title;
      this.currentPriority = prevPriority;
      this.applicationForm.controls.programmePriority.setValue(prevPriority);
      this.applicationForm.controls.specificObjective.setValue(this.selectedSpecificObjective);
    } else {
      this.currentPriority = undefined;
      this.applicationForm.controls.programmePriority.setValue(null);
      this.applicationForm.controls.specificObjective.setValue(null);
    }
  }

  private projectPeriodCount(projectDuration: number): number {
    return projectDuration ? Math.ceil(projectDuration / this.project?.call.lengthOfPeriod) : 0;
  }

  changeCurrentPriority(selectedPriority: string): void {
    this.currentPriority = selectedPriority;
    this.applicationForm.controls.specificObjective.setValue('');
  }
}
