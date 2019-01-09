# n26_coding
HackerRank test from N26 

We would like to have a RESTful API for our statistics. The main use case for the API is to calculate realtime statistics for the last 60 seconds of transactions
The API needs the following endpoints:

POST /transactions
called every time a transaction is made.

GET /statistics
returns the statistic based of the transactions of the last 60 seconds.

DELETE /transactions
deletes all transactions

c4 POST /transactions

This endpoint is called to create a new transaction. It MUST execute in constant time and memory 
Body:{
  "amount": "12.3343",
  "timestamp": "2018-07-17T09:59:51.312Z"
}
 amount
transaction amount; a string of arbitrary length that is parsable as a BigDecimal\cb1 \
 timestamp
transaction time in the ISO 8601 format YYYY-MM-DDThh:mm:ss.sssZ

Returns: Empty body with one of the following:\


 GET /statistics

This endpoint returns the statistics based on the transactions that happened in the last 60 seconds. It MUST execute in constant time and memory 
Returns:
{
  "sum": "1000.00",
  "avg": "100.53",
  "max": "200000.49",
  "min": "50.23",
  "count": 10
}
DELETE /transactions

This endpoint causes all existing transactions to be deleted\
The endpoint should accept an empty request body and return a 204 status code.\
These are the additional requirements for the solution:\
You are free to choose any JVM language to complete the challenge in, but\'a0
your application has to run in Maven.
 In addition to passing the tests, the solution must be at a quality level that you would be comfortable enough to put in production.
}
