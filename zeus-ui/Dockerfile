FROM nginx:alpine

WORKDIR /usr/share/nginx/html

ARG UI_DIST=build/web

COPY ${UI_DIST} .

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]