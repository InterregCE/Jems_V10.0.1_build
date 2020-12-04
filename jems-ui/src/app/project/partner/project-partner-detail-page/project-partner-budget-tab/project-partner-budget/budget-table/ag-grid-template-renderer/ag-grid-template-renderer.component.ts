import {ChangeDetectionStrategy, Component, TemplateRef} from '@angular/core';
import {ICellRendererAngularComp} from 'ag-grid-angular';
import {ICellRendererParams} from 'ag-grid-community';

@Component({
  selector: 'app-template-renderer',
  templateUrl: './ag-grid-template-renderer.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AgGridTemplateRendererComponent implements ICellRendererAngularComp {
  template: TemplateRef<any>;
  templateContext: { $implicit: any };

  refresh(params: any): boolean {
    this.templateContext = {$implicit: params};
    return false;
  }

  agInit(params: ICellRendererParams): void {
    this.template = (params as any).ngTemplate;
    this.refresh(params);
  }
}
