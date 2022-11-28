# Intellij run (http service)
Run actions via the built-in rest server

Example: `curl http://localhost:63343/api/run?command=CompileDirty`

## Use
run gradlew buildPlugin, install the zip created in `./build/distributions` in intellij via `plugins -> install plugin from disk`

The built-in server's port can be changed in: `Preferences | Build, Execution, Deployment | Debugger`