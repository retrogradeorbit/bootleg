@echo off

Rem set GRAALVM_HOME=C:\Users\IEUser\Downloads\graalvm\graalvm-ce-java11-20.1.0
Rem set PATH=%PATH%;C:\Users\IEUser\bin

if "%GRAALVM_HOME%"=="" (
    echo Please set GRAALVM_HOME
    exit /b
)
set JAVA_HOME=%GRAALVM_HOME%\bin
set PATH=%GRAALVM_HOME%\bin;%PATH%
set /P BOOTLEG_VERSION=< .meta\VERSION

echo Building bootleg %BOOTLEG_VERSION%

copy %GRAALVM_HOME%\jre\bin\sunec.dll resources\sunec.dll

call lein do clean, uberjar
if %errorlevel% neq 0 exit /b %errorlevel%

call %GRAALVM_HOME%\bin\gu install native-image

Rem the --no-server option is not supported in GraalVM Windows.
call %GRAALVM_HOME%\bin\native-image.cmd ^
  "-jar" "target/uberjar/bootleg-%BOOTLEG_VERSION%-standalone.jar" ^
  "-H:Name=bootleg" ^
  "-H:+ReportExceptionStackTraces" ^
  "-J-Dclojure.spec.skip-macros=true" ^
  "-J-Dclojure.compiler.direct-linking=true" ^
  "-H:ConfigurationFileDirectories=graal-configs/" ^
  "--initialize-at-build-time" ^
  "-H:Log=registerResource:" ^
  "-H:EnableURLProtocols=http" ^
  "--verbose" ^
  "--allow-incomplete-classpath" ^
  "--no-fallback" ^
  "-J-Xmx5g"

if %errorlevel% neq 0 exit /b %errorlevel%

echo Creating zip archive
jar -cMf bootleg-%BOOTLEG_VERSION%-windows-amd64.zip bootleg.exe
