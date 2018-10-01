package contracts.trader.query

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'GET'
    url "/query/company/f82c40ec-a785-11e8-98d0-529269fb1459"
    headers {
      contentType applicationJson()
    }
  }
  response {
    status HttpStatus.OK.value()
    body(
            "identifier": "f82c40ec-a785-11e8-98d0-529269fb1459",
            "name": "Pivotal",
            "value": 1337,
            "amountOfShares": 42,
            "tradeStarted": false
    )
    headers {
      contentType applicationJson()
    }
    async()
  }
}
