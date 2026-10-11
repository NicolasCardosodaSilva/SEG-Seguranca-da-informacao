package ads.seg;

public class App {
    UserService userService = new UserService(new InMemory());

    public App() {}

    void menu (int opcao) throws InvalidLoginException {
        switch (opcao) {
            case 1 -> {
                String hash = IO.readln("Escolha o seu algorítimo(Bcrypt, PBKDF2):");
                userService.setAlgHash(hash);
                String login = IO.readln("Digite o login do seu usuário:");
                String password = IO.readln("Digite a sua senha:");
                try {
                    userService.register(login,password);
                    System.out.println("Usuário cadastrado com sucesso!");
                } catch (UserAlreadyExistsException e) {
                    System.out.println("Usuário ja existente");
                }

            }
            case 2 -> {
                boolean sucesso = false;
                String login = IO.readln("Entre com o seu login:");

                while (!sucesso) {
                    String senhaAtual = IO.readln("Digite a senha atual:");
                    String novaSenha = IO.readln("Digite a nova senha:");

                    try {
                        boolean updated = userService.updatePassword(login, senhaAtual, novaSenha);
                        if (updated) {
                            sucesso = true;
                            System.out.println("Usuário atualizado com sucesso!");
                        }
                    } catch (UserNotFoundException | InvalidLoginException e) {
                        System.out.println(" Login não encontrado ou inválido.");
                        login = IO.readln("Entre com o login novamente: ");
                    } catch (InvalidPasswordException e) {
                        System.out.println("Senha atual incorreta. Tente novamente.");
                    }
                }

            }

            case 3 ->{
                String login = IO.readln("Entre com o seu login:");
                String password = IO.readln("Digite a sua senha:");

                try {
                    if( userService.authenticate(login,password)){;
                      System.out.println("Usuário autenticado!");
                    }
                } catch (InvalidPasswordException e) {
                    System.out.println("Senha incorreta.");
                } catch (InvalidLoginException e) {
                    System.out.println("Login inválido.");
                }

            }


        }
    }


    static void main(String[] args) throws InvalidLoginException {

        App app = new App();

        int opcao;
        do {
            System.out.println("-----MENU-----\n" +
                    "1 - Cadastrar usuário\n" +
                    "2 - Alterar senha do usuário \n" +
                    "3 - Autenticar usuário \n" +
                    "4 - Sair"
            );
            opcao = Integer.parseInt(IO.readln("Selecione uma opção:  \n"));
            app.menu(opcao);
        } while (opcao != 4);

    }
}
