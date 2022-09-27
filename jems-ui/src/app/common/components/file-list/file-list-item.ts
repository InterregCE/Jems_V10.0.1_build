import { ProjectReportFileDTO, UserSimpleDTO } from '@cat/api';

export interface FileListItem {
  id: number;
  name: string;
  type: ProjectReportFileDTO.TypeEnum;
  uploaded: Date;
  author: UserSimpleDTO;
  sizeString: string;
  description: string;
  editable: boolean;
  deletable: boolean;
  tooltipIfNotDeletable: string;
  iconIfNotDeletable: string;
}
