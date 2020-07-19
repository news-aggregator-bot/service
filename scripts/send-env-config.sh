cd ~/.ssh
echo "SendEnv MYSQL_HOST" >> tempconfig
echo "SendEnv MYSQL_NA_DB" >> tempconfig
echo "SendEnv MYSQL_NA_USER" >> tempconfig
echo "SendEnv MYSQL_NA_PASSWORD" >> tempconfig
echo "SendEnv SPRING_PROFILES_ACTIVE" >> tempconfig
value=$(<config)
echo "$value" >> tempconfig
cp tempconfig config
cat tempconfig
rm tempconfig