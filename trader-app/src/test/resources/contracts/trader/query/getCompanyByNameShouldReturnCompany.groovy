package contracts.trader.query

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'GET'
    url "/query/company/by-name/Pivotal"
    headers {
      contentType applicationJson()
    }
  }
  response {
    status HttpStatus.OK.value()
    body(
            "identifier": "f82c4dd0-a785-11e8-98d0-529269fb1459",
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
