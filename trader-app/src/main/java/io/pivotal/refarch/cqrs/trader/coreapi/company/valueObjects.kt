package io.pivotal.refarch.cqrs.trader.coreapi.company

import com.fasterxml.jackson.annotation.JsonValue
import org.axonframework.common.IdentifierFactory
import java.io.Serializable

data class CompanyId(@JsonValue val identifier: String = IdentifierFactory.getInstance().generateIdentifier()) : Serializable
