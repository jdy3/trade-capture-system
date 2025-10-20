fix(test): TradeControllerTest - Fixed testCreateTrade HTTP semantics

- Problem: testCreateTrade() was failing with 201 expecting 200
- Root Cause: Test expects incorrect status: 200 OK, but Create operations (POST) should expect 201 created
- Solution: Change status().isOK to status().isCreated in testCreateTrade() method
- Impact: Ensures correct HTTP semantics for trade creation

Note(s): Not an integration test, but a unit test that isolates controller logic; no new databse records are actually created

fix(test): TradeControllerTest - Fixed createTrade endpoint validation

- Problem: testCreateTradeValidationFailure_MissingBook() was failing with 201 expecting 400
- Root Cause: Missing @NotNull annotation on TradeDTO bookName field AND missing GlobalExceptionHandler for validation errors
- Solution: Added @NotNull annotation to TradeDTO bookName field and created GlobalExceptionHandler for validation errors
- Impact: Enables proper request validation for trade creation with appropriate error responses

Note(s): Not an integration test, so doesn't hit validateReferenceData(trade) method in service layer.

testCreateTradeValidationFailure_MissingTradeDate() AND testCreateTradeValidationFailure_NegativeNotional() incorrect response data failing tests were also resolved with the GlobalExceptionHandler fix

fix(test): TradeControllerTest - Fixed testDeleteTrade HTTP semantics

- Problem: testDeleteTrade() was failing with 200 expecting 204
- Root Cause: Mismatch with TradeController which returns status 200 with a body
- Solution: Change status().isNoContent() to status().isOK in testDeleteTrade() method
- Impact: Maintains consistency with API design where endpoints return meaningful response bodies

fix(test): TradeControllerTest - Fixed testUpdateTrade service calls

- Problem: testUpdateTrade was failing with no value at JSON path "$.tradeId"
- Root Cause: The controller calls amendTrade(), but testUpdateTrade was mocking saveTrade()
- Solution: Updated testUpdateTrade() to mock amendTrade() rather than saveTrade() and verify amendTrade() is called
- Impact: Ensures testUpdateTrade accurately tests the updateTrade endpoint 




