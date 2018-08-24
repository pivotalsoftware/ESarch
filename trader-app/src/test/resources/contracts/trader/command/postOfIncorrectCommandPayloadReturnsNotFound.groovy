package contracts.trader.command

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'POST'
    url "/command/CreateCompanyCommand"
    body("{}") // An empty Body points to a faulty command payload
    headers {
      contentType applicationJson()
    }
  }
  response {
    status HttpStatus.NOT_FOUND.value()
    async()
  }
}
