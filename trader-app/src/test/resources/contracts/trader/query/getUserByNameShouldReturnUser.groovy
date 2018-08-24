package contracts.trader.query

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'GET'
    url "/query/user/by-name/Pieter Humphrey"
    headers {
      contentType applicationJson()
    }
  }
  response {
    status HttpStatus.OK.value()
    body(
            "identifier": "98684ad8-987e-4d16-8ad8-b620f4320f4c",
            "name": "Pieter Humphrey",
            "username": "john.doe",
            "userName": "john.doe",
            "fullName": "Pieter Humphrey",
            "userId": "98684ad8-987e-4d16-8ad8-b620f4320f4c"
    )
    headers {
      contentType applicationJson()
    }
    async()
  }
}
