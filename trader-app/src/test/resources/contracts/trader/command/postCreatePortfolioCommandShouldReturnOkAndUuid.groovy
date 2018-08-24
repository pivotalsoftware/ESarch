package contracts.trader.command

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'POST'
    url "/command/CreatePortfolioCommand"
    body(
            "portfolioId": anyUuid(),
            "userId": anyUuid()
    )
    headers {
      contentType applicationJson()
    }
  }
  response {
    status HttpStatus.OK.value()
    body("f82c481c-a785-11e8-98d0-529269fb1459")
    headers {
      contentType applicationJson()
    }
    async()
  }
}

