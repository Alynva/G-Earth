package gearth.ui.logger.loggerdisplays;

import gearth.Main;
import gearth.extensions.InternalExtensionFormBuilder;
import gearth.misc.OSValidator;
import gearth.services.extension_handler.ExtensionHandler;
import gearth.services.extension_handler.extensions.extensionproducers.ExtensionProducer;
import gearth.services.extension_handler.extensions.extensionproducers.ExtensionProducerObserver;
import gearth.services.internal_extensions.uilogger.UiLogger;
import gearth.services.internal_extensions.uilogger.UiLoggerLauncher;

/**
 * Created by Jonas on 04/04/18.
 */
public class PacketLoggerFactory implements ExtensionProducer {

    private UiLogger uiLogger;

    public static boolean usesUIlogger() {
        return (!Main.hasFlag("-t"));
    }

    public PacketLoggerFactory(ExtensionHandler handler) {
        handler.addExtensionProducer(this);
    }


    public PacketLogger get() {
        if (usesUIlogger()) {
//            return new UiLogger(); //now an extension
            return uiLogger;
        }

        if (OSValidator.isUnix()) {
            return new LinuxTerminalLogger();
        }
        return new SimpleTerminalLogger();
    }

    @Override
    public void startProducing(ExtensionProducerObserver observer) {
        if (usesUIlogger()) {
            uiLogger = new InternalExtensionFormBuilder<UiLoggerLauncher, UiLogger>()
                    .launch(new UiLoggerLauncher(), observer);
        }
    }
}
