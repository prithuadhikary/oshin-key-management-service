export interface LoadEntityResponse<T> {

  content: Array<T>;
  page: number;
  pageSize: number;
  totalPages: number;
  totalElements: number;

}
