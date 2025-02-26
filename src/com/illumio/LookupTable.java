package com.illumio;

import java.io.*;
import java.util.*;

public class LookupTable {
    private final Map<String, List<String>> lookupMap;
// Constructor for LookupTable.
    public LookupTable(String filePath) throws IOException {
        lookupMap = new HashMap<>();
        loadLookupTable(filePath);
    }
// Loads the lookup table from the specified CSV file.
    private void loadLookupTable(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                // Split the line by commas to extract dstport, protocol, and tag.
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    // Create a key by combining dstport and protocol (in lowercase).
                    String key = parts[0].toLowerCase() + "," + parts[1].toLowerCase();
                    String tag = parts[2];
                    // If the key doesn't exist, create a new list for it.
                    lookupMap.computeIfAbsent(key, k -> new ArrayList<>()).add(tag);
                }
            }
        }
    }

    public List<String> getTags(String dstPort, String protocol) {
    // Create a key by combining dstport and protocol (in lowercase).
        String key = dstPort.toLowerCase() + "," + protocol.toLowerCase();
        // Return the list of tags for the key, or a list containing "Untagged" if the key doesn't exist.
        return lookupMap.getOrDefault(key, Collections.singletonList("Untagged"));
    }
}
