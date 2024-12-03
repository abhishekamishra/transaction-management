Transaction Management Points calculation

Application Name: transaction-management

This application has the functionality where it will calculate the points earned per each transaction. 
ex: $120 = 2*$20 + 1*$50 = 90pts

This application has 3 end-points.
1. /customer/save-customer : This end-point will add customer details into database.
2. /customer/get-details/{id} : This end-point will fetch specific customer details based on customer id. 
3. /customer/calculate-points/{id} : This end-point will calculate the points earned per each transaction for the specific user based on customer id.

Database: postgresql 
Table: transaction_management

Request: [GET] http://localhost:8080/customer/calculate-points/2
Result:
{
    "customerName": "Steve",
    "monthlyAmount": [
        {
            "JAN": 1628,
            "FEB": 90,
            "MAR": 15404,
            "SEP": 1798
        }
    ],
    "qurterlyAmount": {
        "Quarter:1 ": 17122,
        "Quarter:2 ": 18920
    }
}

[POST] http://localhost:8080/customer/save-customer
Request:
[
    {
        "customerName": "Ron",
        "customerId": 1,
        "transactions": [
            {
            "month": "JAN",
            "amount": 2000.13,
            "customerId": 1
            },
            {
            "month": "FEB",
            "amount": 769.88,
            "customerId": 1
            }
        ]
    },
    {
        "customerName": "Steve",
        "customerId": 2,
        "transactions": [
            {
            "month": "JAN",
            "amount": 888.67,
            "customerId": 2
            },
            {
            "month": "FEB",
            "amount": 120.0,
            "customerId": 2
            },
            {
            "month": "MAR",
            "amount": 7776.7,
            "customerId": 2
            },
            {
            "month": "SEP",
            "amount": 974.3,
            "customerId": 2
            }
        ]
    }
]
Result:
[
    {
        "id": 7,
        "customerName": "Ron",
        "customerId": 620,
        "transactions": [
            {
            "id": 19,
            "month": "FEB",
            "amount": 769.88,
            "customerId": 620
            },
            {
            "id": 20,
            "month": "JAN",
            "amount": 2000.13,
            "customerId": 620
            }
         ]
    },
    {
        "id": 8,
        "customerName": "Steve",
        "customerId": 976,
        "transactions": [
            {
            "id": 22,
            "month": "MAR",
            "amount": 7776.7,
            "customerId": 976
            },
            {
            "id": 21,
            "month": "JAN",
            "amount": 888.67,
            "customerId": 976
            },
            {
            "id": 23,
            "month": "FEB",
            "amount": 120.0,
            "customerId": 976
            },
            {
            "id": 24,
            "month": "SEP",
            "amount": 974.3,
            "customerId": 976
            }
        ]
    }
] 