package com.illumio;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // Default input and output directories
            String inputDir = "input";
            String outputDir = "output";

            // Override directories if provided as command-line arguments
            if (args.length >= 2) {
                inputDir = args[0];
                outputDir = args[1];
            }

            // Load lookup table
            LookupTable lookupTable = new LookupTable(inputDir + "/lookup_table.csv");

            // Parse flow logs
            FlowLogParser parser = new FlowLogParser(inputDir + "/flow_logs.txt", lookupTable);
            parser.parse();

            // Generate outputs
            parser.writeTagCounts(outputDir + "/tag_counts.csv");
            parser.writePortProtocolCounts(outputDir + "/port_protocol_counts.csv");

            System.out.println("Processing complete. Output files generated in the '" + outputDir + "' folder.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}