package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.logs.PunishLogs;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

public class MuteCommand extends CommandModule {

    private final DefinedQuery<Role> MUTED_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Muted");
        }
    };
    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public MuteCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "mute";
    }

    @Override
    public String getDescription() {
        return "Mute/unmute a member from this guild.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.USER, "member", "The member to mute.", true)
        };
    }

    @Override
    public int getCooldown() {
        return 2;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        Member member = e.getOption("member").getAsMember();

        if (member.getRoles().contains(STAFF_ROLE.query().first())) {
            e.replyEmbeds(
                    new TechEmbedBuilder("Mute - Error")
                            .error()
                            .text("You cannot mute this user")
                            .build()
            ).queue();

        } else if (member == e.getMember()) {
            e.replyEmbeds(
                    new TechEmbedBuilder("Mute - Error")
                            .error()
                            .text("You cannot mute yourself")
                            .build()
            ).queue();

        } else if(memberHasMutedRole(member)) {
            e.getGuild().removeRoleFromMember(member, MUTED_ROLE.query().first()).queue();
            e.reply(member.getAsMention() + " is no longer muted!").queue();
            PunishLogs.log(member.getAsMention() + " is no longer muted!");
        } else {
            e.getGuild().addRoleToMember(member, MUTED_ROLE.query().first()).queue();
            e.reply(member.getAsMention() + " is now muted!").queue();
            PunishLogs.log(member.getAsMention() + " is now muted!");
        }
    }

    public boolean memberHasMutedRole(Member member) {
        return member.getRoles().contains(MUTED_ROLE.query().first());
    }
}
