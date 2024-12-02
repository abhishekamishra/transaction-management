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
