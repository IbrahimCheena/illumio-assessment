#!/bin/bash

# Directory containing test cases
TEST_CASES_DIR="test_cases"

# Temporary directories for input and output
TEMP_INPUT_DIR="temp_input"
TEMP_OUTPUT_DIR="temp_output"

# Create temporary directories
mkdir -p "$TEMP_INPUT_DIR"
mkdir -p "$TEMP_OUTPUT_DIR"

# Compile the code
echo "Compiling the code..."
javac -d out src/com/illumio/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed. Exiting."
    exit 1
fi

# Loop through all test cases
for test_case in "$TEST_CASES_DIR"/*; do
  echo "Running test case: $(basename "$test_case")"

  # Copy input files to temporary input directory
  cp "$test_case/flow_logs.txt" "$TEMP_INPUT_DIR/"
  cp "$test_case/lookup_table.csv" "$TEMP_INPUT_DIR/"

  # Run the program with temporary input and output directories
  java -cp out com.illumio.Main "$TEMP_INPUT_DIR" "$TEMP_OUTPUT_DIR"

  if [ $? -ne 0 ]; then
    echo "Error running the program for test case $(basename "$test_case"). Skipping."
    continue
  fi

  # Compare output files, ignoring whitespace differences
  diff -w -q "$TEMP_OUTPUT_DIR/tag_counts.csv" "$test_case/expected_output/tag_counts.csv" > /dev/null
  TAG_DIFF_RESULT=$?

  diff -w -q "$TEMP_OUTPUT_DIR/port_protocol_counts.csv" "$test_case/expected_output/port_protocol_counts.csv" > /dev/null
  PORT_DIFF_RESULT=$?

  # Check if the test passed
  if [ $TAG_DIFF_RESULT -eq 0 ] && [ $PORT_DIFF_RESULT -eq 0 ]; then
    echo "Test case $(basename "$test_case") PASSED"
  else
    echo "Test case $(basename "$test_case") FAILED"
  fi

  # Clean up temporary files
  rm -f "$TEMP_INPUT_DIR/flow_logs.txt" "$TEMP_INPUT_DIR/lookup_table.csv"
  rm -f "$TEMP_OUTPUT_DIR/tag_counts.csv" "$TEMP_OUTPUT_DIR/port_protocol_counts.csv"
done

# Remove temporary directories
rmdir "$TEMP_INPUT_DIR"
rmdir "$TEMP_OUTPUT_DIR"

echo "All test cases completed."