package org.ebics.client.ebicsrestapi.bankconnection

import org.ebics.client.api.bankconnection.BankConnection
import org.ebics.client.api.bankconnection.BankConnectionEntity
import org.ebics.client.api.bankconnection.BankConnectionService
import org.ebics.client.api.bankconnection.permission.BankConnectionAccessType
import org.ebics.client.ebicsrestapi.MockUser
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Primary
@Service
class UserServiceTestImpl : BankConnectionService {

    private val mockUsers = mapOf(
        1L to MockUser.createMockUser(1, true),
        2L to MockUser.createMockUser(2, false)
    )

    override fun findBankConnections(permission: BankConnectionAccessType): List<BankConnectionEntity> {
        TODO("Not yet implemented")
    }

    override fun getBankConnectionById(bankConnectionId: Long, permission: BankConnectionAccessType): BankConnectionEntity {
        return mockUsers[bankConnectionId]!!
    }

    override fun saveBankConnection(bankConnection: BankConnectionEntity): Long {
        TODO("Not yet implemented")
    }

    override fun createBankConnection(bankConnection: BankConnection): Long {
        TODO("Not yet implemented")
    }

    override fun updateBankConnection(bankConnectionId: Long, bankConnection: BankConnection): Long {
        TODO("Not yet implemented")
    }

    override fun deleteBankConnection(bankConnectionId: Long) {
        TODO("Not yet implemented")
    }

    override fun resetStatus(bankConnectionId: Long) {
        TODO("Not yet implemented")
    }
}