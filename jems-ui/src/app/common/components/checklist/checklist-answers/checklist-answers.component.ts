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
  ChecklistComponentInstanceDTO, ChecklistInstanceDetailDTO,
  ProgrammeChecklistComponentDTO,
  ProgrammeChecklistDetailDTO, TextInputMetadataDTO
} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {FormService} from '@common/components/section/form/form.service';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {of} from 'rxjs';

@Component({
  selector: 'jems-checklist-answers',
  templateUrl: './checklist-answers.component.html',
  styleUrls: ['./checklist-answers.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChecklistAnswersComponent implements OnInit, OnChanges {
  ComponentType = ProgrammeChecklistComponentDTO.TypeEnum;
  Status = ChecklistInstanceDetailDTO.StatusEnum;
  Alert = Alert;

  @Input()
  components: ChecklistComponentInstanceDTO[] = [];
  @Input()
  status: ChecklistInstanceDetailDTO.StatusEnum;
  @Input()
  finishedDate: string;
  @Input()
  warning: string;

  @Output()
  save = new EventEmitter<ChecklistComponentInstanceDTO[]>();
  @Output()
  finish = new EventEmitter<ChecklistComponentInstanceDTO[]>();

  form = this.formBuilder.group({
    formComponents: this.formBuilder.array([])
  });
  confirmFinish = {
    title: 'checklists.instance.confirm.finish.title',
    message: 'checklists.instance.confirm.finish.message'
  };

  constructor(private formService: FormService,
              private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    if (this.status) {
      // if there is no status we're dealing with a programme checklist (no instance)
      this.formService.init(this.form, of(this.status === this.Status.DRAFT));
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.components) {
      this.initializeEmptyComponents();
      this.resetForm();
    }
  }

  resetForm(): void {
    this.formComponents.clear();
    this.components.forEach(component =>
      this.formComponents.push(this.formBuilder.group({
        id: component.id,
        position: component.position,
        type: component.type,
        programmeMetadata: component.programmeMetadata,
        instanceMetadata: this.getInstanceForm(component)
      }))
    );
    this.formService.resetEditable();
  }

  private getInstanceForm(component: ChecklistComponentInstanceDTO): FormGroup | null{
    if (!component.instanceMetadata) {
      return null;
    }

    const group = this.formBuilder.group(component.instanceMetadata);
    if(component.type === ChecklistComponentInstanceDTO.TypeEnum.TEXTINPUT) {
      group.get('explanation')?.addValidators([Validators.maxLength((component.programmeMetadata as TextInputMetadataDTO).explanationMaxLength)]);
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
            answer: null
          }; break;
          case ChecklistComponentInstanceDTO.TypeEnum.TEXTINPUT: component.instanceMetadata = {
            type:ChecklistComponentInstanceDTO.TypeEnum.TEXTINPUT,
            explanation: null
          }; break;
        }
    });
  }
}
