package me.towecraft.utils.cmd;

import me.towecraft.TAS;
import me.towecraft.utils.callbacks.CallbackSQL;
import me.towecraft.utils.mysql.MySQL;
import me.towecraft.utils.password.HashMethod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;

public class LoginCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (sender instanceof Player) {
                    MySQL.isPlayerDB((Player) sender, new CallbackSQL<Boolean>() {
                        @Override
                        public void done(final Boolean isPlayerDB) {
                            if (isPlayerDB)
                                MySQL.getPlayerData((Player) sender, "valid", new CallbackSQL<String>() {
                                    @Override
                                    public void done(final String data) {
                                        if (data.equals("1")) {
                                            sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.login.already"));
                                        }
                                        if (args.length == 1) {
                                            HashMethod.MashMatch((Player) sender, args[0], new CallbackSQL<Boolean>() {
                                                @Override
                                                public void done(final Boolean correctPass) {
                                                    if (correctPass) {
                                                        sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.login.correct"));
                                                        TAS.plugin.getLoginTimer().getTimers().remove(sender.getName());
                                                        MySQL.setPlayerData((Player) sender, "valid", "1", null);
                                                        MySQL.setPlayerData((Player) sender, "last_login", new Timestamp(System.currentTimeMillis()).toString(), null);
                                                        MySQL.setPlayerData((Player) sender, "log_ip", ((Player) sender).getAddress().getAddress().getHostAddress(), null);
                                                        TAS.connect((Player) sender, "Hub_min");
                                                        MySQL.getPlayer((Player) sender, null);
                                                        MySQL.updatePlayer(((Player) sender).getPlayer());
                                                    } else
                                                        sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.login.wrong_pass"));
                                                }

                                                @Override
                                                public void error(final Exception ex) {
                                                }
                                            });
                                        } else
                                            sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.login.wrong_cmd"));
                                    }

                                    @Override
                                    public void error(final Exception ex) {
                                    }
                                });
                            else
                                sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.register.wrong"));
                        }

                        @Override
                        public void error(final Exception ex) {
                        }
                    });
                }
            }
        }.runTaskAsynchronously(TAS.plugin);

        return true;
    }
}
