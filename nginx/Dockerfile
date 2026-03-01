# Nginx version
FROM nginx:latest

# Clean default files
RUN rm -f /etc/nginx/conf.d/default.conf /etc/nginx/nginx.conf

# Nginx confs
COPY nginx.conf /etc/nginx
COPY 1_servers /etc/nginx/1_servers
COPY 2_configs /etc/nginx/2_configs
COPY 3_locations /etc/nginx/3_locations

# Statics
COPY 4_statics /static/public

# Logs and cache
RUN mkdir -p /var/log/nginx /tmp/nginx_cache

# Run
CMD ["nginx", "-g", "daemon off;"]
