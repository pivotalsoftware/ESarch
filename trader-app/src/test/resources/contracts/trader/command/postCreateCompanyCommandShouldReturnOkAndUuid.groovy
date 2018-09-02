package contracts.trader.command

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'POST'
    url "/command/CreateCompanyCommand"
    body(
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
    body("f82c4dd0-a785-11e8-98d0-529269fb1459")
    headers {
      contentType textPlain()
    }
    async()
  }
}
