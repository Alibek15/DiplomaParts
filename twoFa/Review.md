# JWT RSA Keys for Local Development

This project uses JWT tokens signed with an RSA private key (`private.pem`).  
**The private key is NOT stored in this repository for security reasons.**

## How to generate your own keys

Before running the project, generate a new pair of keys in `src/main/resources/keys/`:

```sh
# Go to the project root folder first
cd path/to/your/project

# Create the directory if it doesn't exist
mkdir -p src/main/resources/keys

# 1. Generate a new private key (PKCS#8 format, RSA 2048)
openssl genpkey -algorithm RSA -out src/main/resources/keys/private.pem -pkeyopt rsa_keygen_bits:2048

# 2. Generate the corresponding public key
openssl rsa -pubout -in src/main/resources/keys/private.pem -out src/main/resources/keys/public.pem
