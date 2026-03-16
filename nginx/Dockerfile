# Nginx version
FROM nginx:latest

# Clean default files
RUN rm -f /etc/nginx/conf.d/*

# Nginx conf
COPY nginx.conf /etc/nginx

# Statics
COPY statics_files /static/public

# Logs and cache
RUN mkdir -p /var/log/nginx /tmp/nginx_cache

# Run
CMD ["nginx", "-g", "daemon off;"]
