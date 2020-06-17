import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';

@Component({
  selector: 'app-description-cell',
  templateUrl: './description-cell.component.html',
  styleUrls: ['./description-cell.component.scss']
})
export class DescriptionCellComponent implements OnInit {
  @Input()
  data: any;

  descriptionForm = this.formBuilder.group({
    descriptionArea: ['', Validators.compose([
      Validators.maxLength(100)
    ])]
  });

  constructor(private formBuilder: FormBuilder) {
  }

  get description() {
    return this.descriptionForm.controls.descriptionArea;
  }

  ngOnInit(): void {
    this.descriptionForm.patchValue({descriptionArea: this.data.row.description});
  }

  onSubmit(): any {
    if (this.description.value.length <= 100) {
      this.data.extraProps.onSave(this.description.value, this.data.index, this.data.row.id);
    }
  }

  cancelSave(): void {
    this.descriptionForm.patchValue({descriptionArea: this.data.row.description});
    this.data.extraProps.onCancel(this.data.index);
  }

  getDescriptionError(): string {
    if (this.description.value.length > 100) {
      return 'project.file.description.size.too.long';
    }
    return '';
  }

  getSizedDescription(): string {
    if (this.data.row.description.length > 30) {
      return this.data.row.description.substring(0, 30) + '...';
    }
    return this.data.row.description;
  }
}
