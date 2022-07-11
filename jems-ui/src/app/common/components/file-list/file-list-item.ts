import { ProjectReportFileDTO, UserSimpleDTO } from '@cat/api';

export interface FileListItem {
  id: number;
  name: string;
  type: ProjectReportFileDTO.TypeEnum;
  uploaded: Date;
  author: UserSimpleDTO;
  size: number;
  sizeString: string;
  deletable: boolean;
}
