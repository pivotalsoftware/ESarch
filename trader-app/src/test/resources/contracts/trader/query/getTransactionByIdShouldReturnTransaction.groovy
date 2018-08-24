package contracts.trader.query

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'GET'
    url "/query/transaction/f82c448e-a785-11e8-98d0-529269fb1459"
    headers {
      contentType applicationJson()
    }
  }
  response {
    status HttpStatus.OK.value()
    body(
            "identifier": "f82c448e-a785-11e8-98d0-529269fb1459",
            "orderBookId": "f82c40ec-a785-11e8-98d0-529269fb1459",
            "portfolioId": "f82c481c-a785-11e8-98d0-529269fb1459",
            "companyName": "Pivotal",
            "amountOfItems": 50,
            "amountOfExecutedItems": 25,
            "pricePerItem": 123,
            "state": "CONFIRMED",
            "type": "BUY"
    )
    headers {
      contentType applicationJson()
    }
    async()
  }
}
