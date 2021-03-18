import {Routes} from '@angular/router';
import {AuditLogComponent} from './audit-log/audit-log.component';
import {AuditPageGuard} from '../common/guards/audit-page.guard';

export const routes: Routes = [
    {
        path: '',
        canActivate: [AuditPageGuard],
        children: [
            {
                path: '',
                component: AuditLogComponent,
            },
        ]
    }
];
