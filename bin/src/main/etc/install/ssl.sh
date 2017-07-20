# Directory
mkdir /home/ssl

# Create certificate, valid 3000 days (10 years)
openssl req -x509 -nodes -newkey rsa:4096 -keyout /home/ssl/key.pem -out /home/ssl/cert.pem -days 3000