package contracts.trader.query

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'GET'
    url "/query/portfolio/f82c481c-a785-11e8-98d0-529269fb1459"
    headers {
      contentType applicationJson()
    }
  }
  response {
    status HttpStatus.OK.value()
    body("""
      {
        "identifier" : "f82c481c-a785-11e8-98d0-529269fb1459",
        "userId" : "98684ad8-987e-4d16-8ad8-b620f4320f4c",
        "amountOfMoney" : 1000000,
        "reservedAmountOfMoney" : 5000,
        "itemsInPossession" : {
          "f82c40ec-a785-11e8-98d0-529269fb1459" : {
            "generatedId" : 1,
            "identifier" : "f82c40ec-a785-11e8-98d0-529269fb1459",
            "companyId" : "f82c40ec-a785-11e8-98d0-529269fb1459",
            "companyName" : "Pivotal",
            "amount" : 321
          }
        },
        "itemsReserved" : {
          "f82c40ec-a785-11e8-98d0-529269fb1459" : {
            "generatedId" : 2,
            "identifier" : "f82c40ec-a785-11e8-98d0-529269fb1459",
            "companyId" : "f82c40ec-a785-11e8-98d0-529269fb1459",
            "companyName" : "Pivotal",
            "amount" : 654
          }
        }
      }
    """)
    headers {
      contentType applicationJson()
    }
    async()
  }
}
