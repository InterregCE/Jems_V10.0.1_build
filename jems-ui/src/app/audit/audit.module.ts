import {NgModule} from '@angular/core';
import {SharedModule} from '../common/shared-module';
import {RouterModule} from '@angular/router';
import {routes} from './audit-routing.module';
import { AuditLogComponent } from './audit-log/audit-log.component';

@NgModule({
    declarations: [
    AuditLogComponent
    ],
    imports: [
        SharedModule,
        RouterModule.forChild(routes),
    ],
})
export class AuditModule {
}
