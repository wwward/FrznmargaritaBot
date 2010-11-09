package frznmargaritaBot;

import java.io.IOException;
import java.util.Random;

import org.jibble.pircbot.PircBot;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

import com.buglabs.bug.base.pub.IBUGBaseControl;
import com.buglabs.bug.module.motion.pub.IMotionObserver;
import com.buglabs.bug.module.motion.pub.IMotionSubject;

//import com.buglabs.bug.module.vonhippel.VonHippelModuleControl;
import com.buglabs.bug.module.vonhippel.pub.IVonHippelModuleControl;


public class IRCBot extends PircBot implements IMotionObserver {
	
	private long lastDetection = 0;
	public static final String MY_NAME = "frznmargarita";

	private static final String[] UNKNOWN_MSGS = { "Huh?  Wha?",
			"Errrm, not with you there, sorry.",
			"Help works, why not try that genius.",
			"My parsing code is pretty simple, try duming it down.",
			"Don't get frustrated, I'm a 73k program." };

	private static final String[] THANKS_MSGS = { "you got it big boy.",
			"your welcome friend!", "not a big deal, I'm not going anywhere.",
			"yep, sure." };

	private static final String[] THANKS_KEYWORDS = { "thanks", "awesome",
			"thx" };

	private static final String[] FLASHED_MSGS = {
			"Consider yourself flashed.  I hope someone notices.",
			"Lights! Camera! Action!", "Blue lights ablaze!" };

	private static final String[] FLASH_KEYWORDS = { "alert", "flash", "notify" };
	
	private static final String[] HOT_KEYWORDS = { "hot", "temp" };

	private final IBUGBaseControl base;
	private long started;
	private final LogReaderService logReaderService;
	private final LogService logService;
	private final IVonHippelModuleControl vhService;

	public IRCBot(IMotionSubject service, IBUGBaseControl base,
			LogReaderService logReaderService, LogService logService, IVonHippelModuleControl vhService) {
		this.base = base;
		this.logReaderService = logReaderService;
		this.logService = logService;
		this.vhService = vhService;
		service.register(this);
		setName(MY_NAME);
		started = System.currentTimeMillis();
	}

	protected void onMessage(String channel, String sender, String login,
			String hostname, String message) {
		
		// START HERE
//		try {
//			vhService.writeADC(IVonHippelModuleControl.VH_ADC_W1_EN
//					| IVonHippelModuleControl.VH_ADC_W2_EN |IVonHippelModuleControl.VH_ADC_W1_CH0);
//			System.out.println("entered vh read branch");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		//System.out.println("Wrote "+((IVonHippelModuleControl.VH_ADC_W1_EN | IVonHippelModuleControl.VH_ADC_W1_CH0))+" in java, sleeping 1 sec before read");
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			Value=vhService.readADC();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		// Nick Vermeer's additions here:
//		Value=((Value & ~0x800000) >> 6);
//		fvalue=((250.0/65535.0)*(double)Value*(9.0/5.0)+32.0);
//		
//		System.out.println("Integer Output ::"+Double.toString(fvalue));
//		System.out.println("ADCTester.jar: ADC Channel 0 data = " + Integer.toHexString(Value)+" "+Integer.toBinaryString(Value));
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		//insert
//		sendMessage(channel, randomIndex(THANKS_MSGS));
//		sendMessage(channel, "By the way, it's " + Double.toString(fvalue) + "degrees Fahrenheit.");
		// END HERE
		
		blink(base, 1);
		System.out.println("this is just before the conditional test");
		if (message.startsWith(MY_NAME)) {
			sendMessage(channel, sender + ", " + getResponse(message));
			//blink(base, 1);
		} else if (stringContains(message, MY_NAME)
				&& stringContains(message, "thanks")) {
			//insert

			//insert
			sendMessage(channel, randomIndex(THANKS_MSGS));
	//		sendMessage(channel, "By the way, it's " + Double.toString(fvalue) + "degrees Fahrenheit.");
		}
	}

	protected void onPrivateMessage(String sender, String login,
			String hostname, String message) {
		logService.log(LogService.LOG_INFO, "Received private message from " + sender);
		//blink(base, 1);

		sendMessage(sender, getDirectResponse(message));
	}

	private String getDirectResponse(String message) {
		if (stringContains(message, "log")) {
			java.util.Enumeration e = logReaderService.getLog();
			StringBuffer sb = new StringBuffer();
			while (e.hasMoreElements()) {
				LogEntry le = (LogEntry) e.nextElement();
				sb.append(le.getLevel());
				sb.append(' ');
				sb.append(le.getTime());
				sb.append(' ');
				sb.append(le.getMessage());
				sb.append('\n');
			}

			return sb.toString();
		}

		return getResponse(message);
	}

