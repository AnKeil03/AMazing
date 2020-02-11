/* CommandHandler.java
    used for processing terminal commands typed by system admins
 */

package drms.server.command;
import drms.Main;


abstract class Command {
    String name;
    Command(String n) {
        name=n;
    }
    abstract void run(String[] args);
}

class Help extends Command {
    Help() {super("help");}
    void run(String[] args) {
        System.out.print("List of commands: ");
        for (Command c: CommandHandler.commands)
            System.out.print(c.name+" ");
        System.out.println();
    }
}

class Connections extends Command {
    Connections() {super("connections");}
    void run(String[] args) {
        Main.server.printConnectionsInfo();
    }
}





public class CommandHandler {

    public static Command[] commands;

    public static void initCommands() {
        commands = new Command[] {
                new Help(), new Connections()
        };
    }

    public static void processCommand(String com) {
        String[] arg = com.split(" ");
        for (Command c: commands)
            if (c.name.equals(arg[0]))
                c.run(arg);
    }

}

