import { EbicsVersion } from 'components/models/ebics-version';
import { Partner } from 'components/models/ebics-partner';
import { ConnectionStatus } from 'components/models/connection-status';

export interface BankConnectionId {
  id: number;
  userId: string;
  name: string;
  partner: Partner;
}

export interface BankConnectionProperty {
  id: number,
  key: string;
  value: string;
}

export interface UserCertificatesForImport {
  e002: string
  x002: string
  a005: string
}

export interface BankConnection extends ConnectionStatus {
    id: number;
    ebicsVersion: EbicsVersion;
    userId: string;
    name: string;
    dn: string;
    userStatus: string;
    partner: Partner;
    keyStore: string;
    usePassword: boolean;
    guestAccess: boolean;
    creator: string;
    securityMedium: string;
    useCertificate: boolean;
    properties: BankConnectionProperty[];
  }

  export interface UserPartnerBank {
    ebicsVersion: EbicsVersion;
    userId: string;
    name: string;
    dn: string;
    partnerId: string;
    bankId: number;
    guestAccess: boolean;
    useCertificate: boolean;
  }

  export enum BankConnectionAccess {
    READ = 'READ',
    WRITE = 'WRITE',
    USE = 'USE',
  }
