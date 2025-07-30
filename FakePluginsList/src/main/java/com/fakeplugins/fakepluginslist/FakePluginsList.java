package com.fakeplugins.fakepluginslist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class FakePluginsList extends Plugin {

    private Configuration config;
    private List<String> successPluginsList;
    private List<String> failedPluginsList;
    private List<String> commands;
    private String message;
    private boolean showFailedPlugins;

    @Override
    public void onEnable() {
        // 创建配置文件
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 注册所有命令
        for (String cmd : commands) {
            getProxy().getPluginManager().registerCommand(this, new PluginsCommand(this, cmd));
            getLogger().info("已注册命令: /" + cmd);
        }
        
        getLogger().info("FakePluginsList 已启用!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FakePluginsList 已禁用!");
    }

    private void loadConfig() {
        commands = config.getStringList("commands");
        if (commands.isEmpty()) {
            commands.add("plugins"); // 默认命令
        }
        message = config.getString("message", "&fPlugins (%count%): %plugins%");
        successPluginsList = config.getStringList("success_plugins");
        failedPluginsList = config.getStringList("failed_plugins");
        showFailedPlugins = config.getBoolean("show_failed_plugins", true); // 默认显示失败插件
    }

    public void reloadConfig() {
        try {
            // 保存旧的命令列表用于后续取消注册
            List<String> oldCommands = new java.util.ArrayList<>(commands);
            
            // 加载新配置
            config = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(getDataFolder(), "config.yml"));
            loadConfig();
            
            // 取消注册旧命令
            // 在BungeeCord中，需要遍历所有命令并找到匹配的命令对象
            for (String cmdName : oldCommands) {
                // 获取所有已注册的命令
                for (java.util.Map.Entry<String, Command> entry : getProxy().getPluginManager().getCommands()) {
                    Command cmd = entry.getValue();
                    // 如果命令名称匹配，则取消注册
                    if (cmd.getName().equalsIgnoreCase(cmdName)) {
                        getProxy().getPluginManager().unregisterCommand(cmd);
                        break;
                    }
                }
            }
            
            // 注册新命令
            for (String cmd : commands) {
                getProxy().getPluginManager().registerCommand(this, new PluginsCommand(this, cmd));
                getLogger().info("已重新注册命令: /" + cmd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class PluginsCommand extends Command {

        private final FakePluginsList plugin;

        public PluginsCommand(FakePluginsList plugin, String commandName) {
            super(commandName);
            this.plugin = plugin;
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("fakepluginslist.reload")) {
                plugin.reloadConfig();
                sender.sendMessage(new TextComponent(ChatColor.GREEN + "FakePluginsList 配置已重新加载!"));
                return;
            }

            StringBuilder pluginsString = new StringBuilder();
            
            // 添加成功加载的插件（绿色）
            for (int i = 0; i < successPluginsList.size(); i++) {
                pluginsString.append(ChatColor.GREEN).append(successPluginsList.get(i));
                // 如果不是最后一个插件，并且还有失败的插件且需要显示，添加逗号
                if (i < successPluginsList.size() - 1 || (showFailedPlugins && !failedPluginsList.isEmpty())) {
                    pluginsString.append(ChatColor.WHITE).append(", ");
                }
            }
            
            // 如果配置为显示失败插件，则添加失败的插件（红色）
            if (showFailedPlugins) {
                for (int i = 0; i < failedPluginsList.size(); i++) {
                    pluginsString.append(ChatColor.RED).append(failedPluginsList.get(i));
                    if (i < failedPluginsList.size() - 1) {
                        pluginsString.append(ChatColor.WHITE).append(", ");
                    }
                }
            }

            // 计算总插件数量（根据是否显示失败插件决定是否计入失败插件数量）
            int totalPlugins = successPluginsList.size() + (showFailedPlugins ? failedPluginsList.size() : 0);
            
            String response = message
                    .replace("%count%", String.valueOf(totalPlugins))
                    .replace("%success_count%", String.valueOf(successPluginsList.size()))
                    .replace("%failed_count%", showFailedPlugins ? String.valueOf(failedPluginsList.size()) : "0")
                    .replace("%plugins%", pluginsString.toString());

            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', response)));
        }
    }
}