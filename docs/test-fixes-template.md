fix(test): TradeControllerTest - Fixed createTrade HTTP semantics

- Problem: testCreateTrade() was failing with 201 expecting 200
- Root Cause: Create operations (POST) should expect 201 created, not 200 OK
- Solution: Change status().isOK to status().isCreated
- Impact: Ensures correct HTTP semantics for trade creation