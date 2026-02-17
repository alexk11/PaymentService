1. Postgres

 kubectl create namespace db || true
 helm upgrade --install my-pg oci://registry-1.docker.io/bitnamicharts/postgresql -n db
 --set auth.postgresPassword=secret --set primary.persistence.size=8Gi --set image.registry=docker.io
 --set image.repository=bitnamilegacy/postgresql --set image.tag=17.6.0-debian-12-r4

2. Kafka

 kubectl create namespace kafka || true
 helm upgrade --install kafka oci://registry-1.docker.io/bitnamicharts/kafka -n kafka -f kafka-values.yml
 --set image.registry=docker.io --set image.repository=bitnamilegacy/kafka --set image.tag=4.0.0-debian-12-r10

3. RabbitMQ

 kubectl create namespace rabbit || true
 helm upgrade --install my-rabbit oci://registry-1.docker.io/bitnamicharts/rabbitmq -n rabbit
 --set auth.username=admin --set auth.password=admin --set auth.erlangCookie=secretcookie \
 --set image.registry=docker.io --set image.repository=bitnamilegacy/rabbitmq --set image.tag=4.1.3-debian-12-r1 \
 --set global.security.allowInsecureImages=true

4. Keycloak

  kubectl create namespace keycloak || true
  helm install keycloak bitnami/keycloak -f keycloak-values.yaml


