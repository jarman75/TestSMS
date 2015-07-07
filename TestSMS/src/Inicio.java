import utilSMS.Sms;

public class Inicio {

	public static void main(String[] args) {
		
		if (args.length<3){
			System.out.println("EnvioSMS <puerto> <movil> <mensaje>");
			return;
		}
		
		String Puerto = args[0];
		String Movil = args[1];
		String Mensaje = args[2];
		
		Sms sms = new Sms(Puerto,Movil,Mensaje);
		
		if (!sms.Enviar()){
			System.out.println("Error enviando sms");
		}
				
		System.exit(0);
		


	}

}
