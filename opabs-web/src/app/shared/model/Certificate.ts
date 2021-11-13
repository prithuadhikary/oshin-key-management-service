import {KeyType} from './KeyType';

export interface Certificate {
  id: string;
  anchor: boolean;
  publicKeyFingerprint: string;
  certificateFingerprint: string;
  parentCertificateId: string;
  trustChainId: string;
  keyType: KeyType;
  dateCreated: string;
  dateUpdated: string;
  subjectDistinguishedName: string;
  issuerDistinguishedName: string;
  validFrom: string;
  validUpto: string;
  expired: boolean;
  notYetValid: boolean;
  keyUsages: Array<string>;
  keyLength: number;
  namedCurve: string;
  pathLengthConstraint: number;
  serial: number;
}