	private String getResponse(String message) {
		String response = randomIndex(UNKNOWN_MSGS);

		if (stringContains(message, "shakin")) {
			if (lastDetection == 0) {
				response = "I've never seen anyone ever do anything.  Ever.";
			} else {
				long secondsDelta = (System.currentTimeMillis() - lastDetection) / 1000;

				if (secondsDelta < 1) {
					response = "Something just moved.  Srsly.";
				} else if (secondsDelta < 60) {
					response = "It's pretty happening right now man.";
				} else if (secondsDelta < 600) {
					response = "Someone or something manages to bumble around a bit.";
				} else if (secondsDelta < 6000) {
					response = "It's been quiet for a while honestly.";
				} else {
					response = "I'm talking to myself I'm so lonely.";
				}
			}
		} else if (stringContains(message, "uptime")) {
			response = "I've been alive for "
					+ generateTimeMessage(System.currentTimeMillis() - started)
					+ ".";
		} else if (stringContains(message, new String[] { "last", "long",
				"time", "anyone", "anybody", "anything" })) {
			if (lastDetection == 0) {
				response = "An infinite amount of time has passed since I've seen something.  Or zero.  Take your pick.";
				System.out.println("foobar");
			} else {
				response = "Motion detected "
						+ generateTimeMessage(System.currentTimeMillis()
								- lastDetection) + " ago.";
			}
		} else if (stringContains(message, "help")) {
			response = "If you ask me like 'what's shakin' I'll give you the scoop on movement in NYCR-space.  If you ask me 'how long' or 'last time' I'll try to be more specific.";
		} else if (stringContains(message, THANKS_KEYWORDS)) {
			response = randomIndex(THANKS_MSGS);
		} else if (stringContains(message, "spelling")) {
			response = "Spelling mistakes add to the mystique ok?";
		} else if (stringContains(message, FLASH_KEYWORDS)) {
			blink(base, 5);
			response = randomIndex(FLASHED_MSGS);
		} else if (stringContains(message, HOT_KEYWORDS)) {
			blink(base,2);
			response = "It looks like it's " + (int)getTemp() + " degrees Fahrenheit.";
		}

		return response;
	}

	private String generateTimeMessage(long milliDelta) {
		long secondsDelta = (milliDelta) / 1000;
		long minutesDelta = secondsDelta / 60;
		long hoursDelta = minutesDelta / 60;
		long daysDelta = hoursDelta / 24;
		String response;

		if (secondsDelta < 1) {
			response = "this instant";
		} else if (secondsDelta < 60) {
			response = "" + secondsDelta + " seconds";
		} else if (minutesDelta < 60) {
			response = "" + (int) minutesDelta + " minutes";
		} else if (hoursDelta < 24) {
			response = "" + (int) hoursDelta + " hours";
		} else {
			response = "" + (int) daysDelta + " days";
		}

		return response;
	}

	private String randomIndex(String[] unknownMsgs) {
		Random r = new Random();
		return unknownMsgs[r.nextInt(unknownMsgs.length)];
	}

	private boolean stringContains(String source, String[] seeking) {
		for (int i = 0; i < seeking.length; ++i) {
			if (stringContains(source, seeking[i])) {
				return true;
			}
		}

		return false;
	}

	private boolean stringContains(String source, String seeking) {
		return source.toUpperCase().indexOf(seeking.toUpperCase()) > -1;
	}

	public void motionDetected() {
		long currentDetection = System.currentTimeMillis();
		//blink(base,1);
		if (((currentDetection - lastDetection) / 1000) / 60 / 60 > 600) {
			sendMessage(Activator.IRC_CHANNEL,
					"Oh, someone's finally here!  Hi!");
			//blink(base, 1);
		}

		lastDetection = currentDetection;
	}

	private void blink(IBUGBaseControl base2, int times) {
		try {
			for (int i = 0; i < times; i++) {
				for (int j = 0; j < 4; j++) {
					base.setLED(j);
				}

				Thread.sleep(100);

				for (int j = 0; j < 4; j++) {
					base.clearLED(j);
				}

				Thread.sleep(100);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private double getTemp()
	{
		
	int Value = 0;
	// Added by Nick vermeer
	double fvalue;
	
	
	try {
		vhService.writeADC(IVonHippelModuleControl.VH_ADC_W1_EN
				| IVonHippelModuleControl.VH_ADC_W2_EN |IVonHippelModuleControl.VH_ADC_W1_CH0);
		System.out.println("entered vh read branch");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//System.out.println("Wrote "+((IVonHippelModuleControl.VH_ADC_W1_EN | IVonHippelModuleControl.VH_ADC_W1_CH0))+" in java, sleeping 1 sec before read");
	try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	try {
		Value=vhService.readADC();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	// Nick Vermeer's additions here:
	Value=((Value & ~0x800000) >> 6);
	// The "-6" at the end of the next line is a manual calibration step based on a real thermometer next to the lm35 - www
	fvalue=((250.0/65535.0)*(double)Value*(9.0/5.0)+32.0)-6;
	
//	System.out.println("Integer Output ::"+Double.toString(fvalue));
//	System.out.println("ADCTester.jar: ADC Channel 0 data = " + Integer.toHexString(Value)+" "+Integer.toBinaryString(Value));
	try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return fvalue;
	}
	
}
