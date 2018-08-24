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
    body("") // TODO body should actually be the BaseContractTest.EXPECTED_UUID field - not sure why that doesn't work
  }
}

