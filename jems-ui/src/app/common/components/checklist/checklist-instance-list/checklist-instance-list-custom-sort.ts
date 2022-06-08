import {MatSort} from '@angular/material/sort';
import {ChecklistInstanceDTO, ChecklistInstanceSelectionDTO} from '@cat/api';
import moment from 'moment/moment';

export class ChecklistSort {

  static customSort(sort: Partial<MatSort>): ((a: ChecklistInstanceDTO | ChecklistInstanceSelectionDTO, b: ChecklistInstanceDTO | ChecklistInstanceSelectionDTO) => number) {
    // eslint-disable-next-line complexity
    return (a: ChecklistInstanceDTO | ChecklistInstanceSelectionDTO, b: ChecklistInstanceDTO | ChecklistInstanceSelectionDTO) => {
      if (sort.active === 'id') {
        const idA = a.id;
        const idB = b.id;
        if (sort.direction === 'asc') {
          return idA - idB;
        } else {
          return idB - idA;
        }
      }

      if (sort.active === 'name') {
        const nameA = a.name;
        const nameB = b.name;

        if (sort.direction === 'asc') {
          return nameA.localeCompare(nameB);
        } else {
          return nameB.localeCompare(nameA);
        }
      }

      if (sort.active === 'type') {
        const typeA = a.type;
        const typeB = b.type;
        if (typeA === typeB) {
          return 0;
        }
        if (sort.direction === 'asc') {
          return (typeA < typeB) ? -1 : 1;
        } else {
          return (typeA > typeB) ? -1 : 1;
        }
      }

      if (sort.active === 'consolidated') {
        const consolidatedA = a.consolidated;
        const consolidatedB = b.consolidated;
        if (consolidatedA === consolidatedB) {
          return 0;
        }
        if (sort.direction === 'asc') {
          return consolidatedA ? -1 : 1;
        } else {
          return consolidatedB ? -1 : 1;
        }
      }

      if (sort.active === 'status') {
        const statusA = a.status;
        const statusB = b.status;
        if (statusA === statusB) {
          return 0;
        }
        if (sort.direction === 'asc') {
          return (statusA < statusB) ? -1 : 1;
        } else {
          return (statusA > statusB) ? -1 : 1;
        }
      }

      if (sort.active === 'creatorEmail') {
        const emailA = ('creatorEmail' in a) ? a.creatorEmail : '';
        const emailB = ('creatorEmail' in b) ? b.creatorEmail : '';

        if (sort.direction === 'asc') {
          return emailA.localeCompare(emailB);
        } else {
          return emailB.localeCompare(emailA);
        }
      }

      if (sort.active === 'finishedDate') {
        const dateA = a.finishedDate ? moment(a.finishedDate) : null;
        const dateB = b.finishedDate ? moment(b.finishedDate) : null;
        if (dateA === null && dateB == null) {
          return 0;
        }
        if (dateA === null || dateB == null) {
          if (sort.direction === 'asc') {
            return dateA === null ? -1 : 1;
          } else {
            return dateB === null ? -1 : 1;
          }
        }

        if (sort.direction === 'asc') {
          return (dateB.isBefore(dateA)) ? -1 : 1;
        } else {
          return (dateA.isBefore(dateB)) ? -1 : 1;
        }
      }

      return 0;
    };
  }
}
