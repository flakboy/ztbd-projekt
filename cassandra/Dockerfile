FROM cassandra:5.0.4

# ARG CASSANDRA_JMX_USER
# ARG CASSANDRA_JMX_PASSWORD

RUN mkdir /init
COPY ./cassandra/entrypoint.sh /init/entrypoint.sh
COPY ./cassandra/init/init.cql /init/init.cql
RUN chmod +x /init/entrypoint.sh

# add a JMX user used for remote authentication
# RUN echo ${CASSANDRA_JMX_USER} ${CASSANDRA_JMX_PASSWORD} >> /etc/cassandra/jmxremote.password
# RUN chown cassandra:cassandra /etc/cassandra/jmxremote.password && chmod 400 /etc/cassandra/jmxremote.password
# RUN echo ${CASSANDRA_JMX_USER} readwrite >> /etc/cassandra/jmxremote.access

# ENV JVM_OPTS="$JVM_OPTS -Djava.rmi.server.hostname=$(hostname -i)" 
# CMD [ "cassandra", "-f" ]
# CMD [ "ls -l /" ]
# CMD [ "sleep", "36000" ]
CMD [ "/init/entrypoint.sh" ]