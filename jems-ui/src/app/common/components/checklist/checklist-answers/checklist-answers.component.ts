import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import {
  ChecklistComponentInstanceDTO,
  ChecklistInstanceDetailDTO,
  ProgrammeChecklistComponentDTO,
  ScoreInstanceMetadataDTO,
  ScoreMetadataDTO,
  TextInputMetadataDTO
} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {FormService} from '@common/components/section/form/form.service';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {of} from 'rxjs';
import {NumberService} from '@common/services/number.service';

@Component({
  selector: 'jems-checklist-answers',
  templateUrl: './checklist-answers.component.html',
  styleUrls: ['./checklist-answers.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChecklistAnswersComponent implements OnInit, OnChanges {
  ComponentType = ProgrammeChecklistComponentDTO.TypeEnum;
  Alert = Alert;

  @Input()
  components: ChecklistComponentInstanceDTO[] = [];
  @Input()
  status: ChecklistInstanceDetailDTO.StatusEnum;
  @Input()
  editable: boolean;
  @Input()
  isScored: boolean;
  @Input()
  minScore: number;
  @Input()
  maxScore: number;
  @Input()
  maxTotalScore: number;
  @Input()
  allowsDecimalScore: boolean;

  @Output()
  save = new EventEmitter<void>();

  form = this.formBuilder.group({
    formComponents: this.formBuilder.array([])
  });

  sliderValues: number[] = [];
  FORM_ERRORS = {
    score: {
      max: 'programme.checklists.instance.slider.max.error'
    }
  };
  FORM_ERRORS_ARGS = {
    score: {
      max: {maxValue: 0}
    }
  };

  constructor(private formService: FormService,
              private formBuilder: FormBuilder) {
  }


  ngOnInit(): void {
    if (this.status) {
      // if there is no status we're dealing with a programme checklist (no instance)
      this.formService.init(this.form, of(this.editable));
    }
    this.maxTotalScore = this.getWeightedMaxScoreQuestions();
    this.isScored = this.maxTotalScore > 0;

    this.FORM_ERRORS_ARGS = {
      score: {
        max: { maxValue: this.maxScore}
      }
    };
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.components) {
      this.initializeEmptyComponents();
      this.resetForm();
    }
  }

  resetForm(): void {
    this.formComponents.clear();
    this.components.forEach(component => {
        if (component.type === this.ComponentType.SCORE) {
          this.sliderValues[component.position] = (component.instanceMetadata as ScoreInstanceMetadataDTO).score || this.minScore;
        }
        this.formComponents.push(this.formBuilder.group({
          id: component.id,
          position: component.position,
          type: component.type,
          programmeMetadata: component.programmeMetadata,
          instanceMetadata: this.getInstanceForm(component)
        }));
      }
    );
    this.formService.resetEditable();
  }

  private getWeightedMaxScoreQuestions(): number {
    let questionsScored = 0;
    this.components.forEach(component => {
      if (component.type === this.ComponentType.SCORE) {
        const metadataWeight = (component.programmeMetadata as ScoreMetadataDTO).weight;
        questionsScored = questionsScored + this.maxScore * metadataWeight;
      }
    })
    return questionsScored;
  }

  private getInstanceForm(component: ChecklistComponentInstanceDTO): FormGroup | null {
    if (!component.instanceMetadata) {
      return null;
    }
    const group = this.formBuilder.group(component.instanceMetadata);

    switch (component.type) {
      case ChecklistComponentInstanceDTO.TypeEnum.OPTIONSTOGGLE:
        group.get('justification')?.addValidators([Validators.maxLength(5000)]);
        break;
      case ChecklistComponentInstanceDTO.TypeEnum.TEXTINPUT:
        group.get('explanation')?.addValidators([Validators.maxLength((component.programmeMetadata as TextInputMetadataDTO).explanationMaxLength)]);
        break;
      case ChecklistComponentInstanceDTO.TypeEnum.SCORE:
        group.get('score')?.setValue(group.get('score')?.value || this.minScore);
        group.get('score')?.addValidators([Validators.min(this.minScore), Validators.max(this.maxScore)]);
        group.get('justification')?.addValidators([Validators.maxLength(5000)]);
        break;
    }

    return group;
  }

  get formComponents(): FormArray {
    return this.form.get('formComponents') as FormArray;
  }

  private initializeEmptyComponents(): void {
    this.components
      .filter(component => !component.instanceMetadata)
      .forEach(component => {
        switch (component.type) {
          case ChecklistComponentInstanceDTO.TypeEnum.OPTIONSTOGGLE: component.instanceMetadata = {
            type:ChecklistComponentInstanceDTO.TypeEnum.OPTIONSTOGGLE,
            answer: null,
            justification: ''
          };
            break;
          case ChecklistComponentInstanceDTO.TypeEnum.TEXTINPUT: component.instanceMetadata = {
            type:ChecklistComponentInstanceDTO.TypeEnum.TEXTINPUT,
            explanation: ''
          };
            break;
          case ChecklistComponentInstanceDTO.TypeEnum.SCORE: component.instanceMetadata = {
            type:ChecklistComponentInstanceDTO.TypeEnum.SCORE,
            score: this.minScore,
            justification: ''
          };
            break;
        }
    });
  }

  getSliderStep(): number{
    return this.allowsDecimalScore ? 0.01 : 1;
  }

  getAverageScore(): number {
    let sum = 0;
    const scoreComponents = this.formComponents.controls.filter(component => component.value.type === this.ComponentType.SCORE);
    scoreComponents.forEach(component => sum += NumberService.product([(component.value.instanceMetadata as ScoreInstanceMetadataDTO).score, (component.value.programmeMetadata as ScoreMetadataDTO).weight]));

    return NumberService.roundNumber(sum, 2);
  }
}
