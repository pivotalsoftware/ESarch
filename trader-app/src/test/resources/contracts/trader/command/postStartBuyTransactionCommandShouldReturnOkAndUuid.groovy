package contracts.trader.command

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'POST'
    url "/command/StartBuyTransactionCommand"
    body(
            "transactionId": anyUuid(),
            "orderBookId": anyUuid(),
            "portfolioId": anyUuid(),
            "tradeCount": anyPositiveInt(),
            "pricePerItem": anyPositiveInt()
    )

    headers {
      contentType applicationJson()
    }
  }
  response {
    status HttpStatus.OK.value()
    body("") // TODO body should actually be the BaseContractTest.EXPECTED_UUID field - not sure why that doesn't work
  }
}

