package contracts.trader.command

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'POST'
    url "/command/CancelCashReservationCommand"
    body(
            "portfolioId": anyUuid(),
            "transactionId": anyUuid(),
            "amountOfMoneyToCancel": anyPositiveInt()
    )
    headers {
      contentType applicationJson()
    }
  }
  response {
    status HttpStatus.OK.value()
    body("")
    async()
  }
}

