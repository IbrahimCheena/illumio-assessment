import random

# Constants
NUM_ENTRIES = 60000  # Adjust this to reach ~10 MB
OUTPUT_FILE = "flow_logs_10mb.txt"

# Flow log components
VERSION = "2"
ACCOUNT_ID = "123456789012"
INTERFACE_ID = "eni-0a1b2c3d"
SRC_ADDR = "10.0.1.201"
DST_ADDR = "198.51.100.2"
PROTOCOL = "6"  # TCP
ACTION = "ACCEPT"
LOG_STATUS = "OK"

# Generate flow logs
with open(OUTPUT_FILE, "w") as f:
    for _ in range(NUM_ENTRIES):
        src_port = random.randint(1024, 65535)
        dst_port = random.randint(1, 65535)
        packets = random.randint(1, 100)
        bytes_sent = random.randint(1000, 100000)
        start_time = random.randint(1620140761, 1620140821)
        end_time = start_time + random.randint(1, 60)

        # Write flow log entry
        f.write(
            f"{VERSION} {ACCOUNT_ID} {INTERFACE_ID} {SRC_ADDR} {DST_ADDR} {src_port} {dst_port} {PROTOCOL} {packets} {bytes_sent} {start_time} {end_time} {ACTION} {LOG_STATUS}\n"
        )

print(f"Generated {NUM_ENTRIES} flow log entries in {OUTPUT_FILE}.")