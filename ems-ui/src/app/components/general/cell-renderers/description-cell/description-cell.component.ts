import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-description-cell',
  templateUrl: './description-cell.component.html',
  styleUrls: ['./description-cell.component.scss']
})
export class DescriptionCellComponent implements OnInit {
  @Input()
  data: any;

  description: string;

  ngOnInit(): void {
    this.description = this.data.row.description;
  }

  onSubmit(): void {
    this.data.extraProps.onSave(this.description, this.data.index, this.data.row.id);
  }

  cancelSave(): void {
    this.description = this.data.row.description;
    this.data.extraProps.onCancel(this.data.index);
  }

}
