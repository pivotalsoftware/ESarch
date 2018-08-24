package contracts.trader.command

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'POST'
    url "/command/ExecutedTransactionCommand"
    body(
            "transactionId": anyUuid(),
            "amountOfItems": anyPositiveInt(),
            "itemPrice": anyPositiveInt()
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

