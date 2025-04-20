package rsa.user;

import rsa.RideSharingAppException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Users implements Serializable {
    private static final long serialVersionUID = 1L;

    // 🔒 Singleton e ficheiro de
    private static Users instance;
    private static File usersFile;

    // Mapa de utilizadores registados (nick → User)
    private Map<String, User> users = new HashMap<>();

    // Construtor privado (padrão Singleton)
    private Users() {}

    // 🔧 Define o ficheiro para guardar os dados
    public static void setUsersFile(File file) {
        usersFile = file;
    }

    public static File getUsersFile() {
        return usersFile;
    }


    // 🔁 Acede à instância única (Singleton)
    public static Users getInstance() throws RideSharingAppException {
        if (instance == null)
            instance = load();
        return instance;
    }

    // ✍️ Regista um novo utilizador
    public User register(String nick, String name) throws RideSharingAppException {
        if (users.containsKey(nick))
            throw new RideSharingAppException("Nickname já registado: " + nick);
        User user = new User(nick, name);
        users.put(nick, user);
        save();
        return user;
    }

    // 📥 Devolve o utilizador com esse nick (ou null)
    public User getUser(String nick) {
        return users.get(nick);
    }

    // 📦 Obtém um utilizador ou cria-o, se não existir
    public User getOrCreateUser(String nick, String name) throws RideSharingAppException {
        User user = getUser(nick);
        return user != null ? user : register(nick, name);
    }

    // 🔐 Verifica a autenticação
    public boolean authenticate(String nick, String key) {
        User user = getUser(nick);
        return user != null && user.authenticate(key);
    }

    // 🔄 Apaga todos os dados (para testes)
    public void reset() {
        users.clear();
        if (usersFile != null && usersFile.exists())
            usersFile.delete();
        instance = null;
    }

    // 📤 Guarda os dados no ficheiro
    private void save() throws RideSharingAppException {
        if (usersFile == null) return;
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(usersFile))) {
            out.writeObject(this);
        } catch (IOException e) {
            throw new RideSharingAppException("Erro ao guardar utilizadores", e);
        }
    }

    // 📥 Carrega os dados do ficheiro (ou cria novo)
    private static Users load() throws RideSharingAppException {
        if (usersFile != null && usersFile.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(usersFile))) {
                return (Users) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RideSharingAppException("Erro ao carregar utilizadores", e);
            }
        }
        return new Users();
    }
}
