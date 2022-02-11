FROM confluentinc/cp-kafka-connect:7.0.1

ENV CONNECT_PLUGIN_PATH="/usr/share/java,/usr/share/confluent-hub-components"

RUN confluent-hub install --no-prompt jcustenborder/kafka-connect-twitter:0.3.34
