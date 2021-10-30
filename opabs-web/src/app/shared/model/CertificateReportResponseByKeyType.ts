export interface CertificateReportResponseByKeyType {

  tenantId: string;
  totalCertificateCount: number;
  certificateCountInfos: [{ readonly keyType: string, readonly certificateCount: number }];
}
