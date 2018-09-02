package contracts.trader.command

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'POST'
    url "/command/StartSellTransactionCommand"
    body(
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
    body("f82c4ace-a785-11e8-98d0-529269fb1459")
    headers {
      contentType textPlain()
    }
    async()
  }
}
