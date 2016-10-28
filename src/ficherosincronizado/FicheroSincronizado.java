package ficherosincronizado;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FicheroSincronizado {
    
    private static final Logger LOGGER = Logger.getLogger("com.prueba.paula");

    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        File log = new File("mylog.txt");
        PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File("mylog.txt"), true)), true);
        
        FileHandler fh = null;
        RandomAccessFile raf = null;
        FileLock bloqueo = null;
        FileChannel canal = null;
        int numleido;
        int orden=0;

        try
        {
            if (args.length > 0) 
            {
                orden = Integer.parseInt(args[0]);
            }
        }catch(NumberFormatException ex)
        {
            System.out.println("Argumento inválido "+ex.toString());
        }
        
        try
        {
            fh = new FileHandler("MyLogFile.log", true);
            SimpleFormatter formater = new SimpleFormatter();
            fh.setFormatter(formater);
        }catch(IOException ex)
        {
            System.out.println("No se ha podido abrir el fichero de log "+ex.toString());
            System.exit(-1);
        }catch(SecurityException ex)
        {
            System.out.println("No se ha podido abrir el fichero de log "+ex.toString());
            System.exit(-1);
        }
        
        LOGGER.addHandler(fh);
        LOGGER.setLevel(Level.SEVERE);
        
        try 
        {
           System.setOut(ps);
           System.setErr(ps);
        }
        catch(Exception e)
        {
            System.err.println("No se ha redirigido la salida");
        }
        
        try
        {
            raf = new RandomAccessFile("prueba.txt", "rwd");
            System.out.println("Se ha creado el fichero");
        }catch(FileNotFoundException ex)
        {
            System.out.println("No se ha podido crear el fichero "+ex.toString());
        }
        
        try
        {
            canal = raf.getChannel();
            bloqueo = canal.lock();
            LOGGER.log(Level.FINE, "Proceso "+orden+" ha entrado en la Seccion Crítica");
            
        } catch (IOException e) 
        {
            System.out.println("No se ha podido bloquear el fichero "+e.toString());
        }

        try 
        {
            System.out.println("El proceso "+orden+" entra en la Seccion Crítica");
            raf.seek(0);
            
            if (raf.length()==0)
            {
                numleido=0;
            }
            else
            {
                numleido = raf.readInt();
            }
            
            System.out.println("El proceso "+orden+" ha leido el valor : "+numleido);
            
            numleido++;
            
            System.out.println("El proceso "+orden+" incrementa el valor leido y ahora es : "+numleido);
            
            raf.seek(0);
            raf.writeInt(numleido);
            
            System.out.println("El proceso "+orden+" ha salido de la Seccion Crítica");
            
            bloqueo.release();
            canal.close();
            raf.close();
        }catch(FileNotFoundException ex)
        {
            LOGGER.log(Level.SEVERE, "Error de acceso al fichero "+orden);
        }
            
        
        }
    }
