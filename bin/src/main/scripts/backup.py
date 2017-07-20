             #!/usr/bin/env python
# Creates a zipped copy of bookmarks database and then ftp's to the bookmarks website
#
import time
import os
from ftplib import FTP
import os, random, struct
# from Crypto.Cipher import AES

#  t_filename=None, chunksize=24*1024):
    # """ Decrypts a file using AES (CBC mode) with the
        # given key. Parameters are similar to encrypt_file,
        # with one difference: out_filename, if not supplied
        # will be in_filename without its last extension
        # (i.e. if in_filename is 'aaa.zip.enc' then
        # out_filename will be 'aaa.zip')
    # """
    # if not out_filename:
        # out_filename = os.path.splitext(in_filename)[0]

    # with open(in_filename, 'rb') as infile:
        # origsize = struct.unpack('<Q', infile.read(struct.calcsize('Q')))[0]
        # iv = infile.read(16)
        # decryptor = AES.new(key, AES.MODE_CBC, iv)

        # with open(out_filename, 'wb') as outfile:
            # while True:
                # chunk = infile.read(chunksize)
                # if len(chunk) == 0:
                    # break
                # outfile.write(decryptor.decrypt(chunk))

            # outfile.truncate(origsize)

# def encrypt_file(key, in_filename, out_filename=None, chunksize=64*1024):
    # """ Encrypts a file using AES (CBC mode) with the
        # given key.

        # key:
            # The encryption key - a string that must be
            # either 16, 24 or 32 bytes long. Longer keys
            # are more secure.

        # in_filename:
            # Name of the input file

        # out_filename:
            # If None, '<in_filename>.enc' will be used.

        # chunksize:
            # Sets the size of the chunk which the function
            # uses to read and encrypt the file. Larger chunk
            # sizes can be faster for some files and machines.
            # chunksize must be divisible by 16.
    # """
    # if not out_filename:
        # out_filename = in_filename + '.enc'

    # iv = ''.join(chr(random.randint(0, 0xFF)) for i in range(16))
    # encryptor = AES.new(key, AES.MODE_CBC, iv)
    # filesize = os.path.getsize(in_filename)

    # with open(in_filename, 'rb') as infile:
        # with open(out_filename, 'wb') as outfile:
            # outfile.write(struct.pack('<Q', filesize))
            # outfile.write(iv)

            # while True:
                # chunk = infile.read(chunksize)
                # if len(chunk) == 0:
                    # break
                # elif len(chunk) % 16 != 0:
                    # chunk += ' ' * (16 - len(chunk) % 16)


filestamp = time.strftime('%Y-%m-%d')
#filestamp = time.strftime('%Y-%m-%d-%H-%M') #For marxism
filePath = os.path.normpath("/home/bookmarks/backup") + "/"
filename = "bookmarks-%s.sql" % (filestamp)
fullPathFilename = filePath + filename


#Dump file
print ("Dumping database to %s" % fullPathFilename)
os.system("mysqldump -uroot -padmin bookmarks > %s.sql" % (fullPathFilename))

#Compress
os.system("rar a -m5 %s.rar %s.sql" % (fullPathFilename, fullPathFilename))

#Encrypt - Get this working
#encrypt_file("this string has ", in_filename = fullPathFilename + ".gz")
file = open(fullPathFilename + ".rar", "rb") 

ftp = FTP('ftp.bookmarksbookshop.co.uk', 'u34059913-kevin','obama08elected')
ftp.cwd('backup')
ftp.storbinary('STOR %s.rar' % (filename), file) 
ftp.retrlines('LIST')
ftp.quit()
file.close()

#Clean up
os.remove('%s.sql' % fullPathFilename)
