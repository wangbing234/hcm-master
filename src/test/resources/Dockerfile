# 基础镜像
FROM nathonfowlie/centos-jre:1.8.0_60
MAINTAINER wanghuiyong@youzhao.io

RUN mkdir /var/hcm/
RUN mkdir /var/logs/
# 修改容器时区
RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
&& echo 'Asia/Shanghai' >/etc/timezone
# 设置工作目录
WORKDIR /var/hcm/
#ADD hcm.conf /var/hcm/hcm.conf
ADD build/libs/hcm-0.0.1-SNAPSHOT.jar /var/hcm/hcm.jar
ADD agent.zip /var/hcm/agent.zip
#ADD deploy.sh /var/hcm/deploy.sh
#RUN sh -c 'touch /var/hcm/hcm.conf'
RUN sh -c 'touch /var/hcm/hcm.jar'
RUN sh -c 'touch /var/hcm/agent.zip'

##安装软件
#RUN apt-get -y update
RUN yum install -y unzip
#RUN yum install -y lrzsz
#RUN sh -c 'touch /var/hcm/deploy.sh'
#RUN chmod u+x /var/hcm/deploy.sh
RUN unzip agent.zip
ENV JAVA_OPTS = ""
#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /var/hcm/hcm.jar --spring.profiles.active=$env >> /var/logs/hcm.log 2>&1"]
#CMD ["sh /var/hcm/deploy.sh dev"]
#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /var/hcm/hcm.jar --spring.profiles.active=$env >> /dev/null 2>&1"]
#ENTRYPOINT [ "sh", "-c", "java -javaagent:agent/skywalking-agent.jar -Dagent.application_code=hcm_api_$env -Djava.security.egd=file:/dev/./urandom -jar /var/hcm/hcm.jar --spring.profiles.active=$env >> /dev/null 2>&1 "]
ENTRYPOINT [ "sh", "-c", "java -javaagent:agent/pinpoint-bootstrap-1.7.3.jar -Dpinpoint.applicationName=hcm_api_$env -Dpinpoint.agentId=101.132.248.102 -Djava.security.egd=file:/dev/./urandom -jar /var/hcm/hcm.jar --spring.profiles.active=$env >> /dev/null 2>&1 "]