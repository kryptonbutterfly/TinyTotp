#!/bin/bash

export SUDO_ASKPASS="/opt/custom_commands/zenityPw.sh"

if [ "$(id -u)" != "0" ]; then
	exec sudo "$0" "$@"
fi

cd ./deb-content
echo ${pwd}

VERSION_FILE="version.txt"

APP_NAME="TinyTotp"
PACKAGE_NAME=$APP_NAME
PACKAGE_VERSION=$(<$VERSION_FILE) 
SOURCE_DIR=$PWD/../
DEPLOY_DIR="/home/$SUDO_USER/ownCloud/Applications"
BUILD_DIR="/tmp/debian"

mkdir -p $BUILD_DIR/DEBIAN
mkdir -p $BUILD_DIR/usr/share/applications
mkdir -p $BUILD_DIR/usr/share/mime
mkdir -p $BUILD_DIR/usr/share/mime/packages
mkdir -p $BUILD_DIR/usr/share/$PACKAGE_NAME
mkdir -p $BUILD_DIR/usr/share/doc/$PACKAGE_NAME
mkdir -p $BUILD_DIR/usr/share/common-licenses/$PACKAGE_NAME

echo "Package: $PACKAGE_NAME" > $BUILD_DIR/DEBIAN/control
echo "Version: $PACKAGE_VERSION" >> $BUILD_DIR/DEBIAN/control
cat control >> $BUILD_DIR/DEBIAN/control

cp $APP_NAME.desktop $BUILD_DIR/usr/share/applications/

cp $SOURCE_DIR/build/$APP_NAME.jar $BUILD_DIR/usr/share/$PACKAGE_NAME/

echo "$PACKAGE_NAME ($PACKAGE_VERSION) trusty; urgency=low" > changelog
echo "  * Rebuild" >> changelog
echo " -- tinycodecrank <tinycodecrank@gmail.com> `date -R`" >> changelog
gzip -9c changelog > $BUILD_DIR/usr/share/doc/$PACKAGE_NAME/changelog.gz

cp *.svg $BUILD_DIR/usr/share/$PACKAGE_NAME/
cp *.png $BUILD_DIR/usr/share/$PACKAGE_NAME/

chmod 0664 $BUILD_DIR/usr/share/$PACKAGE_NAME/*svg
chmod 0664 $BUILD_DIR/usr/share/$PACKAGE_NAME/*png

PACKAGE_SIZE=`du -bs $BUILD_DIR | cut -f 1`
PACKAGE_SIZE=$((PACKAGE_SIZE/1024))
echo "Installed-Size: $PACKAGE_SIZE" >> $BUILD_DIR/DEBIAN/control

chown -R root $BUILD_DIR/
chgrp -R root $BUILD_DIR/

cd /tmp
dpkg --build debian
mv /tmp/debian.deb $SOURCE_DIR/build/$PACKAGE_NAME-$PACKAGE_VERSION.deb
rm -r $BUILD_DIR

cp $SOURCE_DIR/build/$PACKAGE_NAME-$PACKAGE_VERSION.deb $DEPLOY_DIR/Deb/
cp $SOURCE_DIR/build/$APP_NAME.jar $DEPLOY_DIR/
