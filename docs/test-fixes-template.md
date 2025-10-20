fix(test): TradeControllerTest - Fixed createTrade HTTP semantics

- Problem: testCreateTrade() was failing with 201 expecting 200
- Root Cause: Create operations (POST) should expect 201 created, not 200 OK
- Solution: Change status().isOK to status().isCreated in testCreateTrade() method
- Impact: Ensures correct HTTP semantics for trade creation

Note(s): Not an integration test, but a unit test that isolates controller logic; no new records are actually created

fix(test): TradeControllerTest - Fixed createTrade endpoint validation

- Problem: testCreateTradeValidationFailure_MissingBook() was failing with 201 expecting 400
- Root Cause: Missing @NotNull annotation on TradeDTO bookName field AND missing GlobalExceptionHandler for validation errors
- Solution: Added @NotNull annotation to TradeDTO bookName field and created GlobalExceptionHandler for validation errors
- Impact: Enables proper request validation for trade creation with appropriate error responses

Note(s): Not an integration test, so doesn't hit validateReferenceData(trade) method in service layer.

testCreateTradeValidationFailure_MissingTradeDate() AND testCreateTradeValidationFailure_NegativeNotional() incorrect response data failing tests were also resolved with the GlobalExceptionHandler fix




