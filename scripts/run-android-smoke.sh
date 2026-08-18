#!/usr/bin/env bash

set +e

echo "========================================"
echo "ANDROID MOBILE AUTOMATION"
echo "========================================"

echo "Workspace:"
pwd

echo "========================================"
echo "CREATE DIRECTORIES"
echo "========================================"

mkdir -p logs
mkdir -p screenshots
mkdir -p diagnostics


# ============================================================
# ANDROID DEVICE
# ============================================================

echo "========================================"
echo "ANDROID DEVICE"
echo "========================================"

adb devices -l

echo "========================================"
echo "ANDROID VERSION"
echo "========================================"

adb shell getprop ro.build.version.release

echo "========================================"
echo "ANDROID MODEL"
echo "========================================"

adb shell getprop ro.product.model

echo "========================================"
echo "ANDROID BOOT STATUS"
echo "========================================"

adb shell getprop sys.boot_completed


# ============================================================
# SAVE INITIAL DEVICE INFORMATION
# ============================================================

adb devices -l > diagnostics/devices-initial.txt

adb shell getprop > diagnostics/android-properties.txt


# ============================================================
# APK INFORMATION
# ============================================================

echo "========================================"
echo "APK INFORMATION"
echo "========================================"

ls -lh apps/mda.apk

file apps/mda.apk


# ============================================================
# INSTALL APK
# ============================================================

echo "========================================"
echo "INSTALL APK"
echo "========================================"

adb install -r "$(pwd)/apps/mda.apk"

APK_INSTALL_EXIT=$?

echo "APK INSTALL EXIT CODE: $APK_INSTALL_EXIT"

if [ "$APK_INSTALL_EXIT" -ne 0 ]; then
    echo "ERROR: APK installation failed."

    adb devices -l > diagnostics/devices-install-failure.txt

    adb logcat -d > logs/android-install-failure.log

    exit "$APK_INSTALL_EXIT"
fi


# ============================================================
# VERIFY PACKAGE
# ============================================================

echo "========================================"
echo "VERIFY PACKAGE"
echo "========================================"

adb shell pm list packages > diagnostics/packages.txt

grep "com.saucelabs.mydemoapp.android" diagnostics/packages.txt

PACKAGE_EXIT=$?

echo "PACKAGE CHECK EXIT CODE: $PACKAGE_EXIT"


# ============================================================
# CLEAR APPLICATION DATA
# ============================================================

echo "========================================"
echo "CLEAR APPLICATION DATA"
echo "========================================"

adb shell pm clear com.saucelabs.mydemoapp.android

CLEAR_EXIT=$?

echo "PM CLEAR EXIT CODE: $CLEAR_EXIT"

sleep 3


# ============================================================
# FORCE STOP APP
# ============================================================

echo "========================================"
echo "FORCE STOP APPLICATION"
echo "========================================"

adb shell am force-stop com.saucelabs.mydemoapp.android


# ============================================================
# START APP
# ============================================================

echo "========================================"
echo "START APPLICATION"
echo "========================================"

adb shell monkey -p com.saucelabs.mydemoapp.android 1

sleep 10


# ============================================================
# CURRENT ACTIVITY
# ============================================================

echo "========================================"
echo "CURRENT ACTIVITY"
echo "========================================"

adb shell dumpsys window > diagnostics/window-clean-launch.txt

grep -E "mCurrentFocus|mFocusedApp" diagnostics/window-clean-launch.txt


# ============================================================
# APP PROCESS
# ============================================================

echo "========================================"
echo "APP PROCESS"
echo "========================================"

adb shell pidof com.saucelabs.mydemoapp.android


# ============================================================
# CLEAN LAUNCH SCREENSHOT
# ============================================================

echo "========================================"
echo "CLEAN LAUNCH SCREENSHOT"
echo "========================================"

adb exec-out screencap -p > screenshots/clean-launch.png


# ============================================================
# CLEAN LAUNCH UI HIERARCHY
# ============================================================

echo "========================================"
echo "CLEAN LAUNCH UI HIERARCHY"
echo "========================================"

adb shell uiautomator dump /sdcard/clean-launch.xml

