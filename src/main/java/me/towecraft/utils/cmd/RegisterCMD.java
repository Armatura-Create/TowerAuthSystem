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

import java.util.regex.Pattern;

public class RegisterCMD implements CommandExecutor {

    private final int maxPass = 16;
    private final int minPass = 6;

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
                                sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.register.exist"));
                            } else {
                                    if (args.length < 2) {
                                    sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.register.wrong"));
                                    return;
                                }
                                if (!args[0].equals(args[1])) {
                                    sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.register.wrong"));
                                    return;
                                }

                                if (checkRusSymbol(args[1]) || checkContainsRusSymbol(args[1])){
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
                                if (args[0].length() < RegisterCMD.this.minPass) {
                                    sender.sendMessage(TAS.getPrefix() + TAS.files.getMSG().getString("Commands.register.to_short"));
                                    b2 = false;
                                }
                                if (args[0].length() > RegisterCMD.this.maxPass) {
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
                                        MySQL.setPlayerData((Player) sender, "password", HashMethod.HashPassword((Player) sender, args[0]), null);
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
                                            }
                                        });
                                    }
                                }.runTaskAsynchronously(TAS.plugin);
                            }
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

    public static boolean checkEmail(String email) {
        return Pattern.compile("(?:[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[A-Za-z0-9-]*[A-Za-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])").matcher(email).matches();
    }

    public static boolean checkRusSymbol(String pass) {
        return Pattern.compile("[а-яА-Я]+").matcher(pass).matches();
    }

    public static boolean checkContainsRusSymbol(String pass) {
        return Pattern.compile("(?=[а-яА-ЯёЁ]*[a-zA-Z])(?=[a-zA-Z]*[а-яА-ЯёЁ])[\\wа-яА-ЯёЁ]+").matcher(pass).matches();
    }
}
