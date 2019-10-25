@echo off

Rem set GRAALVM_HOME=C:\Users\IEUser\Downloads\graalvm\graalvm-ce-19.2.1
Rem set PATH=%PATH%;C:\Users\IEUser\bin
Rem set BOOTLEG_VERSION: 0.1.5-SNAPSHOT

if "%GRAALVM_HOME%"=="" (
    echo Please set GRAALVM_HOME
    exit /b
)
set JAVA_HOME=%GRAALVM_HOME%\bin
set PATH=%PATH%;%GRAALVM_HOME%\bin

echo Building bootleg %BOOTLEG_VERSION%

copy %GRAALVM_HOME%\jre\bin\sunec.dll resources\sunec.dll

call lein do clean, uberjar
if %errorlevel% neq 0 exit /b %errorlevel%

Rem the --no-server option is not supported in GraalVM Windows.
call %GRAALVM_HOME%\bin\native-image.cmd ^
  -jar target/uberjar/bootleg-%BOOTLEG_VERSION%-standalone.jar ^
  -H:Name=bootleg ^
  -H:+ReportExceptionStackTraces ^
  -J-Dclojure.spec.skip-macros=true ^
  -J-Dclojure.compiler.direct-linking=true ^
  -H:ConfigurationFileDirectories=graal-configs/ ^
  --initialize-at-build-time ^
  -H:Log=registerResource: ^
  -H:EnableURLProtocols=http,https ^
  --verbose ^
  --allow-incomplete-classpath ^
  --no-fallback ^
  "-J-Xmx4g" ^
  -H:+TraceClassInitialization -H:+PrintClassInitialization
if %errorlevel% neq 0 exit /b %errorlevel%

echo Creating zip archive
jar -cMf bootleg-%BOOTLEG_VERSION%-windows-amd64.zip bootleg.exe
