#融360 amqp_http_wrapper启动脚本
#!/bin/bash
appPath="/home/rong/www/amqp-http-wrapper/zt3862266"
export LANG=zh_CN

start()
{
# cd $appPath

for file in `ls ./src/main/resources/`
  do
    if [ -d "./src/main/resources/$file" ]; then
	echo "正在启动示例:"$file
	mvn exec:java -Dlog4j.configuration=file:./src/main/resources/$file/log4j.properties -Dexec.mainClass="org.zhangtao.App" -Dexec.args="$file">/dev/null 2>&1 &
	#检查下启动状态
	sleep 1
	pid=`ps aux | grep "/$file/log4j.properties" | grep -v grep | awk  '{print $2}'`
	if [ -z $pid ];then
		echo "启动失败!重试"
		mvn exec:java -Dlog4j.configuration=file:./src/main/resources/$file/log4j.properties -Dexec.mainClass="org.zhangtao.App" -Dexec.args="$file">/dev/null 2>&1 &
	fi
    fi
done
}

check()
{
# cd $appPath

for file in `ls ./src/main/resources/`
  do
    if [ -d "./src/main/resources/$file" ]; then
      	echo "检查启动情况:"$file
	  pid=`ps aux | grep "/$file/log4j.properties" | grep -v grep | awk '{print $2}'`
	  if [ -z $pid ];then
		  echo "启动失败! 重试"
		  exit 1
	  fi
    fi
done
}

#启动
start

#检查进程
check
