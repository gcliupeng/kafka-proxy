#!/bin/bash
ps -ef | grep org.zhangtao.App | grep -v grep | awk '{print $2}' | xargs kill -15
sleep 6
