package com.illumio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LookupTable {
    private final Map<String, List<String>> lookupMap;

    public LookupTable(String filePath) throws IOException {
        lookupMap = new HashMap<>();
        loadLookupTable(filePath);
    }

    private void loadLookupTable(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String key = parts[0].toLowerCase() + "," + parts[1].toLowerCase();
                    String tag = parts[2];
                    lookupMap.computeIfAbsent(key, k -> new ArrayList<>()).add(tag);
                }
            }
        }
    }

    public List<String> getTags(String dstPort, String protocol) {
        String key = dstPort.toLowerCase() + "," + protocol.toLowerCase();
        return lookupMap.getOrDefault(key, Collections.singletonList("Untagged"));
    }
}