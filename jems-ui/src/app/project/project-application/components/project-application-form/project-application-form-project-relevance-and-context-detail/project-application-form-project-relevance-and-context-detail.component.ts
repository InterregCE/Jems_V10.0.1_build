import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {FormState} from '@common/components/forms/form-state';
import {Permission} from 'src/app/security/permissions/permission';
import {InputProjectRelevance, InputProjectRelevanceBenefit, InputProjectRelevanceStrategy, InputProjectRelevanceSynergy} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {ProjectRelevanceBenefit} from './dtos/project-relevance-benefit';
import {Subject} from 'rxjs';
import {ProjectRelevanceStrategy} from './dtos/project-relevance-strategy';
import {ProjectRelevanceSynergy} from './dtos/project-relevance-synergy';

@Component({
  selector: 'app-project-application-form-project-relevance-and-context-detail',
  templateUrl: './project-application-form-project-relevance-and-context-detail.component.html',
  styleUrls: ['./project-application-form-project-relevance-and-context-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormProjectRelevanceAndContextDetailComponent extends ViewEditForm implements OnInit {
  Permission = Permission;

  @Input()
  editable: boolean;
  @Input()
  project: InputProjectRelevance;
  @Input()
  strategiesFromCall: InputProjectRelevanceStrategy.StrategyEnum[];
  @Output()
  updateData = new EventEmitter<InputProjectRelevance>();
  @Output()
  deleteData = new EventEmitter<InputProjectRelevance>();

  editableBenefitsForm = new FormGroup({});
  benefitsDataSource: MatTableDataSource<ProjectRelevanceBenefit>;
  benefitCounter = 1;

  editableStrategyForm = new FormGroup({});
  strategiesDataSource: MatTableDataSource<ProjectRelevanceStrategy>;
  strategyCounter = 1;

  editableSynergyForm = new FormGroup({});
  synergiesDataSource: MatTableDataSource<ProjectRelevanceSynergy>;
  synergyCounter = 1;

  changeTableState$ = new Subject<null>();

  projectRelevanceForm: FormGroup = this.formBuilder.group({
    territorialChallenge: ['', Validators.maxLength(5000)],
    commonChallenge: ['', Validators.maxLength(5000)],
    internationalCooperation: ['', Validators.maxLength(5000)],
    availableKnowledge: ['', Validators.maxLength(5000)]
  });

  territorialChallengeErrors = {
    maxlength: 'project.application.form.relevance.entered.text.size.too.long'
  };
  commonChallengeErrors = {
    maxlength: 'project.application.form.relevance.entered.text.size.too.long'
  };
  internationalCooperationErrors = {
    maxlength: 'project.application.form.relevance.entered.text.size.too.long'
  };
  availableKnowledgeErrors = {
    maxlength: 'project.application.form.relevance.entered.text.size.too.long'
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              private sideNavService: SideNavService) {
    super(changeDetectorRef);
  }

  ngOnInit() {
    super.ngOnInit();
    this.benefitsDataSource = new MatTableDataSource(this.constructBenefitsDataSource());
    this.strategiesDataSource = new MatTableDataSource(this.constructStrategyDataSource());
    this.synergiesDataSource = new MatTableDataSource(this.constructSynergyDataSource());
    this.changeFormState$.next(FormState.VIEW);
  }

  getForm(): FormGroup | null {
    return this.projectRelevanceForm;
  }

  onSubmit(): void {
    this.updateData.emit({
      territorialChallenge: this.projectRelevanceForm.controls.territorialChallenge.value,
      commonChallenge: this.projectRelevanceForm.controls.commonChallenge.value,
      transnationalCooperation: this.projectRelevanceForm.controls.internationalCooperation.value,
      projectBenefits: this.buildBenefitsToSave(),
      projectStrategies: this.buildStrategiesToSave(),
      projectSynergies: this.buildSynergiesToSave(),
      availableKnowledge: this.projectRelevanceForm.controls.availableKnowledge.value,
    });
  }

  saveDeletion(): void {
    this.deleteData.emit({
      territorialChallenge: this.project?.territorialChallenge,
      commonChallenge: this.project?.commonChallenge,
      transnationalCooperation: this.project?.transnationalCooperation,
      projectBenefits: this.buildBenefitsToSave(),
      projectStrategies: this.buildStrategiesToSave(),
      projectSynergies: this.buildSynergiesToSave(),
      availableKnowledge: this.project?.availableKnowledge,
    });
  }

  targetGroup = (id: number): string => id + 'targ';
  specification = (id: number): string => id + 'spec';
  strategy = (id: number): string => id + 'strat';
  contribution = (id: number): string => id + 'con';
  projectInitiative = (id: number): string => id + 'projIn';
  synergy = (id: number): string => id + 'syn';

  private initFields() {
    this.projectRelevanceForm.controls.territorialChallenge.setValue(this.project?.territorialChallenge);
    this.projectRelevanceForm.controls.commonChallenge.setValue(this.project?.commonChallenge);
    this.projectRelevanceForm.controls.internationalCooperation.setValue(this.project?.transnationalCooperation);
    this.projectRelevanceForm.controls.availableKnowledge.setValue(this.project?.availableKnowledge);
  }

  protected enterViewMode(): void {
    this.editableBenefitsForm = new FormGroup({});
    if (!this.benefitsDataSource) {
      return;
    }
    this.benefitsDataSource.data = this.constructBenefitsDataSource();

    this.editableStrategyForm = new FormGroup({});
    if (!this.strategiesDataSource) {
      return;
    }
    this.strategiesDataSource.data = this.constructStrategyDataSource();

    this.editableSynergyForm = new FormGroup({});
    if (!this.synergiesDataSource) {
      return;
    }
    this.synergiesDataSource.data = this.constructSynergyDataSource();

    this.sideNavService.setAlertStatus(false);
    this.initFields();
  }

  protected enterEditMode(): void {
    this.changeTableState$.next();
    this.sideNavService.setAlertStatus(true);
  }

  private constructBenefitsDataSource(): ProjectRelevanceBenefit[] {
    const data: ProjectRelevanceBenefit[] = [];
    this.benefitCounter = 1;
    this.project?.projectBenefits.forEach((element) => {
      data.push({
        id: this.benefitCounter,
        targetGroup: element.group,
        specification: element.specification
      } as ProjectRelevanceBenefit);
      this.benefitCounter = this.benefitCounter + 1;
    })
    return data;
  }

  private constructStrategyDataSource(): ProjectRelevanceStrategy[] {
    const data: ProjectRelevanceStrategy[] = [];
    this.strategyCounter = 1;
    this.project?.projectStrategies.forEach((element) => {
      data.push({
        id: this.strategyCounter,
        projectStrategy: element.strategy ? element.strategy : 'Other',
        specification: element.specification
      } as ProjectRelevanceStrategy);
      this.strategyCounter = this.strategyCounter + 1;
    })
    return data;
  }

  private constructSynergyDataSource(): ProjectRelevanceSynergy[] {
    const data: ProjectRelevanceSynergy[] = [];
    this.synergyCounter = 1;
    this.project?.projectSynergies.forEach((element) => {
      data.push({
        id: this.synergyCounter,
        specification: element.specification,
        synergy: element.synergy
      } as ProjectRelevanceSynergy);
      this.synergyCounter = this.synergyCounter + 1;
    })
    return data;
  }

  private buildBenefitsToSave(): InputProjectRelevanceBenefit[] {
    return this.benefitsDataSource.data
      .map(element => ({
        group: this.editableBenefitsForm.get(this.targetGroup(element.id))?.value,
        specification: this.editableBenefitsForm.get(this.specification(element.id))?.value
      }))
  }

  private buildStrategiesToSave(): InputProjectRelevanceStrategy[] {
    return this.strategiesDataSource.data
      .map(element => ({
        strategy: this.editableStrategyForm.get(this.strategy(element.id))?.value !== 'Other'
          ? this.editableStrategyForm.get(this.strategy(element.id))?.value
          : null,
        specification: this.editableStrategyForm.get(this.contribution(element.id))?.value
      }))
  }

  private buildSynergiesToSave(): InputProjectRelevanceSynergy[] {
    return this.synergiesDataSource.data
      .map(element => ({
        specification: this.editableSynergyForm.get(this.projectInitiative(element.id))?.value,
        synergy: this.editableSynergyForm.get(this.synergy(element.id))?.value
      }))
      .filter(element => element.specification || element.synergy);
  }
}
