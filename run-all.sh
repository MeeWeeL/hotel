#!/usr/bin/env bash
set -e
echo "Starting Eureka..."
(mvn -q -pl eureka-server spring-boot:run &) ; sleep 5
echo "Starting Gateway..."
(mvn -q -pl api-gateway spring-boot:run &) ; sleep 5
echo "Starting Hotel Service..."
(mvn -q -pl hotel-service spring-boot:run &) ; sleep 5
echo "Starting Booking Service..."
(mvn -q -pl booking-service spring-boot:run &) ; sleep 1
echo "All services started. Press Ctrl+C to stop foreground process."
wait
