# FakePluginsList

一个简单的BungeeCord插件，用于显示自定义的插件列表。

## 功能

- 拦截 `/plugins` 命令（可自定义）
- 显示自定义的插件列表
- 区分显示成功加载的插件（绿色）和加载失败的插件（红色）
- 可选择是否显示加载失败的插件列表
- 显示成功和失败插件的数量统计
- 可自定义显示格式和插件列表
- 支持颜色代码

## 配置

```yaml
# 触发命令列表 (不需要加/)
commands:
  - "plugins"
  - "pl"
  - "bukkit:plugins"
  - "bukkit:pl"
  # 可以添加更多自定义命令

# 是否显示加载失败的插件 (true: 显示, false: 不显示)
show_failed_plugins: true

# 显示消息 (%count% 会被替换为插件总数量, %plugins% 会被替换为插件列表)
# %success_count% 会被替换为成功加载的插件数量
# %failed_count% 会被替换为加载失败的插件数量
message: "&fPlugins (&a%success_count%&f/&c%failed_count%&f): %plugins%"

# 成功加载的插件列表 (显示为绿色 &a)
success_plugins:
  - "Essentials"
  - "WorldEdit"
  - "WorldGuard"
  # 添加更多成功加载的插件...

# 加载失败的插件列表 (显示为红色 &c)
failed_plugins:
  - "FailedPlugin1"
  - "BrokenPlugin"
  # 添加更多加载失败的插件...
```

## 命令

- `/plugins`, `/pl`, `/bukkit:plugins`, `/bukkit:pl` - 显示自定义的插件列表（默认命令，可在配置中添加或修改）
- `/plugins reload` - 重新加载配置文件（需要 `fakepluginslist.reload` 权限）

你可以在配置文件中添加任意数量的自定义命令，所有命令都会显示相同的插件列表。

## 权限

- `fakepluginslist.reload` - 允许重新加载插件配置

## 安装

1. 下载插件的 JAR 文件
2. 将 JAR 文件放入 BungeeCord 服务器的 `plugins` 文件夹中
3. 重启服务器或使用插件管理器加载插件
4. 配置文件将在首次启动时自动生成

## 编译

使用 Maven 编译插件：

```bash
mvn clean package
```

编译后的 JAR 文件将位于 `target` 文件夹中。


# FakePluginsList

A simple BungeeCord plugin for displaying custom plugin lists with configurable output.

## Features ✨

- Intercepts `/plugins` and related commands
- Fully customizable plugin list display
- Color-coded output (success/failed plugins)
- Toggleable failed plugins display
- Plugin count statistics
- Simple YAML configuration
- Permission-based reload command

## Configuration ⚙️

`config.yml` example:
```yaml
# Command triggers (without slash)
commands:
  - "plugins"
  - "pl"
  - "bukkit:plugins"
  - "bukkit:pl"

# Toggle failed plugins visibility
show_failed_plugins: true

# Display message formatting
# Available placeholders:
# %count% - Total plugins
# %success_count% - Successful plugins
# %failed_count% - Failed plugins  
# %plugins% - Plugin list
message: "&fPlugins (&a%success_count%&f/&c%failed_count%&f): %plugins%"

# Plugin lists
success_plugins:
  - "Essentials"
  - "WorldEdit"
  - "WorldGuard"

failed_plugins:
  - "BrokenPlugin"
  - "OutdatedAddon"
