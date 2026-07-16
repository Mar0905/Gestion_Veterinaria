#!/bin/bash

DB_NAME="gestion_veterinaria"
DB_USER="root"
DB_PASS=""
BACKUP_DIR="backups"
DATE=$(date +%Y%m%d)
FILE="$BACKUP_DIR/vetsys_$DATE.sql"

mkdir -p "$BACKUP_DIR"

mysqldump -u "$DB_USER" ${DB_PASS:+-p"$DB_PASS"} "$DB_NAME" > "$FILE"

if [ $? -eq 0 ]; then
    echo "Backup creado: $FILE"
else
    echo "Error al crear el backup" >&2
    exit 1
fi
