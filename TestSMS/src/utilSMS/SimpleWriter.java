package utilSMS;

import javax.comm.*;
import java.io.*;
import java.util.*;

public class SimpleWriter implements Runnable, SerialPortEventListener {

	private InputStream inputStream;

	public void serialEvent(SerialPortEvent event) {
	    switch (event.getEventType()) {
	        case SerialPortEvent.BI:
	        case SerialPortEvent.OE:
	        case SerialPortEvent.FE:
	        case SerialPortEvent.PE:
	        case SerialPortEvent.CD:
	        case SerialPortEvent.CTS:
	        case SerialPortEvent.DSR:
	        case SerialPortEvent.RI:
	        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
	            break;
	        case SerialPortEvent.DATA_AVAILABLE: {

	            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	            String line = "";
	            try {

	                while ((line = reader.readLine()) != null) {
	                    System.out.println(line);
	                }
	            } catch (IOException e) {
	                System.err.println("Error leyendo Puerto " + e.getMessage());
	            }
	            break;

	        }
	    } //switch
	}

	public SimpleWriter(SerialPort serial) {
	    try {
	        inputStream = serial.getInputStream();
	        try {
	            serial.addEventListener(this);
	        } catch (TooManyListenersException e) {
	            System.out.println("Exception añadiendo Listener" + e.getMessage());
	        }
	        serial.notifyOnDataAvailable(true);

	    } catch (Exception ex) {
	        System.out.println("Exception obteniendo InputStream" + ex.getMessage() );
	    }

	}
	
	public static void showText(String Text) {
	    System.out.println(Text);
	  }

	public void run() {
		
	}

	
}
