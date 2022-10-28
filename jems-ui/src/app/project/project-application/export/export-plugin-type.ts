import {ExportCategoryTypeEnum} from '@project/project-application/export/export-category-type';
import {PluginInfoDTO} from '@cat/api';

export class PluginType {
  type: ExportCategoryTypeEnum;
  plugin: PluginInfoDTO;
}
