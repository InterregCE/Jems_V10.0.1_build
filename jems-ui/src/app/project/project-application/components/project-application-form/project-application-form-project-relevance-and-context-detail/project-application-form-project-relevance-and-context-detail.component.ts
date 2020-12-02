import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  InputProjectRelevance,
  InputProjectRelevanceBenefit,
  InputProjectRelevanceStrategy,
  InputProjectRelevanceSynergy,
  InputTranslation
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {ProjectRelevanceBenefit} from './dtos/project-relevance-benefit';
import {Observable} from 'rxjs';
import {ProjectRelevanceStrategy} from './dtos/project-relevance-strategy';
import {ProjectRelevanceSynergy} from './dtos/project-relevance-synergy';
import {takeUntil, tap} from 'rxjs/operators';
import {FormService} from '@common/components/section/form/form.service';
import {BaseComponent} from '@common/components/base-component';
import {HttpErrorResponse} from '@angular/common/http';
import {MultiLanguageInputService} from '../../../../../common/services/multi-language-input.service';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';

@Component({
  selector: 'app-project-application-form-project-relevance-and-context-detail',
  templateUrl: './project-application-form-project-relevance-and-context-detail.component.html',
  styleUrls: ['./project-application-form-project-relevance-and-context-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormProjectRelevanceAndContextDetailComponent extends BaseComponent implements OnInit {
  // TODO: remove these and adapt the component to save independently
  @Input()
  error$: Observable<HttpErrorResponse | null>;
  @Input()
  success$: Observable<any>;

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

  territorialChallenge: MultiLanguageInput;
  commonChallenge: MultiLanguageInput;
  transnationalCooperation: MultiLanguageInput;

  projectRelevanceForm: FormGroup = this.formBuilder.group({
    territorialChallenge: ['', Validators.maxLength(5000)],
    commonChallenge: ['', Validators.maxLength(5000)],
    transnationalCooperation: ['', Validators.maxLength(5000)],
    availableKnowledge: ['', Validators.maxLength(5000)]
  });

  territorialChallengeErrors = {
    maxlength: 'project.application.form.relevance.entered.text.size.too.long'
  };
  commonChallengeErrors = {
    maxlength: 'project.application.form.relevance.entered.text.size.too.long'
  };
  transnationalCooperationErrors = {
    maxlength: 'project.application.form.relevance.entered.text.size.too.long'
  };
  availableKnowledgeErrors = {
    maxlength: 'project.application.form.relevance.entered.text.size.too.long'
  };

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              public languageService: MultiLanguageInputService) {
    super();
  }

  ngOnInit(): void {
    this.benefitsDataSource = new MatTableDataSource(this.constructBenefitsDataSource());
    this.strategiesDataSource = new MatTableDataSource(this.constructStrategyDataSource());
    this.synergiesDataSource = new MatTableDataSource(this.constructSynergyDataSource());
    this.resetForm();

    this.formService.init(this.projectRelevanceForm);
    this.formService.setAdditionalValidators([this.formValid.bind(this)]);
    this.error$
      .pipe(
        takeUntil(this.destroyed$),
        tap(err => this.formService.setError(err))
      )
      .subscribe();
    this.success$
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.formService.setSuccess('project.application.form.relevance.save.success'))
      )
      .subscribe();
  }

  onSubmit(): void {
    this.updateData.emit({
      territorialChallenge: this.territorialChallenge.inputs,
      commonChallenge: this.commonChallenge.inputs,
      transnationalCooperation: this.transnationalCooperation.inputs,
      projectBenefits: this.buildBenefitsToSave(),
      projectStrategies: this.buildStrategiesToSave(),
      projectSynergies: this.buildSynergiesToSave(),
      availableKnowledge: this.projectRelevanceForm.controls.availableKnowledge.value,
    });
  }

  targetGroup = (id: number): string => id + 'targ';
  specification = (id: number): string => id + 'spec';
  strategy = (id: number): string => id + 'strat';
  contribution = (id: number): string => id + 'con';
  projectInitiative = (id: number): string => id + 'projIn';
  synergy = (id: number): string => id + 'syn';

  resetForm(): void {
    this.benefitsDataSource.data = this.constructBenefitsDataSource();
    this.editableBenefitsForm = new FormGroup({});
    this.strategiesDataSource.data = this.constructStrategyDataSource();
    this.editableStrategyForm = new FormGroup({});
    this.synergiesDataSource.data = this.constructSynergyDataSource();
    this.editableSynergyForm = new FormGroup({});

    this.territorialChallenge = this.languageService.initInput(this.project?.territorialChallenge);
    this.commonChallenge = this.languageService.initInput(this.project?.commonChallenge);
    this.transnationalCooperation = this.languageService.initInput(this.project?.transnationalCooperation);
    this.projectRelevanceForm.controls.availableKnowledge.setValue(this.project?.availableKnowledge);
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
    });
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
    });
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
    });
    return data;
  }

  private buildBenefitsToSave(): InputProjectRelevanceBenefit[] {
    return this.benefitsDataSource.data
      .map(element => ({
        group: this.editableBenefitsForm.get(this.targetGroup(element.id))?.value,
        specification: this.editableBenefitsForm.get(this.specification(element.id))?.value
      }));
  }

  private buildStrategiesToSave(): InputProjectRelevanceStrategy[] {
    return this.strategiesDataSource.data
      .map(element => ({
        strategy: this.editableStrategyForm.get(this.strategy(element.id))?.value !== 'Other'
          ? this.editableStrategyForm.get(this.strategy(element.id))?.value
          : null,
        specification: this.editableStrategyForm.get(this.contribution(element.id))?.value
      }));
  }

  private buildSynergiesToSave(): InputProjectRelevanceSynergy[] {
    return this.synergiesDataSource.data
      .map(element => ({
        specification: this.editableSynergyForm.get(this.projectInitiative(element.id))?.value,
        synergy: this.editableSynergyForm.get(this.synergy(element.id))?.value
      }))
      .filter(element => element.specification || element.synergy);
  }

  tableChanged(): void {
    this.formService.setDirty(true);
  }

  private formValid(): boolean {
    return this.editableBenefitsForm.valid
      && this.editableStrategyForm.valid
      && this.editableSynergyForm.valid
      && this.territorialChallenge.isValid()
      && this.commonChallenge.isValid()
      && this.transnationalCooperation.isValid();
  }
}
