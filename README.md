# Project Name

transaction-management:  This application has the functionality where it will calculate the points earned per each transaction.
ex: $120 = 2*$20 + 1*$50 = 90pts

---

## Table of Contents

1. [Description](#description)
2. [Installation](#installation)
4. [Contributing](#contributing)
6. [Contact](#contact)

---

## Description

This application has the functionality where it will calculate the points earned per each transaction. Here I have used Java 17, Springboot, JPA, PostgreSql.

This application has 2 end-points.
1. /customer/save-customers : This end-point will add customer details into database.
2. /customer/calculate-points/{id} : This end-point will calculate the points earned per each transaction for the specific user based on customer id.

Request:
[POST] http://localhost:8080/customer/save-customers
[
{
"customerName": "Ron",
"transactions": [
{
"month": "JAN",
"amount": 2000.13
},
{
"month": "FEB",
"amount": 769.88
}
]
},
{
"customerName": "Steve",
"transactions": [
{
"month": "JAN",
"amount": 888.67
},
{
"month": "FEB",
"amount": 120.0
},
{
"month": "MAR",
"amount": 7776.7
},
{
"month": "SEP",
"amount": 974.3
}
]
}
]

Result:
[
{
"id": 21,
"customerName": "Ron",
"customerId": 94,
"transactions": [
{
"id": 61,
"month": "JAN",
"amount": 2000.13,
"customerId": 94
},
{
"id": 62,
"month": "FEB",
"amount": 769.88,
"customerId": 94
}
]
},
{
"id": 22,
"customerName": "Steve",
"customerId": 371,
"transactions": [
{
"id": 63,
"month": "JAN",
"amount": 888.67,
"customerId": 371
},
{
"id": 64,
"month": "FEB",
"amount": 120.0,
"customerId": 371
},
{
"id": 65,
"month": "MAR",
"amount": 7776.7,
"customerId": 371
},
{
"id": 66,
"month": "SEP",
"amount": 974.3,
"customerId": 371
}
]
}
]

Request:
[GET] http://localhost:8080/customer/calculate-points/94

Result:
{
"customerName": "Ron",
"monthlyAmount": [
{
"JAN": 3850,
"FEB": 1388
}
],
"qurterlyAmount": {
"Quarter:1 ": 5238
}
}

Request:
[GET] http://localhost:8080/customer/calculate-points/371

{
"customerName": "Steve",
"monthlyAmount": [
{
"JAN": 1626,
"FEB": 90,
"MAR": 15402
}
],
"qurterlyAmount": 17118
}



---

## Installation

Follow these steps to install and set up your project:

1. Clone the repository:
   ```bash
   git clone https://github.com/abhishekamishra/transaction-management
   
2. Run the application

## Contributing

We welcome contributions to this project! To contribute, please follow these steps:

1. Fork the repository.
2. Create a new branch (git checkout -b feature-branch).
3. Make your changes.
4. Commit your changes (git commit -am 'Add new feature').
5. Push to the branch (git push origin feature-branch).
6. Create a new Pull Request.

## Contact

If you have any questions or suggestions, feel free to reach out:

Name: Abhisheka Mishra
Email: abhishekamishra07@gmail.com
GitHub: @abhishekamishra