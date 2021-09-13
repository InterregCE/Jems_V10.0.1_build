import {Pipe, PipeTransform} from '@angular/core';
import {UserRoleSummaryDTO} from '@cat/api';

@Pipe({name: 'userRoleFilter', pure: true})
export class UserRoleFilterPipe implements PipeTransform {

  transform(roles: UserRoleSummaryDTO[], currentlySelectedRolesIds: number[], filterText: string): UserRoleSummaryDTO[] {
    const result = roles?.filter(role => currentlySelectedRolesIds?.indexOf(role.id) < 0) || [];
    if (!filterText) {
      return result;
    }

    return result.filter(userRole => userRole.name.toUpperCase().startsWith(filterText.toUpperCase()));
  }

}
