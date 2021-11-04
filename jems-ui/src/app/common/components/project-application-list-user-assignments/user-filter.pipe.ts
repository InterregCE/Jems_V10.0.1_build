import {Pipe, PipeTransform} from '@angular/core';
import {OutputUser} from '@cat/api';

@Pipe({name: 'filterUsers', pure: true})
export class UsersFilterPipe implements PipeTransform {

  private static getSearchValueForUser(user: OutputUser): string {
    return `${user.email} ${user.name} ${user.surname}`;
  }

  transform(users: OutputUser[], filterText: string): OutputUser[] {
    if (!filterText) {
      return users;
    }

    return users.filter(user =>
      UsersFilterPipe.getSearchValueForUser(user).toUpperCase().includes(filterText.trim().toUpperCase())
    );
  }
}
