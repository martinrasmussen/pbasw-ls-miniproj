package rmi.server;




import rmi.ExchangeRateRMI;
import rmi.server.api.remote.CurrencyNameProvider;
import rmi.server.api.remote.FinanceAPI;
import rmi.server.api.remote.OERNameProvider;
import rmi.server.api.remote.YahooFinanceAPI;
import rmi.server.cache.CacheImpl;
import rmi.server.cache.LeastRecentlyUsedPolicy;
import rmi.server.cache.ReplacementPolicy;
import rmi.server.currency.Currencies;
import rmi.server.currency.CurrencyDataSource;
import rmi.server.currency.ForexCurrency;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ExchangeRateServer implements ExchangeRateRMI {

    private CacheImpl<String, ForexCurrency> cache;
    private PropertiesConfigurationManager configManager;
    private static final String CONFIG_PATH = "server.properties";

    public ExchangeRateServer() {
        configManager = new PropertiesConfigurationManager(CONFIG_PATH);

        long timeToLive = configManager.getCacheTTL();
        FinanceAPI financeAPI = new YahooFinanceAPI();
        CurrencyNameProvider nameProvider = new OERNameProvider();
        CurrencyDataSource dataSource = new CurrencyDataSource(financeAPI, nameProvider);
        ReplacementPolicy<String> replacementPolicy = new LeastRecentlyUsedPolicy<>();

        cache = new CacheImpl<>(timeToLive, -1, dataSource, replacementPolicy);
    }

    public static void main(String[] varargs) {
        ExchangeRateServer serverInstance = new ExchangeRateServer();
        // RMI Server functionality
        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            // Bind the remote object's stub in the registry
            ExchangeRateRMI stub = (ExchangeRateRMI) UnicastRemoteObject.exportObject(serverInstance, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("ExchangeRateRMI", stub);
            Scanner input = new Scanner(System.in);
            serverInstance.displayWelcomeMessage();
            serverInstance.displayHelpMessage();
            while (true) {
                System.out.print("> ");
                String s = input.nextLine();
                serverInstance.handleInput(s);
            }
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private void handleInput(String input) {
        input = input.trim();
        int splitIndex = input.indexOf(" ");
        String commandName = splitIndex == -1 ? input : input.substring(0, splitIndex);
        if (!commands.containsKey(commandName)) {
            System.out.println("Invalid command!");
            commandName = "help";
        }
        ConsoleCommand cmd = commands.get(commandName);
        String params = input.substring(commandName.length()).trim();
        if (params.isEmpty()) cmd.execute();
        else cmd.execute(params);
    }

    private void displayWelcomeMessage() {
        System.out.println("Server ready.");
    }

    private void displayHelpMessage() {
        System.out.println("  Available commands: ");
        for (String key : commands.keySet()) {
            ConsoleCommand cmd = commands.get(key);
            String commandNameAndParameters = String.format("%s %s", key, cmd.parameterDescription);
            System.out.printf("    %-26s %s\n", commandNameAndParameters, cmd.commandDescription);
        }
    }

    @Override
    public double convert(String sourceCurrencySymbol, String targetCurrencySymbol, double amount) throws RemoteException {
        // TODO: Serverside validation
        ForexCurrency source = cache.get(sourceCurrencySymbol);
        ForexCurrency target = cache.get(targetCurrencySymbol);
        return Currencies.convert(amount, source, target);
    }

    @Override
    public String[] getSupportedCurrencies() {
        Set<String> currencySymbols = cache.keySet();
        Set<String> set = new TreeSet<>(currencySymbols);
        String[] excluded = configManager.getExcludedCurrencies();
        set.removeAll(Arrays.asList(excluded));
        return set.toArray(new String[set.size()]);
    }

    private abstract class ConsoleCommand {
        private String commandDescription;
        private String parameterDescription;

        public ConsoleCommand(String commandDescription, String parameterDescription) {
            this.commandDescription = commandDescription;
            this.parameterDescription = parameterDescription == null ? "" : parameterDescription;
        }

        protected abstract void execute(Object... varargs);
    }

    private void showCurrencyList(String header, int columns, String... currencies) {
        StringBuilder sb = new StringBuilder(header);
        for (int i = 0; i < currencies.length; i++) {
            String currency = currencies[i];
            if (i % columns == 0) sb.append("\n");
            sb.append(String.format("  %s,", currency));
        }
        System.out.println(sb.toString().substring(0, sb.length() - 1));
    }

    // A map of possible server commands. LinkedHashMap is used to retain the ordering of the inserted items.
    private final HashMap<String, ConsoleCommand> commands = new LinkedHashMap<String, ConsoleCommand>() {{
        // HELP---------------------------------------
        put("help", new ConsoleCommand("Display this message", null) {
            @Override
            protected void execute(Object... varargs) {
                displayHelpMessage();
            }
        });
        // EXCLUDE------------------------------------
        put("exclude", new ConsoleCommand("Adds currencies to the list of excluded currencies", "curr1[,curr2,...]") {
            @Override
            protected void execute(Object... varargs) {
                if (varargs.length == 0) {
                    String[] excludedCurrencies = configManager.getExcludedCurrencies();
                    showCurrencyList("Currently excluded currencies:", 3, excludedCurrencies);
                    return;
                }

                String[] currencies = ((String) varargs[0]).split(",");
                for (int i = 0; i < currencies.length; i++) {
                    String currency = currencies[i].trim();
                    if (currency.length() != 3) {
                        System.out.printf("  WARNING: Currency symbols should be three letters long! (%s)\n", currency);
                    }
                    currencies[i] = currency.toUpperCase();
                }
                configManager.excludeCurrencies(currencies);
            }
        });
        // INCLUDE------------------------------------
        put("include", new ConsoleCommand("Removes currencies from the list of excluded currencies", "curr1[,curr2,...]") {
            @Override
            protected void execute(Object... varargs) {
                if (varargs.length == 0) {
                    String[] includedCurrencies = getSupportedCurrencies();
                    showCurrencyList("Currently included currencies:", 3, includedCurrencies);
                    return;
                }

                String[] currencies = ((String) varargs[0]).split(",");
                for (int i = 0; i < currencies.length; i++) {
                    String currency = currencies[i].trim();
                    if (currency.length() != 3) {
                        System.out.printf("  WARNING: Currency symbols should be three letters long! (%s)\n", currency);
                    }
                    currencies[i] = currency.toUpperCase();
                }
                configManager.includeCurrencies(currencies);
            }
        });
        // CACHE-CLEAR--------------------------------
        put("cache-clear", new ConsoleCommand("Clears the cache", null) {
            @Override
            protected void execute(Object... varargs) {
                cache.clear();
                System.out.println("Cache cleared!");
            }
        });
        // UPDATE-------------------------------------
        put("update", new ConsoleCommand("Sets the cache update rate to 'ms' milliseconds if provided.", "millis") {
            @Override
            protected void execute(Object... varargs) {
                if (varargs.length == 0) {
                    System.out.println("Current cache update rate is " + configManager.getCacheTTL());
                    return;
                }

                try {
                    long newUpdateRate = Long.parseLong(varargs[0].toString());
                    configManager.setUpdateRate(newUpdateRate);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid parameter. Usage example (to set update rate to 24 hours):\n  > update 86400000");
                }
            }
        });
        // RELOAD-------------------------------------
        put("reload", new ConsoleCommand("Reloads configuration file", null) {
            @Override
            protected void execute(Object... varargs) {
                // TODO: Possibly take path as a command parameter
                configManager.loadConfigFile(CONFIG_PATH);
                System.out.printf("%s successfully reloaded!\n", CONFIG_PATH);
            }
        });
        // SHUTDOWN-----------------------------------
        put("shutdown", new ConsoleCommand("Shut down the server", null) {
            @Override
            protected void execute(Object... varargs) {
                System.out.print("Are you sure you want to shut down the server? [y/N]\n> ");
                String input = new Scanner(System.in).nextLine();
                if (input.toLowerCase().equals("y")) System.exit(0);
                System.out.println("Shutdown aborted.");
            }
        });
    }};
}
