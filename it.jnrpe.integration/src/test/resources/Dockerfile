FROM alpine:3.14
RUN apk add --no-cache bash gcc procps libc-dev openssl-dev make openssl
RUN wget https://github.com/NagiosEnterprises/nrpe/releases/download/nrpe-4.0.3/nrpe-4.0.3.tar.gz
RUN tar xvfz nrpe-4.0.3.tar.gz
RUN cd nrpe-4.0.3 && ./configure && make check_nrpe
RUN echo 'ping localhost &' > /bootstrap.sh
RUN echo 'sleep infinity' >> /bootstrap.sh
RUN chmod +x /bootstrap.sh


FROM alpine:3.14
COPY --from=0 /nrpe-4.0.3/src/check_nrpe /usr/bin
COPY --from=0 /bootstrap.sh /bootstrap.sh
CMD /bootstrap.sh
