package contracts.trader.command

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'POST'
    url "/command/CreateCompanyCommand"
    body(
            "companyId": anyUuid(),
            "userId": anyNonBlankString(),
            "companyName": anyNonEmptyString(),
            "companyValue": anyPositiveInt(),
            "amountOfShares": anyPositiveInt()
    )

    headers {
      contentType applicationJson()
    }
  }
  response {
    status HttpStatus.OK.value()
    body("") // TODO body should actually be the CommandContractTest.EXPECTED_UUID field - not sure why that doesn't work
  }
}