adb pull /sdcard/clean-launch.xml diagnostics/clean-launch.xml


# ============================================================
# START APPIUM
# ============================================================

echo "========================================"
echo "START APPIUM"
echo "========================================"

appium --log-timestamp > logs/appium.log 2>&1 &

APPIUM_PID=$!

echo "APPIUM PID: $APPIUM_PID"

sleep 8


# ============================================================
# APPIUM STATUS
# ============================================================

echo "========================================"
echo "APPIUM STATUS"
echo "========================================"

curl -s http://127.0.0.1:4723/status > diagnostics/appium-status.json

cat diagnostics/appium-status.json

echo


# ============================================================
# APPIUM LOG
# ============================================================

echo "========================================"
echo "APPIUM LOG"
echo "========================================"

cat logs/appium.log


# ============================================================
# UI BEFORE TEST
# ============================================================

echo "========================================"
echo "UI BEFORE TEST"
echo "========================================"

adb shell uiautomator dump /sdcard/window-before-test.xml

adb pull /sdcard/window-before-test.xml diagnostics/window-before-test.xml

adb exec-out screencap -p > screenshots/before-test.png


# ============================================================
# RUN MAVEN
# ============================================================

echo "========================================"
echo "RUNNING MAVEN TESTS"
echo "========================================"

mvn clean test \
    -Dgroups=smoke \
    -Dplatform=android \
    -Denv=qa \
    -Dapp="$(pwd)/apps/mda.apk" \
    -DdeviceName=emulator-5554 \
    -DappiumServerUrl=http://127.0.0.1:4723 \
    -DnoReset=false \
    -DfullReset=false \
    -DsuiteXmlFile=testng.xml

TEST_EXIT_CODE=$?

echo "========================================"
echo "MAVEN EXIT CODE"
echo "========================================"

echo "$TEST_EXIT_CODE"


# ============================================================
# POST TEST DIAGNOSTICS
# ============================================================

echo "========================================"
echo "POST TEST DIAGNOSTICS"
echo "========================================"

adb devices -l > diagnostics/devices-after-test.txt

adb shell dumpsys window > diagnostics/window-after-test.txt

adb shell dumpsys activity > diagnostics/activity-after-test.txt

adb shell dumpsys package com.saucelabs.mydemoapp.android \
    > diagnostics/app-package-after-test.txt

grep -E "mCurrentFocus|mFocusedApp" \
    diagnostics/window-after-test.txt \
    > diagnostics/current-activity.txt


# ============================================================
# POST TEST UI
# ============================================================

echo "========================================"
echo "POST TEST UI"
echo "========================================"

adb shell uiautomator dump /sdcard/window-after-test.xml

adb pull /sdcard/window-after-test.xml \
    diagnostics/window-after-test.xml


# ============================================================
# FINAL SCREENSHOT
# ============================================================

echo "========================================"
echo "FINAL SCREENSHOT"
echo "========================================"

adb exec-out screencap -p \
    > screenshots/ci-final-screen.png


# ============================================================
# ANDROID LOGCAT
# ============================================================

echo "========================================"
echo "ANDROID LOGCAT"
echo "========================================"

adb logcat -d > logs/android-logcat.txt


# ============================================================
# COPY APPIUM LOG
# ============================================================

echo "========================================"
echo "COPY APPIUM LOG"
echo "========================================"

cp logs/appium.log diagnostics/appium.log


# ============================================================
# MAVEN REPORTS
# ============================================================

echo "========================================"
echo "SUREFIRE REPORTS"
echo "========================================"

find target/surefire-reports \
    -maxdepth 2 \
    -type f \
    -print 2>/dev/null


# ============================================================
# STOP APPIUM
# ============================================================

echo "========================================"
echo "STOP APPIUM"
echo "========================================"

kill "$APPIUM_PID" 2>/dev/null || true


# ============================================================
# FINAL RESULT
# ============================================================

echo "========================================"
echo "FINAL TEST RESULT"
echo "========================================"

echo "Maven exit code: $TEST_EXIT_CODE"

exit "$TEST_EXIT_CODE"