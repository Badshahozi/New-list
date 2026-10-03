

import java.util.EnumMap;
import java.util.Map;

// =====================================================================
// BEFORE: The growing switch statement inside NotifierFactory
// =====================================================================
/*
class NotifierFactoryOriginal {
    static Notifier create(ContactChannel channel) {
        switch (channel) {
            case TELEGRAM: return new TelegramNotifier();
            case SMS:      return new SmsNotifier();
            case EMAIL:
            default:       return new EmailNotifier();
        }
    }
}
*/

// =====================================================================
// AFTER: Refactored toward Strategy / Factory
// =====================================================================

enum ContactChannel { EMAIL, TELEGRAM, SMS, WHATSAPP }

interface Notifier {
    void send(String recipient, String message);
}

// 1. Separate variant classes behind shared Notifier interface
class EmailNotifier implements Notifier {
    public void send(String recipient, String message) {
        System.out.println("   [E-mail -> " + recipient + "] " + message);
    }
}

class TelegramNotifier implements Notifier {
    public void send(String recipient, String message) {
        System.out.println("   [Telegram -> " + recipient + "] " + message);
    }
}

class SmsNotifier implements Notifier {
    public void send(String recipient, String message) {
        System.out.println("   [SMS -> " + recipient + "] " + message);
    }
}

// NEW type added without changing any existing notifier classes
class WhatsAppNotifier implements Notifier {
    public void send(String recipient, String message) {
        System.out.println("   [WhatsApp -> " + recipient + "] " + message);
    }
}

// 2. Strategy interface for instantiation
interface NotifierStrategy {
    ContactChannel getChannel();
    Notifier createNotifier();
}

class EmailStrategy implements NotifierStrategy {
    public ContactChannel getChannel() { return ContactChannel.EMAIL; }
    public Notifier createNotifier() { return new EmailNotifier(); }
}

class TelegramStrategy implements NotifierStrategy {
    public ContactChannel getChannel() { return ContactChannel.TELEGRAM; }
    public Notifier createNotifier() { return new TelegramNotifier(); }
}

class SmsStrategy implements NotifierStrategy {
    public ContactChannel getChannel() { return ContactChannel.SMS; }
    public Notifier createNotifier() { return new SmsNotifier(); }
}

// NEW strategy class added independently
class WhatsAppStrategy implements NotifierStrategy {
    public ContactChannel getChannel() { return ContactChannel.WHATSAPP; }
    public Notifier createNotifier() { return new WhatsAppNotifier(); }
}

// 3. Dynamic Factory replacing the switch statement
class NotifierFactory {
    private static final Map<ContactChannel, NotifierStrategy> strategies = new EnumMap<>(ContactChannel.class);

    static {
        register(new EmailStrategy());
        register(new TelegramStrategy());
        register(new SmsStrategy());
    }

    public static void register(NotifierStrategy strategy) {
        strategies.put(strategy.getChannel(), strategy);
    }

    public static Notifier create(ContactChannel channel) {
        NotifierStrategy strategy = strategies.get(channel);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy registered for: " + channel);
        }
        return strategy.createNotifier();
    }
}

// =====================================================================
// DEMO: Proving new types require no changes to existing code
// =====================================================================
public class Main {
    public static void main(String[] args) {
        System.out.println("=== 1. Existing Types (Factory / Strategy) ===");
        Notifier email = NotifierFactory.create(ContactChannel.EMAIL);
        email.send("aigerim@mail.kz", "Welcome to the Book Club!");

        Notifier telegram = NotifierFactory.create(ContactChannel.TELEGRAM);
        telegram.send("@aigerim_reads", "You matched with Farhad!");

        System.out.println("\n=== 2. Adding NEW Type (WhatsApp) With ZERO Changes To Existing Code ===");
        // Registering WhatsAppStrategy dynamically at runtime
        NotifierFactory.register(new WhatsAppStrategy());

        Notifier whatsapp = NotifierFactory.create(ContactChannel.WHATSAPP);
        whatsapp.send("+7 707 232 38 30", "WhatsApp notification delivered successfully!");
    }
}