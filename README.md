# OpenResty Lua Support

This plugin adds auto-completion, function argument handlers, and documentation support to files using the [Lua plugin](https://plugins.jetbrains.com/plugin/5055?pr=).

Once you have the Lua plugin and this plugin installed, you should start seeing auto-completion to any call starting with `ngx`.

Currently everything is pulled from the [lua-nginx-module documentation](https://github.com/openresty/lua-nginx-module), so there may some things missing especially all of the `resty` plugins.

## Development

To regenerate all of the keywords and documentation, run `./gradlew generateDocumentation`. This will pull the lua-nginx-module into the `vendor` directory then run some gradle tasks to extract the information.
