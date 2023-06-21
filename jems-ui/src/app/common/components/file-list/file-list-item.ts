import {JemsFileDTO, UserSimpleDTO} from '@cat/api';

export interface FileListItem {
  id: number;
  name: string;
  type: JemsFileDTO.TypeEnum;
  uploaded: Date;
  author: UserSimpleDTO;
  sizeString: string;
  description: string;
  editable: boolean;
  deletable: boolean;
  tooltipIfNotDeletable: string;
  iconIfNotDeletable: string;
}
