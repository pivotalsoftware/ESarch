package contracts.trader

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
    request {
        method 'POST'
        url "/command/CreateCompanyCommand"
        body """
            {
            "companyId":"7962b5b3-f223-4c2e-aadf-0e908f2331b1",
            "userId":"b1469bc0-3e04-4c3a-8d18-24d5df513a0a",
            "companyName":"COMPANY-cf2d1993-0380-4f5e-94ef-09cdd1fd222c",
            "companyValue":0,
            "amountOfShares":0
            }
        """
        headers {
            contentType applicationJson()
        }
    }
    response {
        status HttpStatus.OK.value()
    }
}

