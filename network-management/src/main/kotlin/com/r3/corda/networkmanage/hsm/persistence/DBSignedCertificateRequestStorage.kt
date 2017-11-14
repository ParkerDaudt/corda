package com.r3.corda.networkmanage.hsm.persistence

import com.r3.corda.networkmanage.common.persistence.CertificateSigningRequest
import com.r3.corda.networkmanage.common.persistence.PersistentCertificateRequestStorage
import com.r3.corda.networkmanage.common.persistence.RequestStatus
import net.corda.node.utilities.CordaPersistence
import org.bouncycastle.pkcs.PKCS10CertificationRequest
import java.security.cert.CertPath

data class ApprovedCertificateRequestData(val requestId: String, val request: PKCS10CertificationRequest, var certPath: CertPath? = null)

class DBSignedCertificateRequestStorage(database: CordaPersistence) : SignedCertificateRequestStorage {

    private val storage = PersistentCertificateRequestStorage(database)

    override fun store(requests: List<ApprovedCertificateRequestData>, signers: List<String>) {
        for ((requestId, _, certPath) in requests) {
            storage.putCertificatePath(requestId, certPath!!, signers)
        }
    }

    override fun getApprovedRequests(): List<ApprovedCertificateRequestData> {
        return storage.getRequests(RequestStatus.APPROVED).map { it.toRequestData() }
    }

    private fun CertificateSigningRequest.toRequestData() = ApprovedCertificateRequestData(requestId, request)
}