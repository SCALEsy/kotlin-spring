#!/bin/sh
app_path=..
jars_path=$app_path/lib
conf_path=$app_path/conf
env=dev
app_class=com.sharecreator.Application
jars=`find $jars_path -name "*.jar"`

cp_env=$conf_path

for jar in $jars
do
    cp_env=$cp_env:$jar
done

# 如果Main方法所在的主 jar 包，不在 lib 目录下，则需要再把主 jar 包也接入 cn_env
# cp_env=$cp_env:$app_path/test.jar

psid=0
checkpid() {
   javaps=`$JAVA_HOME/bin/jps -l | grep $app_class`

   if [ -n "$javaps" ]; then
      psid=`echo $javaps | awk '{print $1}'`
   else
      psid=0
   fi
}

start() {
   checkpid
   if [ $psid -ne 0 ]; then
      echo "================================"
      echo "warn: $app_class already started! (pid=$psid)"
      echo "================================"
   else
      echo -n "Starting $app_class ..."
      nohup java -Dfile.encoding=utf8 -Dspring.profiles.active=$env -cp $cp_env  $app_class >nohup.out 2>&1 &
      checkpid
      if [ $psid -ne 0 ]; then
         echo "(pid=$psid) [OK]"
      else
         echo "[Failed]"
      fi
   fi
}
status() {
   checkpid

   if [ $psid -ne 0 ];  then
      echo "$APP_MAINCLASS is running! (pid=$psid)"
   else
      echo "$APP_MAINCLASS is not running"
   fi
}
stop() {
   checkpid

   if [ $psid -ne 0 ]; then
      echo -n "Stopping $app_class ...(pid=$psid) "
      kill -9 $psid
      if [ $? -eq 0 ]; then
         echo "[OK]"
      else
         echo "[Failed]"
      fi

      checkpid
      if [ $psid -ne 0 ]; then
         stop
      fi
   else
      echo "================================"
      echo "warn: $app_class is not running"
      echo "================================"
   fi
}
stop