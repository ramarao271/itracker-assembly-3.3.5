#!
# 
# ==========================================================
# SCRIPT for migrating itracker 2.4 MySQL-database to 3.0
# ==========================================================
#
# Usage: Call with optional DB-Password as the only argument
#  $ ./create_itracker_mig_and_transform.sh [<password>]
# 
# 1. DROPS and CREATES target datbase ($DBTARGET)
# 2. Does dump the existing itracker database ($DATABASE)
# 3. Loads the dump-file to target datase
# 4. Transforms the imported database to new DDL
#
#

DBUSER=root
DBPASS="$1"
DATABASE=itracker
DBTARGET=itracker_mig
DBHOST=localhost


echo "Transforming itracker 2.4 at '$DATABASE' on $DBHOST to 3.0 at '$DBTARGET' with user '$DBUSER'"

echo "dropping $DBTARGET"
mysqladmin -f -h$DBHOST -u"$DBUSER" -p"$DBPASS" DROP "$DBTARGET"

echo "creating $DBTARGET" 
mysqladmin -h$DBHOST -u"$DBUSER" -p"$DBPASS" CREATE "$DBTARGET"

DUMPFILE="./"$DATABASE"_dump.sql"

echo "dumping $DATABASE TO $DUMPFILE ..."
mysqldump -h$DBHOST -u$DBUSER -p"$DBPASS" "$DATABASE" > "$DUMPFILE"

echo "importing $DUMPFILE TO $DBTARGET ..."
mysql -h$DBHOST -u"$DBUSER" -p"$DBPASS" "$DBTARGET" < "$DUMPFILE"

echo "applying transformations of database structure ..."
mysql -h$DBHOST -u"$DBUSER" -p"$DBPASS" "$DBTARGET" < ./itracker_migration_transform_script.sql

mysql -h$DBHOST -u"$DBUSER" -p"$DBPASS" "$DBTARGET" < ./itracker_migration_new_indexes.sql

echo "DONE: Successfully dumped and transformed to 3.0"