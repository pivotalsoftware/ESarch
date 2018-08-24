package contracts.trader.query

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'GET'
    url "/query/executed-trades/f82c40ec-a785-11e8-98d0-529269fb1459"
    headers {
      contentType applicationJson()
    }
  }
  response {
    status HttpStatus.OK.value()
    body([
            "generatedId": 1,
            "tradeCount" : 30,
            "tradePrice" : 180,
            "companyName": "Pivotal",
            "orderBookId": "f82c40ec-a785-11e8-98d0-529269fb1459"
    ])
    headers {
      contentType applicationJson()
    }
    async()
  }
}
