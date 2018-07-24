package io.pivotal.refarch.cqrs.trader.coreapi.portfolio

import org.axonframework.common.IdentifierFactory
import java.io.Serializable

data class PortfolioId(val identifier: String = IdentifierFactory.getInstance().generateIdentifier()) : Serializable {

    companion object {
        private const val serialVersionUID = 6784433385287437985L
    }

}
