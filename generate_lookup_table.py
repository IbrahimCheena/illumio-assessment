import random

# Constants
NUM_ENTRIES = 10000  # Number of mappings in the lookup table
OUTPUT_FILE = "lookup_table_10k.csv"

# Protocol options
PROTOCOLS = ["tcp", "udp", "icmp"]

# Tag options
TAGS = [f"sv_P{i}" for i in range(1, 101)]  # Tags like sv_P1, sv_P2, ..., sv_P100

# Generate lookup table
with open(OUTPUT_FILE, "w") as f:
    # Write header
    f.write("dstport,protocol,tag\n")

    # Generate entries
    for _ in range(NUM_ENTRIES):
        dstport = random.randint(1, 65535)  # Random port between 1 and 65535
        protocol = random.choice(PROTOCOLS)  # Random protocol
        tag = random.choice(TAGS)  # Random tag

        # Write entry
        f.write(f"{dstport},{protocol},{tag}\n")

print(f"Generated {NUM_ENTRIES} mappings in {OUTPUT_FILE}.")