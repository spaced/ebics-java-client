import { computed, Ref, ref } from 'vue';
import {
  BankConnection, UserCertificatesForImport,
} from 'components/models/ebics-bank-connection';
import {
  AdminOrderType,
} from 'components/models/ebics-order-type';
import { UserIniWizzStep } from 'components/models/user-init-wizz';
import {
  UserPassword, UserLettersResponse,
  CertRequest, CertImportRequest
} from 'components/models/ebics-request-response';
import { api } from 'boot/axios';
import usePasswordAPI from 'components/password-api';



export default function useBankConnectionInitializationAPI(
  user: Ref<BankConnection>,
  userCertificatesForImport: Ref<UserCertificatesForImport>
) {
  const { pwdApiErrorHandler, pwdApiOkHandler, promptCertPassword } = usePasswordAPI();

  //TBD: needs to be somehow persisted to user state
  const userStatusLetterPrinted = ref<boolean>(false);

  //TBD: needs to be somehow persisted to user state
  const userStatusBankKeysVerified = ref<boolean>(false);

  /*
   * This computed property reflects step which is calculated from actual userStatus,
   * It is needed in order to know which steps are actually finished
   */
  const actualWizardStep = computed<UserIniWizzStep>({
    get() {
      switch (user.value.userStatus) {
        case 'CREATED':
          return UserIniWizzStep.CreateUserKeys;
        case 'NEW':
        case 'LOCKED':
        case 'PARTLY_INITIALIZED_INI':
        case 'PARTLY_INITIALIZED_HIA':
          return UserIniWizzStep.UploadUserKeys;
        case 'INITIALIZED':
          return userStatusLetterPrinted.value
            ? UserIniWizzStep.DownloadBankKeys
            : UserIniWizzStep.PrintUserLetters;
        case 'READY':
          return userStatusBankKeysVerified.value
            ? UserIniWizzStep.Finish
            : UserIniWizzStep.VerifyBankKeys;
      }
      return UserIniWizzStep.CreateUserKeys;
    },
    set(value) {
      switch (value) {
        case UserIniWizzStep.DownloadBankKeys:
          if (user.value.userStatus == 'INITIALIZED') {
            userStatusLetterPrinted.value = true;
          }
        case UserIniWizzStep.Finish:
          if (user.value.userStatus == 'READY') {
            userStatusBankKeysVerified.value = true;
          }
      }
    },
  });

  /**
   * Returns true if the @param step is already done in compare with actual status of user
   * @param step wizart step to be tested if is done
   * @returns
   */
  const isStepDone = (step: UserIniWizzStep): boolean => {
    return step < actualWizardStep.value;
  };

  const createUserKeysRequest = async (): Promise<void> => {
    try {
      const pass = await promptCertPassword(user.value, true);

      if (userCertificatesForImport.value.x002.length >0 && userCertificatesForImport.value.e002.length && userCertificatesForImport.value.a005.length ) {
        await api.post<CertRequest>(`bankconnections/${user.value.id}/certificates/import`, {
          dn: user.value.dn,
          usePassword: user.value.usePassword,
          password: pass,
          encryptionE002Xml: userCertificatesForImport.value.e002,
          authenticationX002Xml: userCertificatesForImport.value.x002,
          signatureA005Xml: userCertificatesForImport.value.a005,
        } as CertImportRequest);
      } else {
        await api.post<CertRequest>(`bankconnections/${user.value.id}/certificates`, {
          dn: user.value.dn,
          usePassword: user.value.usePassword,
          password: pass,
        } as CertRequest);
      }
      pwdApiOkHandler(user.value,
        `Certificates created successfully for user name: ${user.value.name} dn: ${user.value.dn}`
      );
    } catch (error) {
      pwdApiErrorHandler(user.value, 'Create certificates failed', error);
    }
  };

  const getUserLetters = async (): Promise<UserLettersResponse | undefined> => {
    try {
      const pass = await promptCertPassword(user.value, false);
      const response = await api.post<UserLettersResponse>(`bankconnections/${user.value.id}/certificates/letters`, {
        password: pass
      });
      pwdApiOkHandler(user.value,
        `Certificates created successfully for user name: ${user.value.name} dn: ${user.value.dn}`
      );
      return response.data;
    } catch (error) {
      pwdApiErrorHandler(user.value, 'Create certificates failed', error);
    }
  };

  const resetUserStatusRequest = async (): Promise<void> => {
    try {
      await api.post<UserPassword>(`bankconnections/${user.value.id}/resetStatus`);
      pwdApiOkHandler(user.value,
        `Initialization status resetted successfully for user name: ${user.value.name}`
      );
    } catch (error) {
      pwdApiErrorHandler(user.value, 'Initialization status reset failed: ', error);
    }
  };

  /**
   * Executest EBICS Admin Ordertype requests like INI, HIA, HPB, SPR,..
   * @param adminOrderType
   * @param pass
   * @returns
   */
  const ebicsAdminTypeRequest = async (
    adminOrderType: AdminOrderType
  ): Promise<void> => {
    try {
      const pass = await promptCertPassword(user.value, false);
      await api.post<UserPassword>(
        `bankconnections/${user.value.id}/${user.value.ebicsVersion}/send${adminOrderType}`,
        { password: pass }
      );
      pwdApiOkHandler(user.value,
        `${adminOrderType} executed successfully for user name: ${user.value.name}`
      );
    } catch (error) {
      pwdApiErrorHandler(user.value, `${adminOrderType} failed: `, error);
    }
  };

  return {
    actualWizardStep,
    isStepDone,
    createUserKeysRequest,
    getUserLetters,
    ebicsAdminTypeRequest,
    resetUserStatusRequest,
  };
}
