package contracts.trader.command

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'POST'
    url "/command/ReserveCashCommand"
    body(
            "portfolioId": anyUuid(),
            "transactionId": anyUuid(),
            "amountOfMoneyToReserve": anyPositiveInt()
    )
    headers {
      contentType applicationJson()
    }
  }
  response {
    status HttpStatus.OK.value()
    async()
  }
}
