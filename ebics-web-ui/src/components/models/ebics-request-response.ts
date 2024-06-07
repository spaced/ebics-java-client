import { BtfService } from 'components/models/btf-service';

export interface UserPassword {
  password: string;
}

export interface CertRequest extends UserPassword {
  dn: string;
  usePassword: boolean;
  password: string;
}

export interface CertImportRequest extends CertRequest {
  authenticationX002Xml: string,
  signatureA005Xml: string,
  encryptionE002Xml: string
}

export interface UploadRequest {
  password: string;
  params: Map<string, string>;
}

export interface UploadResponse {
  orderNumber: string;
  transactionId: string;
}

export interface UploadRequestH004 extends UploadRequest {
  password: string;
  orderType: string;
  attributeType: string;
  params: Map<string, string>;
}

export interface UploadRequestH005 extends UploadRequest {
  password: string;
  orderService: BtfService;
  signatureFlag: boolean;
  edsFlag: boolean;
  fileName: string;
  params: Map<string, string>;
}

export interface DownloadRequest {
  password: string;
  adminOrderType: string;
  params: Map<string, string>;
  startDate?: Date;
  endDate?: Date;
}

export interface DownloadRequestH004 extends DownloadRequest {
  password: string;
  adminOrderType: string;
  orderType?: string;
  params: Map<string, string>;
  startDate?: Date;
  endDate?: Date;
}

export interface DownloadRequestH005 extends DownloadRequest {
  password: string;
  adminOrderType: string;
  orderService?: BtfService;
  params: Map<string, string>;
  startDate?: Date;
  endDate?: Date;
}
interface Letter {
  letterText: string;
  hash: string;
}

export interface UserLettersResponse {
  signature: Letter;
  encryption: Letter;
  authentication: Letter;
}
