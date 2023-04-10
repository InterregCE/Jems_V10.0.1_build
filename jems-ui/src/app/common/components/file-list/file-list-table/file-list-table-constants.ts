import { MatSort } from '@angular/material/sort';

export class FileListTableConstants {
  static DEFAULT_SORT: Partial<MatSort> = {active: 'uploaded', direction: 'desc'};
  static SENSITIVE_FILE_NAME_MASK = '*********.***';
}
