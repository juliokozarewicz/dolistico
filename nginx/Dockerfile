FROM nginx:latest
RUN apt-get update && apt-get install -y gettext-base
COPY nginx.conf.template /etc/nginx/nginx.conf.template
RUN mkdir -p /var/log/nginx /tmp/nginx_cache
CMD envsubst ' \
    \
    $DOCUMENTATION_BASE_URL \
    $DOCUMENTATION_PORT \
    \
    $HELLOWORLD_BASE_URL \
    $HELLOWORLD_PORT \
    \
    $ACCOUNTS_BASE_URL \
    $ACCOUNTS_PORT \
    \
    $TASKS_BASE_URL \
    $TASKS_PORT \
    \
    ' \
    < /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf && \
    nginx -g 'daemon off;'
