import { Pageable, Sort } from "@cat/api";
import { FileListItem } from "@common/components/file-list/file-list-item";

export interface PageFileList {
  totalElements: number;
  totalPages: number;
  size: number;
  content: Array<FileListItem>;
  number: number;
  sort: Sort;
  first: boolean;
  last: boolean;
  pageable: Pageable;
  numberOfElements: number;
  empty: boolean;
}
