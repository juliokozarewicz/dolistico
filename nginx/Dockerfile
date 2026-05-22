# Nginx version
FROM nginx:latest

# Clean default files
RUN rm -f /etc/nginx/conf.d/*

# Install Gettext
RUN apt-get update && apt-get install -y gettext-base

# Statics
COPY static /static/public

# Nginx conf
COPY nginx.conf.template /etc/nginx/nginx.conf.template

# Logs and cache
RUN mkdir -p /var/log/nginx /tmp/nginx_cache

# Run
CMD export PUBLIC_DOMAIN=$(echo "$PUBLIC_DOMAIN" \
| tr ',' '\n' \
| sed 's/^[[:space:]]*//;s/[[:space:]]*$//' \
| grep -v '^$' \
| tr '\n' ' ') && \
envsubst ' \
\
$PUBLIC_DOMAIN \
\
$DOCUMENTATION_BASE_URL \
$DOCUMENTATION_PORT \
\
$HELLO_WORLD_BASE_URL \
$HELLO_WORLD_PORT \
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