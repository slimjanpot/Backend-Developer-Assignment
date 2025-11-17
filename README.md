# Webshop API Backend (Mechhive Assignment)
This is a simple backend API for a webshop, built with Java Spring Boot.

This application fetches product and currency data from external dummy APIs, caches this data in Redis, and provides two main endpoints:

An endpoint to get a list of all products, with a 10% fee added and prices converted to any specified currency.

An endpoint to simulate a transaction for multiple products in a specified currency.

##Features
Product Enrichment: Automatically adds a 10% sales fee to all products and serves them in any currency.

Transaction Simulation: A POST endpoint to "buy" multiple items at once and receive a final receipt.

Dynamic Caching: Fetches and caches product lists and currency rates on startup and every 5 minutes, as required.

Robust & Fast: Uses a plain String-based Redis cache for stability and to ensure all API responses are well under the 100ms requirement.

##Tech Stack
Java 17/21

Spring Boot 3

Maven

Redis (for caching)

Docker (to run Redis)

##Setup & Running Instructions
Follow these steps to get the application running on your local machine.

1. Prerequisites
Before you begin, you will need the following tools installed on your system:

Git

Java Development Kit (JDK) 17 or newer

Docker Desktop

2. Clone the Repository

# Clone this repository to your local machine
git clone https://github.com/slimjanpot/Backend-Developer-Assignment.git

# Navigate into the project directory
cd webshop-api
3. Start the Redis Cache (via Docker)
The application requires a Redis instance for caching.

Make sure Docker Desktop is open and running on your machine.

In your terminal, run the following command to pull and start the official Redis container:

docker run -d --name my-webshop-redis -p 6379:6379 redis
-d runs the container in the background.

--name my-webshop-redis gives it a recognizable name.

-p 6379:6379 maps your computer's port 6379 to the container's port 6379.

(Note: If the container already exists from a previous run, start it from your Docker Desktop dashboard or by running docker start my-webshop-redis)

4. Run the Spring Boot Application
This project uses the Maven Wrapper (mvnw), so you don't need to have Maven installed separately.

###On macOS / Linux:
./mvnw spring-boot:run
On Windows (PowerShell):

.\mvnw.cmd spring-boot:run
The application will start on http://localhost:8080. You will see startup logs confirming the cache is being cleared and then populated.

##How to Test & Verify All Requirements
Here is how to test that the application meets all the assignment's requirements. An API client like Postman is recommended.

1. Requirement: Get Products in any Currency
Test:

Make a GET request to http://localhost:8080/products?currency=USD

Make another GET request to http://localhost:8080/products?currency=JPY

Verification:

The request returns a Status 200 OK.

The JSON response shows a list of products.

Each product includes the purchasePrice, salePrice, and the correct currency code ("USD" or "JPY").

The prices are different for each currency, proving conversion and the 10% fee calculation are working.

2. Requirement: Create a Transaction
Test:

Make a POST request to http://localhost:8080/transactions.

Set the Body to raw and JSON.

Use the following JSON body:

JSON

{
  "currency": "USD",
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 5, "quantity": 1 }
  ]
}
Verification:

The request returns a Status 200 OK.

The JSON response is a TransactionReceipt.

It correctly shows "totalItems": 3.

It shows totalPurchasePrice and totalSalePrice calculated correctly and converted to "USD".

3. Requirement: Caching & 5-Minute Refresh
Test:

Observe the application terminal/console upon starting.

Wait for 5 minutes.

Verification:

On startup, logs appear: --- [STARTUP] Clearing entire Redis cache... --- followed by --- [STARTUP] Populating cache... ---.

After 5 minutes, new logs appear automatically: Fetching products from API... and Fetching conversion rates from API.... This confirms the @Scheduled task is running.

4. Requirement: Response Time < 100ms
Test:

In Postman, make the GET /products?currency=USD request a second time.

Look at the "Time" metric in the Postman response panel.

Verification:

The response time is very low (e.g., 25ms - 50ms), well below the 100ms requirement. This proves the data is being served from the fast Redis cache, not the slow external APIs.

5. Requirement: Multilayered Architecture & Clean Code
Verification:

The project is organized into controller, service, model, and config packages, demonstrating a clean, multilayered architecture.

Controllers are thin and only handle web requests.

Services contain all business logic (caching, calculations).

Models (DTOs) are used to define clear data contracts for requests and responses.
