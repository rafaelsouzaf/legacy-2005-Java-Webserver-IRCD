package chatmania.ircd;

import chatmania.User;

// essa interface existe para tipar os comandos
// disponiveis aos usuarios
// Todas classes de comandos acessiveis pelo IRC
// deve obrigatoriamente implementar essa interface

public interface CommandsI {

    // metodo execute
    void execute(User tmpUser, String cmd);

}