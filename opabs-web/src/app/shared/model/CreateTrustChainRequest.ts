export class CreateTrustChainRequest {
  name: string;
  description: string;
  validFrom: string;
  validityInYears: string;
  keyUsages: string;
  signatureAlgorithm: string;
}
