export const portfolios = {
  items: [
    {
      id: 1,
      portfolioName: 'Buyer 99',
      moneyAvailable: 10000,
      reserved: 720,
      itemsAvailable: [
        {
          name: 'Shell',
          count: 20
        },
        {
          name: 'BP',
          count: 20
        },
        {
          name: 'Apple',
          count: 10
        }
      ],
      transactions: [
        {
          company: 'Shell',
          type: 'BUY',
          itemsCount: 10,
          price: 40,
          executedCount: 0,
          state: 'CONFIRMED'
        },
        {
          company: 'BP',
          type: 'SELL',
          itemsCount: 20,
          price: 10,
          executedCount: 30,
          state: 'SELL'
        }
      ]
    },
    {
      id: 2,
      portfolioName: 'Buyer 2',
      moneyAvailable: 30000,
      reserved: 520,
      itemsAvailable: [
        {
          name: 'Google',
          count: 10
        },
        {
          name: 'FB',
          count: 2
        },
        {
          name: 'Apple',
          count: 50
        }
      ],
      transactions: [
        {
          company: 'Shell',
          type: 'BUY',
          itemsCount: 10,
          price: 40,
          executedCount: 0,
          state: 'CONFIRMED'
        },
        {
          company: 'BP',
          type: 'SELL',
          itemsCount: 20,
          price: 10,
          executedCount: 30,
          state: 'SELL'
        }
      ]
    },
    {
      id: 3,
      portfolioName: 'Buyer 3',
      moneyAvailable: 5000,
      reserved: 80,
      itemsAvailable: [
        {
          name: 'Pivotal',
          count: 10
        },
        {
          name: 'Home Depot',
          count: 5
        }
      ],
      transactions: [
        {
          company: 'Shell',
          type: 'BUY',
          itemsCount: 10,
          price: 40,
          executedCount: 0,
          state: 'CONFIRMED'
        },
        {
          company: 'BP',
          type: 'SELL',
          itemsCount: 20,
          price: 10,
          executedCount: 30,
          state: 'SELL'
        }
      ]
    }
  ]
}





export const portfoliosMock = {
  items: [
    {
      identifier: 1,
      userName: 'Buyer 99',
      amountOfMoney: 10000,
      reservedAmountOfMoney: 720,
      itemsInPossession: [
        {
          generatedId: 20,
          identifier: "",
          companyId: "",
          companyName: "Shell",
          amount: 20
        },
        {
          generatedId: 52,
          identifier: "",
          companyId: "",
          companyName: "Apple",
          amount: 18
        }
      ],
      itemsReserved: [
        {
          generatedId: 10,
          identifier: "",
          companyId: "",
          companyName: "Bp",
          amount: 31
        }
      ]

    },
    {
      identifier: 2,
      userName: 'Buyer 12',
      amountOfMoney: 20000,
      reservedAmountOfMoney: 655,
      itemsInPossession: [
        {
          generatedId: 52,
          identifier: "",
          companyId: "",
          companyName: "Apple",
          amount: 18
        },
        {
          generatedId: 20,
          identifier: "",
          companyId: "",
          companyName: "Shell",
          amount: 20
        }
      ],
      itemsReserved: [
        {
          generatedId: 10,
          identifier: "",
          companyId: "",
          companyName: "Bp",
          amount: 31
        }
      ]

    },
    {
      identifier: 3,
      userName: 'Buyer 10',
      amountOfMoney: 4000,
      reservedAmountOfMoney: 100,
      itemsInPossession: [
        {
          generatedId: 23,
          identifier: "",
          companyId: "",
          companyName: "Philips",
          amount: 45
        },
        {
          generatedId: 20,
          identifier: "",
          companyId: "",
          companyName: "Shell",
          amount: 20
        },
        {
          generatedId: 52,
          identifier: "",
          companyId: "",
          companyName: "Apple",
          amount: 18
        }
      ],
      itemsReserved: [
        {
          generatedId: 10,
          identifier: "",
          companyId: "",
          companyName: "Bp",
          amount: 31
        }
      ]

    },
    
  ]
}