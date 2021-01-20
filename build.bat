call "C:\Program Files (x86)\Microsoft Visual Studio\2017\BuildTools\VC\Auxiliary\Build\vcvars64.bat"

%HOME%/.sdkman/candidates/java/current/bin/native-image.cmd ^
--verbose ^
--static ^
-J-Djava.util.logging.manager=org.jboss.logmanager.LogManager ^
-J-Duser.language=en ^
-J-Dfile.encoding=UTF-8 ^
-J-Xmx6g ^
--report-unsupported-elements-at-runtime ^
--allow-incomplete-classpath ^
-H:IncludeResources=.*yaml\$ ^
-H:Log=registerResource: ^
--initialize-at-build-time= ^
-H:+JNI ^
-jar build\kattlo-0.1.0-runner.jar ^
-H:FallbackThreshold=0 ^
-H:+ReportExceptionStackTraces ^
-H:-AddAllCharsets ^
-H:EnableURLProtocols=http ^
--no-server ^
-H:-UseServiceLoaderFeature ^
-H:+StackTrace kattlo
