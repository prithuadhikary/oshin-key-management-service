export interface CertificateReportResponseByHierarchy {
  countsByLevel: Array<{ level: string, count: number }>;
  totalCount: number;
}
