#!/bin/sh

. ./app.conf

tail -f "$DEPLOY_DIR/logs/app-access.log"