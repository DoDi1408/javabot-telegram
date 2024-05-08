# javabot-telegram
This will not work without a wallet folder for a database connection, this wallet folder should be at project root. By wallet I mean the folder that is generated from an autonomous database on OCI to establish a database connection.
# The following env variables will be needed
 * DIRECTORY: This is the absolute path to the wallet folder
* DB_CREDENTIALS_USR
* DB_CREDENTIALS_PSW
* BOT_CREDENTIALS_PSW : Telegram bot token
<h1>Architecture</h1>

![architecture](https://github.com/dodi1408/javabot-telegram/blob/main/archi.png?raw=true)

<h1>CI/CD</h1>

![devops](https://github.com/dodi1408/javabot-telegram/blob/main/PIPELINE.png?raw=true)

