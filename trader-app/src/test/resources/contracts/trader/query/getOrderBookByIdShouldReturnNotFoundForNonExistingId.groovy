package contracts.trader.query

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'GET'
    url "/query/order-book/non-existing-id"
    headers {
      contentType applicationJson()
    }
  }
  response {
    status HttpStatus.NOT_FOUND.value()
    async()
  }
}
