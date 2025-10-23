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

fix(test): TradeControllerTest - Fixed ID validation for updateTrade endpoint

- Problem: testUpdateTradeIdMismatch was failing with 200 expecting 400
- Root Cause: Controller was automatically overriding tradeDTO.tradeId with URL path parameter, making ID mismatch validation impossible
- Solution: Added validation logic to controller method to check for null tradeId and mismatch between URL and body tradeIds before processing
- Impact: Enables proper ID mismatch handling and maintains data integrity for the updateTrade endpoint

Note(s): Prevents silent data correction issues and follows REST API best practices by rejecting incorrect data rather than auto-filling missing fields.

There are other potential data integrity issues with the current approach as it silently corrects incomplete data, with no feedback to the client about the problem. REST API best practice is for missing required fields to be rejected, not auto-filled. 

Recomend validation for missing IDs to be added to the controller method and service layer for trade amendment.

fix(test): TradeServiceTest - Fixed incomplete/incorrect testCashflowGeneration_MonthlySchedule method

- Problem: testCashflowGeneration_MonthlySchedule() was failing with <12> expecting <1>
- Root Cause: Incomplete/missing mocks AND logical errors in testCashflowGeneration_MonthlySchedule()
- Solution: Added missing mocks AND corrected logical errors for testCashflowGeneration_MonthlySchedule()
- Impact: Enables proper cashflow testing

Note(s): Errors for testAmendTrade_Success() AND testCreateTrade_Success() were also resolved by completeing required mocks.

fix(test): TradeServiceTest - Fixed date validation error response

- Problem: testCreateTrade_InvalidDates_ShouldFail was failing with correct message <Start date cannot be before trade date> but expecting <Wrong error message>
- Root Cause: Test assertion had placeholder error message instead of actual business rule validation message 
- Solution: Updated the assertEquals() assertion to expect the correct error message
- Impact: Enables meaningful user date validation prompts for data validation in accordance with business rules