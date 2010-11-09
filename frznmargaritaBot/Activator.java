package frznmargaritaBot;

import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import com.buglabs.application.IServiceProvider;
import com.buglabs.application.RunnableWithServices;
import com.buglabs.application.ServiceTrackerHelper;
import com.buglabs.bug.base.pub.IBUGBaseControl;
import com.buglabs.bug.module.motion.pub.IMotionSubject;
// Added by wwward on Jul 6th, 2010
import com.buglabs.bug.module.vonhippel.pub.IVonHippelModuleControl;

public class Activator implements BundleActivator, RunnableWithServices {

	protected static final String IRC_CHANNEL = "#nycresistor";
	
	private static String [] services = { 
			IMotionSubject.class.getName(), 
			IBUGBaseControl.class.getName(),
			LogService.class.getName(),
			IVonHippelModuleControl.class.getName()
	};
	
	private ServiceTracker st;
	private IRCBot bot;
	
	public void start(BundleContext context) throws Exception {
		st = ServiceTrackerHelper.createAndOpen(context, services, this);
	}

	public void stop(BundleContext context) throws Exception {
		st.close();
	}

	public void allServicesAvailable(IServiceProvider serviceProvider) {
		IBUGBaseControl base = (IBUGBaseControl) serviceProvider.getService(IBUGBaseControl.class);
		bot = new IRCBot((IMotionSubject) serviceProvider.getService(IMotionSubject.class), base, (LogReaderService) serviceProvider.getService(LogService.class), (LogService) serviceProvider.getService(LogService.class), (IVonHippelModuleControl) serviceProvider.getService(IVonHippelModuleControl.class));
		
		try {
			bot.connect("irc.freenode.net");
			bot.joinChannel(IRC_CHANNEL);
		} catch (NickAlreadyInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IrcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void serviceUnavailable(IServiceProvider serviceProvider,
			ServiceReference sr, Object service) {
		if (bot != null) {
			bot.disconnect();
			bot.dispose();
		}
	}
}