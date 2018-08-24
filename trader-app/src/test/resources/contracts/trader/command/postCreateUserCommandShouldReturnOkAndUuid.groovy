package contracts.trader.command

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'POST'
    url "/command/CreateUserCommand"
    body(
            "userId": anyUuid(),
            "name": anyNonBlankString(),
            "username": anyNonBlankString(),
            "password": anyNonBlankString()
    )
    headers {
      contentType applicationJson()
    }
  }
  response {
    status HttpStatus.OK.value()
    body("98684ad8-987e-4d16-8ad8-b620f4320f4c")
    headers {
      contentType textPlain()
    }
    async()
  }
}
