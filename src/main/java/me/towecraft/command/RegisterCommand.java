package me.towecraft.command;

import me.towecraft.TAS;
import me.towecraft.utils.FileMessages;
import me.towecraft.utils.HashUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Component;
import unsave.plugin.context.annotations.PostConstruct;

@Component
public class RegisterCommand implements CommandExecutor {

    @Autowire
    private TAS plugin;

    @Autowire
    private FileMessages fileMessages;

    private int minPassLength;
    private int maxPassLength;

    @PostConstruct
    public void init() {
        plugin.getCommand("r").setExecutor(this);
        plugin.getCommand("reg").setExecutor(this);
        minPassLength = plugin.getConfig().getInt("Password.minLength", 6);
        maxPassLength = plugin.getConfig().getInt("Password.maxLength", 16);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (sender instanceof Player) {



                    MySQL.isPlayerDB((Player) sender, new CallbackSQL<Boolean>() {

                        @Override
                        public void done(final Boolean data) {
                            if (data) {
                                sender.sendMessage(plugin.getPrefix() + fileMessages.getMSG().getString("Commands.register.exist"));
                            } else {
                                if (args.length < 2) {
                                    sender.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes( '&' ,
                                            fileMessages.getMSG().getString("Commands.register.wrong")));
                                    return;
                                }
                                if (!args[0].equals(args[1])) {
                                    sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.register.wrong"));
                                    return;
                                }

                                if (checkRusSymbol(args[1]) || checkContainsRusSymbol(args[1])) {
                                    sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.register.cyrillic_pass"));
                                    return;
                                }

                                boolean b2 = true;
                                for (String s : TAS.files.getMSG().getStringList("BannedPasswords")) {
                                    if (args[0].equals(s)) {
                                        sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.register.banned"));
                                        b2 = false;
                                        break;
                                    }
                                }

                                if (args[0].length() < minPassLength) {
                                    sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.register.to_short"));
                                    b2 = false;
                                }

                                if (args[0].length() > maxPassLength) {
                                    sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.register.to_long"));
                                    b2 = false;
                                }

                                String email = null;

                                if (args.length == 3) {
                                    if (!checkEmail(args[2])) {
                                        sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.register.to_email"));
                                        b2 = false;
                                    } else
                                        email = args[2];
                                }

                                if (!b2)
                                    return;

                                sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.register.registering"));
                                MySQL.PlayerSQL((Player) sender, 0, email);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        MySQL.setPlayerData((Player) sender, "password", HashUtil.HashPassword((Player) sender, args[0]), null);
                                        sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.register.success"));
                                        MySQL.getPlayer((Player) sender, new CallbackSQL<Boolean>() {
                                            @Override
                                            public void done(final Boolean isGetPlayer) {
                                                if (isGetPlayer) {
                                                    TAS.connect((Player) sender, "Hub_min");
                                                    MySQL.updatePlayer(((Player) sender).getPlayer());
                                                }
                                            }

                                            @Override
                                            public void error(final Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        });
                                    }
                                }.runTaskAsynchronously(TAS.plugin);
                            }
                        }

                        @Override
                        public void error(final Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }
}
