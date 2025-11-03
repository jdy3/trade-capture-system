# Enhancement: Advanced Trade Search System


enhancement(trade): Enhancement 1 - Multi-criteria search

- Used logger.debug for enhancement retrieval operations in service layer to reduce noise in production. Recommendation for all retrieval operations to use logger.debug and only business operations to use logger.info.

- Used "/search/counterparty/{variable}" rather than /search/{variable}" as it’s more descriptive, explicit, and extensible. The /search endpoint family might grow (for book, and date searches etc).

enhancement(trade): Enhancement 1 - Paginated filtering

enhancement(trade): Enhancement 1 - RSQL query support

- Added RSQL parsing support and JPA Specification integration via external library.

- Introduced RsqlAliasConfig to rewrite user-friendly query aliases into entity field paths (case-insensitive on alias keys).

- Updated GlobalExceptionHandler to provide clearer responses for RSQL syntax and unknown-field errors.

- Full case-insensitive value matching is not natively supported by the RSQL library.
Implementing a universal lower-case comparison visitor would require custom parsing logic.
Recommendation: use a case-insensitive DB collation or enforce canonical casing on input values for consistent behavior.








