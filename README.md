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

This application has 3 end-points.
1. /customer/save-customer : This end-point will add customer details into database.
2. /customer/get-details/{id} : This end-point will fetch specific customer details based on customer id.
3. /customer/calculate-points/{id} : This end-point will calculate the points earned per each transaction for the specific user based on customer id.

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
Email: abhisheka.mishra01@infosys.com
GitHub: @abhishekamishra