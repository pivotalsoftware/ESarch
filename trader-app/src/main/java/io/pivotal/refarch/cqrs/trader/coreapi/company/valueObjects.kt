package io.pivotal.refarch.cqrs.trader.coreapi.company

import com.fasterxml.jackson.annotation.JsonValue
import org.axonframework.common.IdentifierFactory
import java.io.Serializable

data class CompanyId(@JsonValue val identifier: String = IdentifierFactory.getInstance().generateIdentifier()) : Serializable {

    companion object {
        private const val serialVersionUID = -2521069615900157076L
    }

}
