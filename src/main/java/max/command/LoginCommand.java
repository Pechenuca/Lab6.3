package max.command;


import max.database.Credentials;

import java.io.IOException;

import static max.command.ICommand.TYPE_INPUT_CREDENTIAL;

public class LoginCommand extends Command {

    protected Credentials credentials = null;

    public LoginCommand() {
        commandKey = "login";
        description = "Login into the system to manage your own dragons.\nSyntax: login {credentials}";
    }

    @Override
    public void addInput(Object credentials) {
        this.credentials = (Credentials) credentials;
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) throws IOException {
        return context.DBRequestManager().login(this.credentials, context.resourcesBundle());
    }

    @Override
    public int requireInput() {
        return TYPE_INPUT_CREDENTIAL;
    }

    @Override
    public Object getInput() {
        return credentials;
    }
}
