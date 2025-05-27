#!/bin/bash

# Download the Gradle wrapper JAR file
# This is a critical file needed for Codemagic builds

mkdir -p /home/ubuntu/DastakMobile7/gradle/wrapper
curl -L https://github.com/gradle/gradle/raw/master/gradle/wrapper/gradle-wrapper.jar -o /home/ubuntu/DastakMobile7/gradle/wrapper/gradle-wrapper.jar

echo "Gradle wrapper JAR downloaded successfully"
echo "Checking if all required Gradle files are present:"

# Check if all required Gradle files exist
files=(
  "/home/ubuntu/DastakMobile7/build.gradle"
  "/home/ubuntu/DastakMobile7/app/build.gradle"
  "/home/ubuntu/DastakMobile7/settings.gradle"
  "/home/ubuntu/DastakMobile7/gradle/wrapper/gradle-wrapper.properties"
  "/home/ubuntu/DastakMobile7/gradle/wrapper/gradle-wrapper.jar"
  "/home/ubuntu/DastakMobile7/gradlew"
  "/home/ubuntu/DastakMobile7/gradlew.bat"
  "/home/ubuntu/DastakMobile7/codemagic.yaml"
)

all_files_exist=true
for file in "${files[@]}"; do
  if [ -f "$file" ]; then
    echo "✓ $file exists"
  else
    echo "✗ $file is missing"
    all_files_exist=false
  fi
done

if $all_files_exist; then
  echo "All Gradle and build configuration files are present and ready for Codemagic!"
else
  echo "Some required files are missing. Please check the list above."
fi
