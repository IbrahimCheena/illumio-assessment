package com.illumio;

import java.io.*;
import java.util.*;

public class FlowLogParser {
    private final String filePath;
    private final LookupTable lookupTable;
    private final Map<String, Integer> tagCounts;
    private final Map<String, Integer> portProtocolCounts;

    // Map protocol numbers to names
    private static final Map<String, String> PROTOCOL_MAP = new HashMap<>();
    static {
        PROTOCOL_MAP.put("6", "tcp");  // TCP
        PROTOCOL_MAP.put("17", "udp"); // UDP
        PROTOCOL_MAP.put("1", "icmp"); // ICMP
    }

    public FlowLogParser(String filePath, LookupTable lookupTable) {
        this.filePath = filePath;
        this.lookupTable = lookupTable;
        this.tagCounts = new HashMap<>();
        this.portProtocolCounts = new HashMap<>();
    }

    public void parse() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length < 9) {
                    System.err.println("Invalid log entry: " + line);
                    continue; // Skip invalid entries
                }

                // Correctly parse destination port and protocol
                String dstPort = parts[6]; // 7th field (Dest. Port) [AWS]
                String protocolNumber = parts[7]; // 8th field (Protocol) [AWS]

                // Map protocol number to name
                String protocol = PROTOCOL_MAP.getOrDefault(protocolNumber, "unknown");

                // Count port/protocol combinations
                String portProtocolKey = dstPort + "," + protocol;
                portProtocolCounts.put(portProtocolKey, portProtocolCounts.getOrDefault(portProtocolKey, 0) + 1);

                // Get all tags for the dstPort and protocol combination
                List<String> tags = lookupTable.getTags(dstPort, protocol);
                for (String tag : tags) {
                    tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
                }
            }
        }
    }

    public void writeTagCounts(String outputPath) throws IOException {
        // Ensure "Untagged" is included with a count of 0 if it doesn't exist
        tagCounts.putIfAbsent("Untagged", 0);

        // Sort tags alphabetically
        writeCounts(outputPath, tagCounts, "Tag,Count", Comparator.naturalOrder());
    }

    public void writePortProtocolCounts(String outputPath) throws IOException {
        // Sort port/protocol combinations numerically by port
        writeCounts(outputPath, portProtocolCounts, "Port,Protocol,Count", (key1, key2) -> {
            int port1 = Integer.parseInt(key1.split(",")[0]);
            int port2 = Integer.parseInt(key2.split(",")[0]);
            return Integer.compare(port1, port2);
        });
    }

    private void writeCounts(String outputPath, Map<String, Integer> counts, String header, Comparator<String> comparator) throws IOException {
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(header + "\n");
            counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(comparator))
                .forEach(entry -> {
                    try {
                        writer.write(entry.getKey() + "," + entry.getValue() + "\n");
                    } catch (IOException e) {
                        System.err.println("Error writing to file: " + e.getMessage());
                    }
                });
        }
    }
}