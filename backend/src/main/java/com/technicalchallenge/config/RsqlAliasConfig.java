package com.technicalchallenge.config;

import java.util.HashMap;
import java.util.Map;

// Rewrites user-friendly RSQL aliases (case-insensitive for alias keys) to entity property paths
public class RsqlAliasConfig {

    public static Map<String, String> aliasMap = new HashMap<>();

    static {
        aliasMap.put("counterparty", "counterparty.name");
        aliasMap.put("book", "book.bookName");
        aliasMap.put("trader", "traderUser.loginId");
        aliasMap.put("status", "tradeStatus.tradeStatus");
        aliasMap.put("date", "tradeDate");
    }   

    public static String applyAliases(String query) {
        String rewritten = query;

        for (var entry : aliasMap.entrySet()) {
        rewritten = rewritten.replaceAll("(?i)(?<!\\.)\\b" + entry.getKey() + "\\b(?=\\s*[!<>=]=?)", entry.getValue());
        }
    
        return rewritten;
    }
    
}
