cd ~/.ssh
echo "SendEnv MYSQL_HOST" >> config
echo "SendEnv MYSQL_NA_DB" >> config
echo "SendEnv MYSQL_NA_USER" >> config
echo "SendEnv MYSQL_NA_PASSWORD" >> config
echo "SendEnv SPRING_PROFILES_ACTIVE" >> config