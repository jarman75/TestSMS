/*CLASE PARA ENVIO DE SMS, MEDIANTE MODEM GPS/GPRS, CONECTADO A PUERTO SERIE
 * 
 * 
 * */
package utilSMS;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;


public class Sms {

	private String pPuerto;
	private String pMovil;
	private String pMensaje;
	
	public Sms(String Puerto, String Movil, String Mensaje) {
		pPuerto=Puerto;
		pMovil=Movil;
		pMensaje=Mensaje;		
	}
	
	public boolean Enviar() {
	if (!pMovil.contains("+34")){
		pMovil = "+34"+pMovil;
	}	
	
	String messageString = pMensaje.replace("+", " ");	    
    String centromsg ="\"+34607003110\"";  //centro mensajes para vodafone
    String dest = "\"" + pMovil + "\"";  // movil destinatario.
	
    String line1 = "AT+CMGF=1\r\n";   //comando AT para activar centro mensajes
    String line2 = "AT+CSCA=" + centromsg + "\r\n";   //comando AT para indicar telefono centro de mensajes
    String line3 = "AT+CMGS=" + dest + "\r\n";		//comando AT para indicar telefefono destino
    String line4 = "#####" + messageString + "\r\n";	// mensaje formateado
	
	//cargando libreria para uso de puerto serie
    try{
		System.loadLibrary("rxtxSerial");	    	
    	System.out.println("Se ha cargado la libreria nativa correctamente");	    	
    } catch (UnsatisfiedLinkError u) {	    	
    	System.err.println("No se ha encontrado la libreria nativa de puerto serie");
    	return false;
    }

    
    //busca por la lista de puertos activos, tipo Serial
     Enumeration<?> listaPuertos = CommPortIdentifier.getPortIdentifiers();	    
     CommPortIdentifier idPuerto = null;	    
     boolean encontrado = false;	    
     while (listaPuertos.hasMoreElements() && !encontrado) {	    
	     idPuerto = (CommPortIdentifier) listaPuertos.nextElement();	    
	     if (idPuerto.getPortType() == CommPortIdentifier.PORT_SERIAL) {	    
		     if (idPuerto.getName().equals(pPuerto)) {	    
		    	 encontrado = true;	    
		    	 break;
		     }else{
		    	 System.out.println("Puerto encontrado: " + idPuerto.getName());
		     }
		     
	     }	    
     }
     
     //si no se encuentra el puerto solicitado se sale
     if (!encontrado){
    	 System.out.println("no existe puerto " + pPuerto + " en el equipo");
    	 return false;
     }
    
    
     //se abre un SimpleWriter para que escriba los eventos del modem
     SerialPort puertoSerie = null;	     
     try {	     
    	 puertoSerie = (SerialPort)idPuerto.open( "DescripcionPropietario",2000 );
    	 @SuppressWarnings("unused")
		 SimpleWriter wr= new SimpleWriter(puertoSerie);	    	 
     } catch( PortInUseException e ) {	     
    	  System.out.println("Error abriendo el puerto serie "+pPuerto);
    	  return false;
     }
     
     // se configura los parametros de la conexión serie, bits/segundos=9600, bits/datos=8, bits/parada=1, Paridad=ninguna
     try {
    	  	puertoSerie.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    	  	puertoSerie.notifyOnDataAvailable(true);
    	  	System.out.println("Configurado parametros puerto OK."); 

     } catch( UnsupportedCommOperationException e ) {
    		  System.out.println("Error configurando parametros de configuracion");
    		  return false;	    	 
     }
     
    //se abren Streams para la escritura/lectura en puerto serie 
    OutputStream outputStream=null;    
	try {
		outputStream = puertoSerie.getOutputStream();		
		System.out.println("comunicacion activo con puerto "+pPuerto);
	} catch (IOException e1) {			
		System.out.println("Error abriendo comunicacion con puerto "+pPuerto);
		return false;
	}
     
    //se intenta enviar el mensaje al movil 
	try {
         System.out.println("Enviando SMS '" + messageString + "'a numero: " + pMovil);	    	 
         outputStream.write(line1.getBytes());	    	 
         outputStream.write(line2.getBytes());             
         outputStream.write(line3.getBytes());             
         outputStream.write(line4.getBytes());             
         outputStream.write(26);  //contro+Z para terminar mensaje             
         outputStream.flush();             
         System.out.println("SMS enviado OK");
         return true;
     } catch (Exception e) {
         System.out.println("Error enviando SMS " + e.getMessage());
         return false;
     }finally{
    	 puertoSerie.close();
    	 System.out.println("puerto cerrado " + pPuerto);
     }

	}
}
